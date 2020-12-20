package com.lbq.concurrent.chapter02;
/**
 * 守护线程是一类比较特殊的线程，一般用于处理一些后台的工作，比如jdk的垃圾回收线程，
 * 什么是守护线程？为什么要有守护线程，以及何时需要守护线程？
 * 
 * The Java Virtual Machine exits when the only threads running are all deamon threads.
 * 在正常情况下，若jvm中没有一个非守护线程，则jvm的进程会退出。
 * 
 * 设置守护线程的方法很简单，调用setDaemon方法即可，true代表守护线程，false代表正常线程。
 * 线程是否为守护线程和它的父线程有很大的关系，如果父线程四正常线程，则子线程也是正常线程，反之亦然，
 * 如果你想修改它的特性则可以借助setDaemon方法。isDaemon方法可以判断该线程是不是守护线程。
 * 另外需要注意的就是，setDaemon方法只在线程启动之前才能生效，如果一个线程已经死亡，
 * 那么再设置setDaemon则会抛出IllegalThreadStateException异常。
 * 
 * 守护线程经常用作与执行一些后台任务，因此有时它也被称为后台线程，当你希望关闭某些线程的时候，或者退出jvm进程的时候，
 * 一些线程能够自动关闭，此时就可以考虑用守护线程为你完成这样的工作。
 * @author 14378
 *
 */
public class DaemonThread {

	public static void main(String[] args) throws InterruptedException {
		Thread thread = new Thread(() -> {
			while(true) {
				try {
					Thread.sleep(1);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}) ;
		//thread.setDaemon(true);//将thread设置为守护线程
		
		thread.start();
		Thread.sleep(2000L);
		System.out.println("Main thread finished lifecycle.");
	}

}
