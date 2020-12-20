package com.lbq.concurrent.chapter04;

//import java.util.concurrent.TimeUnit;
/**
 * 什么是共享资源？
 * 共享资源指的是多个线程同时对同一份资源进行访问（读写操作），被多个线程访问的资源就称为共享资源，
 * 如何保证多个线程访问到的数据是一致的，则被称为数据同步或者资源同步。
 * 
 * 多次运行下面程序，每次都会有不一样的发现，但是总结起来主要有三个问题，具体如下：
 * 第一，某个号码被略过没有出现。
 * 第二，某个号码被多次显示。
 * 第三，号码超过了最大值500。
 * 
 * 数据不一致问题原因分析
 * 1.号码被略过
 * 线程的执行是由cpu时间片轮询调度的，假设此时线程1和2都执行到了index=65的位置，
 * 其中线程2将index修改为66之后未输出之前，CPU调度器将执行权利交给了线程1，
 * 线程1直接将其累加到了67，那么66就被忽略了。
 * 
 * 2.号码重复出现
 * 线程1执行index+1，然后CPU执行权落入线程2手里，由于线程1并没有给index赋予计算后的结果393，
 * 因此线程2执行index+1的结果仍然是393，所以会出现重复号码的情况。
 * 
 * 3.号码超过了最大值
 * 当index=499的时候，线程1和线程2都看到条件满足，线程2短暂停顿，线程1将index增加到了500，
 * 线程2恢复运行后又将500增加到了501，此时就出现了超过最大值的情况。
 * @author 14378
 *
 * 上述出现的几个问题，究其原因就是因为多个线程对index变量(共享变量/资源)同时操作引起的，在jdk1.5版本以前，要解决这个问题需要使用synchronized关键字，
 * synchronized提供了一种排他机制，也就是在同一时间只能由一个线程执行某些操作。
 *
 * synchronized关键字可以实现一个简单的策略来防止线程干扰和内存一致性错误，
 * 如果一个对象对多个线程是可见的，那么对该对象的所有读或者写都将通过同步的方式来进行，
 * 具体表现如下：
 * 1.synchronized关键字提供了一种锁的机制，能够确保共享变量的互斥访问，从而防止数据不一致问题的出现。
 * 2.synchronized关键字包括monitor enter和monitor exit两个jvm指令，
 * 它能够保证在任何时候任何线程执行到monitor enter成功之前都必须从主内存中获取数据，而不是从缓存中，
 * 在monitor exit运行成功之后，共享变量被更新后的值必须刷入主内存。
 * 3.synchronized的指令严格遵守Java happens-before规则，一个monitor exit指令之前必定要有一个monitor enter。
 * 
 * synchronized可以用于对代码块或方法进行修饰，而不能够用于对class以及变量进行修饰。
 */
public class TicketWindowRunnable implements Runnable {

	private int index = 1;
	
	private final static int MAX = 500;
	
	private final static Object MUTEX = new Object();
	
	@Override
	public void run() {
		synchronized (MUTEX) {
			while(index <= MAX) {
//				try {
//					TimeUnit.SECONDS.sleep(1);
//				} catch (InterruptedException e) {
//					e.printStackTrace();
//				}
				System.out.println(Thread.currentThread() + " 的号码是：" + (index++));
			}
		}
		
	}

	public static void main(String[] args) {
		final TicketWindowRunnable task = new TicketWindowRunnable();
		
		Thread windowThread1 = new Thread(task, "一号窗口");
		Thread windowThread2 = new Thread(task, "二号窗口");
		Thread windowThread3 = new Thread(task, "三号窗口");
		Thread windowThread4 = new Thread(task, "四号窗口");
		
		windowThread1.start();
		windowThread2.start();
		windowThread3.start();
		windowThread4.start();
	}
}
