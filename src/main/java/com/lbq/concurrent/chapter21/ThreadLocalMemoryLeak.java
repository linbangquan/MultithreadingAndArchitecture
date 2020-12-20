package com.lbq.concurrent.chapter21;

import java.util.concurrent.TimeUnit;
/**
 * 21.3.3 ThreadLocal的内存泄漏问题分析
 * 在21.2节中实现的线程上下文和ThreadLocal非常类似，都是使用当前线程作为一个map的Key值用于线程间数据的隔离，
 * 但是21.3节中存在内存泄漏的隐患，比如某个线程结束了生命周期，但是Thread的实例和所要存储的数据还存在于contexts中，
 * 随着运行时间的不断增大（比如几个月、半年甚至更久的时间），在contexts中将会残留很多thread实例以及被保存的数据。
 * 这个问题在ThreadLocal中也是存在的，尤其是在早些的JDK版本中，ThreadLocalMap完全是由HashMap来充当的，
 * 在最新的JDK版本中，ThreadLocal为解决内存泄漏做了很多工作，我们一起通过源码分析结合实战练习来研究下。
 * 1.WeakReference在JVM中触发任意GC(young gc、full gc)时都会导致Entry的回收。
 * 2.在get数据时增加检查，清除已经被垃圾回收器回收的Entry(Weak Reference可自动回收)。
 * ThreadLocalMap的源码代码片段如下：
 * //ThreadLocalMap的源码片段
 * private Entry getEntryAfterMiss(ThreadLocal<?> key, int i, Entry e) {
 * 		Entry[] tab = table;
 * 		int len = tab.length;
 * 		//查找key为null的Entry
 * 		while(e != null) {
 * 			ThreadLocal<?> k = e.get();
 * 			if (k == key) {
 * 				return e;
 * 			}
 * 			if (k == null) {
 * 				expungeStaleEntry(i);
 * 			}else {
 * 				i = nextIndex(i, len);
 * 			}
 * 			e = tab[i];
 * 		}
 * 		return null;
 * }
 * private boolean cleanSomeSlots(int i, int n) {
 * 		boolean removed = false;
 * 		Entry[] tab = table;
 * 		int len = tab.length;
 * 		//查找key为null的Entry
 * 		do {
 * 			i = nextIndex(i, len);
 * 			Entry e = tab[i];
 * 			if(e != null && e.get() == null) {
 * 				n = len;
 * 				removed = true;
 *				//将key为null的Entry删除
 * 				i = expungeStaleEntry(i);
 * 			}
 * 		} while ((n >>>= 1) != 0);
 * 		return removed;
 * }
 * 
 * //执行Entry在ThreadLocalMap中的删除动作
 * private int expungeStaleEntry(int staleSlot) {
 * 		Entry[] tab = table;
 * 		int len = tab.length;
 * 		//expunge entry at staleSlot
 * 		tab[staleSlot].value = null;
 * 		tab[staleSlot] = null;
 * 		size--;
 * 		//Rehash until we encounter null
 * 		Entry e;
 * 		int i;
 * 		for (i = nextIndex(staleSlot, len); (e = tab[i]) != null; i = nextIndex(i, len)) {
 * 			ThreadLocal<?> k = e.get();
 * 			if(k == null) {
 * 				e.value = null;
 * 				tab[i] = null;
 * 				size--;
 * 			}else {
 * 				int h = k.threadLocalHashCode & (len - 1);
 * 				if(h != i) {
 * 					tab[i] = null;
 * 					while (tab[h] != null) {
 * 						h = nextIndex(h, len);
 * 					}
 * 					tab[h] = e;
 * 				}
 * 			}
 * 		} 
 * 		return i;
 * }
 * 3.在set数据时增加检查，删除已经被垃圾回收器清除的Entry，并且将其移除，代码如下：
 * //ThreadLocalMap的源码片段
 * private boolean cleanSomeSlots(int i, int n) {
 *		boolean removed = false;
 *		Entry[] tab = table;
 *		int len = tab.length;
 *		//查找key为null的Entry
 *		do {
 *			i = nextIndex(i, len);
 *			Entry e = tab[i];
 *			if (e != null && e.get() == null) {
 *				n = len;
 *				removed = true;
 *				//将key为null的Entry删除
 *				i = expungeStaleEntry(i);
 *			}
 *		} while((n >>>= 1) != 0);
 *		return removed; 
 * }
 * 
 * 通过以上这三点的分析，ThreadLocal可以在一定程度上保证不发生内存泄漏，我们来看看如下的代码：
 * 该代码首先定义了一个ThreadLocal<byte[]>分别设置了100MB的数据(最终存储于threadLocal中的数据以最后一次set为主)，
 * 然后将threadLocal设置为null，最后手动进行一次full gc，查看内存的变化情况。
 * 借助于VisualVM工具对JVM的进程进行监控我们发现，堆内存的大小维持在了100MB以上的水准，远远高于应该有的数值，
 * 无论进行多少次强制GC，最后100MB的堆内存都不会得到释放，根据21.3.2节中对ThreadLocal源码的分析，
 * 我们来梳理一下ThreadLocal中对象的引用链。
 * 当Thread和ThreadLocal发生绑定之后，关键对象引用链如图21-3所示，与我们在源代码分析中的情况是一致的，
 * 将ThreadLocal Ref显式地指定为null时，引用关系链就变成了如图21-4所示的情况。
 * 当ThreadLocal被显式地指定为null之后，执行GC操作，此时堆内存中的ThreadLocal被回收，同时ThreadLocalMap中的Entry.key也成为null，
 * 但是value将不会被释放，除非当前线程已经结束了生命周期的Thread引用被垃圾回收器回收。
 * 
 * 内存泄漏和内存溢出是有区别的，内存泄漏是导致内存溢出的原因之一，但两者并不是完全等价的，内存泄漏更多的是程序中不再持有某个对象的引用，
 * 但是该对象仍然无法被垃圾回收器回收，究其原因是因为该对象到引用根Root的链路是可达的，比如Thread Ref到Entry.Value的引用链路。
 * @author 14378
 *
 */
public class ThreadLocalMemoryLeak {

	public static void main(String[] args) throws InterruptedException {
		ThreadLocal<byte[]> threadLocal = new ThreadLocal<>();
		TimeUnit.SECONDS.sleep(30);
		threadLocal.set(new byte[1024 * 1024 * 100]); //100MB
		threadLocal.set(new byte[1024 * 1024 * 100]); //100MB
		threadLocal.set(new byte[1024 * 1024 * 100]); //100MB
		threadLocal = null;
		Thread.currentThread().join();
	}

}
