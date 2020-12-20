package com.lbq.concurrent.chapter06;

import java.util.concurrent.TimeUnit;
/**
 * 线程可以设置为守护线程，ThreadGroup也可以设置为守护ThreadGroup，但是若将一个ThreadGroup设置为daemon，也并不会影响线程的daemon属性，
 * 如果一个ThreadGroup的daemon被设置为true，那么在group中没有任何active线程的时候该group将自动destroy。
 * @author 14378
 *
 */
public class ThreadGroupDaemon {

	public static void main(String[] args) throws InterruptedException {
		ThreadGroup group1 = new ThreadGroup("Group1");
		new Thread(group1, () -> {
			try {
				TimeUnit.SECONDS.sleep(1);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}, "group1-thread1").start();

		ThreadGroup group2 = new ThreadGroup("Group2");	
		new Thread(group2, () -> {
			try {
				TimeUnit.SECONDS.sleep(1);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}, "group2-thread1").start();	
		group2.setDaemon(true);
		
		TimeUnit.SECONDS.sleep(5);
		System.out.println(group1.isDestroyed());//false
		System.out.println(group2.isDestroyed());//true
	}

}
