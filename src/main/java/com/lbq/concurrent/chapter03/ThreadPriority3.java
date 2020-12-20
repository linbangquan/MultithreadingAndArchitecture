package com.lbq.concurrent.chapter03;
/**
 * 一般情况下，不会对线程设定优先级别，更不会让某些业务严重地依赖线程的优先级别，比如权重，借助优先级设定某个任务的权重，这种方式是不可取的，
 * 一般定义线程的时候使用默认的优先级就好了，那么线程默认的优先级是多少呢?
 * 线程默认的优先级和它的父类保持一致，一般情况下都是5，因为main线程的优先级就是5，所以它派生出来的线程都是5.
 * @author 14378
 *
 */
public class ThreadPriority3 {

	public static void main(String[] args) {
		Thread t1 = new Thread();
		System.out.println("t1 priority " + t1.getPriority());
		
		Thread t2 = new Thread(() -> {
			Thread t3 = new Thread();
			System.out.println("t3 priority " + t3.getPriority());
		});
		
		t2.setPriority(6);
		t2.start();
		System.out.println("t2 priority " + t2.getPriority());
	}
}
