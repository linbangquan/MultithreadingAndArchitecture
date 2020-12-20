package com.lbq.concurrent.chapter04;

import java.util.concurrent.TimeUnit;
/**
 * synchronized关键字提供了一种互斥机制，也就是说在同一时刻，只能有一个线程访问同步资源，
 * 很多资源、书籍将synchronized(mutex)称为锁，其实这种说法是不严谨的，
 * 准确地将应该是某线程获取了与mutex关联的monitor锁。
 * 
 * 随便选中程序中创建的某个线程，会发现只有一个线程在TIMED_WAITING(sleeping)状态，其他线程都进入了BLOCKED状态。
 * 
 * 使用jstack命令打印进程的线程堆栈信息，选取其中几处关键的地方对其进行分析。
 * Thread-0持有monitor<0x00000000d7db1040>的锁并且处于休眠状态中，那么其他线程将会无法进入accessResource方法。
 * Thread-1线程进入BLOCKED状态并且等待着获取monitor<0x00000000d7db1040>的锁，其他的几个线程同样也是BLOCKED状态。
 * 
 * 使用jdk命令javap对Mutex class 进行反汇编，输出了大量的jvm指令，在这些指令中，你将发现monitor enter和monitor exit是成对出现的，
 * （有些时候会出现一个monitor enter多个monitor exit，但是每个monitor exit之前必有对应的monitor enter，这是肯定的）。
 * 
 * 选取其中的片段，进行重点分析。①获取到mutex引用，然后执行②monitorenter JVM指令，休眠结束之后goto至③monitorexit的位置
 * （astore_<n>存储引用至本地变量表；aload_<n>从本地变量表加载引用；getstatic从class中获得静态属性）：
 * public void accessResource();
    Code:
       0: getstatic     #2                  // Field MUTEX:Ljava/lang/Object;	①获取MUTEX
       3: dup
       4: astore_1
       5: monitorenter						//②执行monitorenter JVM指令
       6: getstatic     #3                  // Field java/util/concurrent/TimeUnit.MINUTES:Ljava/util/concurrent/TimeUnit;
       9: ldc2_w        #4                  // long 10l
      12: invokevirtual #6                  // Method java/util/concurrent/TimeUnit.sleep:(J)V
      15: goto          23					//③跳转到23行
      18: astore_2
      19: aload_2
      20: invokevirtual #8                  // Method java/lang/InterruptedException.printStackTrace:()V
      23: aload_1							//④
      24: monitorexit						//⑤执行monitor exit JVM指令
      25: goto          33
      28: astore_3
      29: aload_1
      30: monitorexit
      31: aload_3
      32: athrow
      33: return
 * 一.Monitorenter
 * 每个对象都与一个monitor相关联，一个monitor的lock的锁只能被一个线程在同一时间获取，在一个线程尝试获得与对象关联monitor的所有权时会发生如下的几件事情。
 * 1.如果monitor的计数器为0，则意味着该monitor的lock还没有被获得，某个线程获得之后将立即对该计数器加一，从此该线程就是这个monitor的所有者了。
 * 2.如果一个已经拥有该monitor所有权的线程重入，则会导致monitor的计数器再次累加。
 * 3.如果monitor已经被其他线程所拥有，则其他线程尝试获取该monitor的所有权时，会被陷入阻塞状态直到monitor计数器变为0，才能再次尝试获取对monitor的所有权。
 * 二、Monitorexit
 * 释放对monitor的所有权，想要释放对某个对象关联的monitor的所有权的前提是，你已经获得了所有权。
 * 释放所有权的过程比较简单，就是将monitor的计数器减一，如果计数器的结果为0，那就意味着该线程不再拥有对该monitor的所有权，通俗地将就是解锁。
 * 与此同时被该monitor block的线程将再次尝试获得对该monitor的所有权。
 * 
 * 使用synchronized需要注意的问题
 * 1.与monitor关联的对象不能为null
 * 2.synchronized作用域太大
 * 3.不同的monitor企图锁相同的方法
 * 4.多个锁的交叉导致死锁
 * @author 14378
 *
 */
public class Mutex {

	private final static Object MUTEX = new Object();
	//private final static Object MUTEX = null;//java.lang.NullPointerException
	
	public void accessResource() {
		synchronized(MUTEX) {
			try {
				TimeUnit.MINUTES.sleep(10);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	public static void main(String[] args) {
		final Mutex mutex = new Mutex();
		for(int i = 0; i < 5; i++) {
			new Thread(mutex::accessResource).start();
		}
	}
}

//		"Thread-4" #15 prio=5 os_prio=0 tid=0x000000001aa97000 nid=0x26d0 waiting for monitor entry [0x000000001b78f000]
//		   java.lang.Thread.State: BLOCKED (on object monitor)
//			at com.lbq.concurrent.chapter04.Mutex.accessResource(Mutex.java:12)
//			- waiting to lock <0x00000000d7db1040> (a java.lang.Object)
//			at com.lbq.concurrent.chapter04.Mutex$$Lambda$1/531885035.run(Unknown Source)
//			at java.lang.Thread.run(Thread.java:748)
//
//		"Thread-3" #14 prio=5 os_prio=0 tid=0x000000001aa96800 nid=0x3188 waiting for monitor entry [0x000000001b68f000]
//		   java.lang.Thread.State: BLOCKED (on object monitor)
//			at com.lbq.concurrent.chapter04.Mutex.accessResource(Mutex.java:12)
//			- waiting to lock <0x00000000d7db1040> (a java.lang.Object)
//			at com.lbq.concurrent.chapter04.Mutex$$Lambda$1/531885035.run(Unknown Source)
//			at java.lang.Thread.run(Thread.java:748)
//
//		"Thread-2" #13 prio=5 os_prio=0 tid=0x000000001aa94000 nid=0x2c0c waiting for monitor entry [0x000000001b58f000]
//		   java.lang.Thread.State: BLOCKED (on object monitor)
//			at com.lbq.concurrent.chapter04.Mutex.accessResource(Mutex.java:12)
//			- waiting to lock <0x00000000d7db1040> (a java.lang.Object)
//			at com.lbq.concurrent.chapter04.Mutex$$Lambda$1/531885035.run(Unknown Source)
//			at java.lang.Thread.run(Thread.java:748)
//
//		"Thread-1" #12 prio=5 os_prio=0 tid=0x000000001aa91000 nid=0x325c waiting for monitor entry [0x000000001b48f000]
//		   java.lang.Thread.State: BLOCKED (on object monitor)
//			at com.lbq.concurrent.chapter04.Mutex.accessResource(Mutex.java:12)
//			- waiting to lock <0x00000000d7db1040> (a java.lang.Object)
//			at com.lbq.concurrent.chapter04.Mutex$$Lambda$1/531885035.run(Unknown Source)
//			at java.lang.Thread.run(Thread.java:748)
//
//		"Thread-0" #11 prio=5 os_prio=0 tid=0x000000001aa90800 nid=0xdd0 waiting on condition [0x000000001b38e000]
//		   java.lang.Thread.State: TIMED_WAITING (sleeping)
//			at java.lang.Thread.sleep(Native Method)
//			at java.lang.Thread.sleep(Thread.java:340)
//			at java.util.concurrent.TimeUnit.sleep(TimeUnit.java:386)
//			at com.lbq.concurrent.chapter04.Mutex.accessResource(Mutex.java:12)
//			- locked <0x00000000d7db1040> (a java.lang.Object)
//			at com.lbq.concurrent.chapter04.Mutex$$Lambda$1/531885035.run(Unknown Source)
//			at java.lang.Thread.run(Thread.java:748)
