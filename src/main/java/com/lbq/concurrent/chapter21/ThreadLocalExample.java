package com.lbq.concurrent.chapter21;

import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;
/**
 * 21.3 ThreadLocal详解
 * 自JDK1.2版本起，Java就提供了java.lang.ThreadLocal，ThreadLocal为每一个使用该变量的线程都提供了独立的副本，
 * 可以做到线程间的数据隔离，每一个线程都可以访问各自内部的副本变量。
 * 
 * 21.3.1 ThreadLocal的使用场景及注意事项
 * ThreadLocal在Java的开发中非常常见，一般在以下情况中会使用到ThreadLocal。
 * 1.在进行对象跨层传递的时候，可以考虑使用ThreadLocal，避免方法多次传递，打破层次间的约束。
 * 2.线程间数据隔离，比如21.2节中描述的线程上下文ActionContext。
 * 3.进行事务操作，用于存储线程事务信息。
 * ThreadLocal并不是解决多线程下共享资源的技术，一般情况下，每一个线程的ThreadLocal存储的都是一个全新的对象(通过new关键字创建)，
 * 如果多线程的ThreadLocal存储了一个对象的引用，那么其还是将面临资源竞争，数据不一致等并发问题。
 * 
 * 21.3.2 ThreadLocal的方法详解及源码分析
 * 在使用ThreadLocal的时候，最常用的方法就是initialValue()、set(T t)、get()。
 * (1) initialValue()方法
 * initialValue方法为ThreadLocal要保存的数据类型指定了一个初始化值，在ThreadLocal中默认返回值为null，示例代码如下：
 * //ThreadLocal中initialValue()方法源码
 * protected T initialValue() {
 * 		return null;
 * }
 * 但是我们可以通过重写initialValue()方法进行数据的初始化，如下面的代码所示，
 * 线程并未对threadlocal进行set操作，但是还可以通过get方法得到一个初始值，
 * 通过输出信息也不难看出，每一个线程通过get方法获取的值都不一样的（线程私有的数据拷贝）：
 * 在get和set方法源码分析的时候，我们会看到initialValue()方法何时被调用。
 * 提醒：上述重写initialValue()方法的方式若使用Java8提供的Supplier函数接口会更加简化：
 * ThreadLocal<Object> threadLocal = ThreadLocal.withInitial(Object::new);
 * 
 * (2) set(T t)方法
 * set方法主要是为了ThreadLocal指定将要被存储的数据，如果重写了initialValue()方法，在不调用set(T t)方法的时候，数据的初始值是initialValue()方法的计算结果，
 * 示例代码如下：
 * //ThreadLocal的set方法源码
 * public void set(T value) {
 * 		Thread t = Thread.currentThread();
 * 		ThreadLocalMap map = getMap(t);
 * 		if (map != null) {
 * 			map.set(this, value);
 * 		}else {
 * 			createMap(t, value);
 * 		}
 * }
 * 
 * //ThreadLocal的creatMap方法源码
 * void createMap(Thread t, T firstValue) {
 * 		t.threadLocals = new ThreadLocalMap(this, firstValue);
 * }
 * 
 * //ThreadLocalMap的set方法源码
 * private void set(ThreadLocal<?> key, Object value) {
 * 		Entry[] tab = table;
 * 		int len = tab.length;
 * 		int i = key.threadLocalHashCode & (len-1);
 * 		for(Entry e = tab[i]; e != null; e = tab[i = nextIndex(i, len)]) {
 * 			ThreadLocal<?> k = e.get();
 * 			if(k == key) {
 * 				e.value = value;
 * 				return;
 * 			}
 * 			if(k == null) {
 * 				replaceStaleEntry(key, value, i);
 * 				return;
 * 			}
 * 		}
 * 		tab[i] = new Entry(key, value);
 * 		int sz = ++size;
 * 		if(!cleanSomeSlots(i, sz) && sz >= threshold) {
 * 			rehash();
 * 		}
 * }
 * 上述代码的运行步骤具体如下：
 * 1)获取当前线程Thread.currentThread()。
 * 2)根据当前线程获取与之关联的ThreadLocalMap数据结构。
 * 3)如果map为null则进入第4步，否则进入第5步。
 * 4)当map为null的时候创建一个ThreadLocalMap，用当前ThreadLocal实例作为key，将要存放的数据作为Value，对应到ThreadLocalMap中则是创建了一个Entry。
 * 5)在map的set方法中遍历整个map的Entry，如果发现ThreadLocal相同，则使用新的数据替换即可，set过程结束。
 * 6)在遍历map的entry过程中，如果发现有Entry的key值为null，则直接将其逐出并且使用新的数据占用被逐出数据的位置，这个过程主要是为了防止内存泄漏。
 * 7)创建新的entry，使用ThreadLocal作为key，将要存放的数据作为Value。
 * 8)最后再根据ThreadLocalMap的当前数据元素的大小和阀值做比较，再次进行key为null的数据项清理工作。
 * 
 * (3) get()方法
 * get用于返回当前线程在ThreadLocal中数据备份，当前线程的数据都存放在一个称为ThreadLocalMap的数据结构中，我们稍后会介绍ThreadLocalMap，
 * get方法示例代码如下：
 * //ThreadLocal的get方法源码
 * public T get() {
 * 		Thread t = Thread.currentThread();
 * 		ThreadLocalMap map = getMap(t);
 * 		if(map != null) {
 * 			ThreadLocalMap.Entry e = map.getEntry(this);
 * 			if (e != null) {
 * 				@SuppressWarnings("unchecked")
 * 				T result = (T)e.value;
 * 				return result;
 * 			}
 * 		}
 * 		return setInitialValue();
 * }
 * 
 * //ThreadLocal的setInitialValue方法源码
 * private T setInitialValue() {
 * 		T value = initialValue();
 * 		Thread t = Thread.currentThread();
 * 		ThreadLocalMap map = getMap(t);
 * 		if (map != null) {
 * 			map.set(this, value);
 * 		}else {
 * 			createMap(t, value);
 * 		}
 * 		return value;
 * }
 * 通过上面的源码我们大致分析一下一个数据拷贝的get过程，运行步骤具体如下：
 * 1) 首先获取当前线程Thread.currentThread()方法。
 * 2) 根据Thread获取ThreadLocalMap，其中ThreadLocalMap与Thread是关联的，而我们存入ThreadLocal中数据事实上是存储在ThreadLocalMap的Entry中的。
 * 3) 如果map已经被创建过，则以当前的ThreadLocal作为key值获取对应的Entry。
 * 4) 如果Entry不为null，则直接返回Entry的value值，否则进入第5步。
 * 5) 如果在第2步获取不到对应的ThreadLocalMap，则执行setInitialValue()方法。
 * 6) 在setInitialValue()方法中首先通过执行initialValue()方法获取初始值。
 * 7) 根据当前线程Thread获取对应的ThreadLocalMap。
 * 8) 如果ThreadLocalMap不为null，则为map指定initialValue()所获得的初始化值，实际上是在map.set(this, value)方法中new了一个Entry对象。
 * 9) 如果ThreadLocalMap为null（首次使用的时候），则创建一个ThreadLocalMap，并且与Thread对象的threadlocals属性相关联系（通过这里我们也可以发现ThreadLocalMap的构造过程是一个lazy的方式）。
 * 10) 返回initialValue()方法的结果，当然这个结果在没有被重写的情况下结果为null。
 * 
 * (4)ThreadLocalMap
 * 无论是get方法还是set方法都不可避免地要与ThreadLocalMap和Entry打交道，ThreadLocalMap是一个完全类似于HashMap的数据结构，
 * 仅仅用于存放线程存放在ThreadLocal中的数据备份，ThreadLocalMap的所有方法对外部都完全不可见。
 * 在ThreadLocalMap中用于存储数据的是Entry，它是一个WeakReference类型的子类，之所以被设计成WeakReference是为了能够在JVM发生垃圾回收事件时，能够自动回收防止内存溢出的情况出现，
 * 通过Entry源码分析不难发现，在Entry中会存储ThreadLocal以及所需要数据的备份。ThreadLocalMap的Entry源码如下：
 * //ThreadLocalMap的Entry源码
 * static class Entry extends WeakReference<ThreadLocal<?>> {
 * 		//The value associated with this ThreadLocal.
 * 		Object value;
 * 		Entry(ThreadLocal<?> k, Object v) {
 * 			super(k);
 * 			value = v;
 * 		}
 * }
 * @author 14378
 *
 */
public class ThreadLocalExample {

	public static void main(String[] args) {
//		//创建ThreadLocal实例
//		ThreadLocal<Integer> tlocal = new ThreadLocal<>();
//		//创建十个线程，使用tlocal
//		IntStream.range(0, 10).forEach(i -> new Thread(() -> {
//			try {
//				//每个线程都会设置tlocal，但是彼此之间的数据是独立的
//				tlocal.set(i);
//				System.out.println(Thread.currentThread() + " set i " + tlocal.get());
//				TimeUnit.SECONDS.sleep(1);
//				System.out.println(Thread.currentThread() + " get i " + tlocal.get());
//			} catch (InterruptedException e) {
//				e.printStackTrace();
//			}
//		}).start());
		System.out.println("--------------------------------------------------------------------------");
//		ThreadLocal<Object> threadLocal = new ThreadLocal<Object>() {
//			@Override
//			protected Object initialValue() {
//				return new Object();
//			}
//		};
		ThreadLocal<Object> threadLocal = ThreadLocal.withInitial(Object::new);
		new Thread(() -> System.out.println(threadLocal.get())).start();
		System.out.println(threadLocal.get());
	}

}
