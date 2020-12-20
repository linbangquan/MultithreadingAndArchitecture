package com.lbq.concurrent.chapter23;

import java.util.concurrent.TimeUnit;
/**
 * Latch设计模式
 * 23.1 什么是Latch
 * Latch(门阀)设计模式，该模式指定了一个屏障，只有所有的条件达到满足的时候，门阀才能打开。
 * 
 * 23.3 本章总结
 * Latch设计模式提供了等待所有子任务完成，然后继续接下来工作的一种设计方式，自jdk1.5起也提供了CountDownLatch的工具类，
 * 其作用与我们创建的并无两样，当await超时的时候，已完成任务的线程自然正常结束，但是未完成的则不会被中断还会继续执行下去，
 * 也就是说CountDownLatch只提供了门阀的功能，并不负责对线程的管理控制，对线程的控制还需要程序员自己控制。
 * 
 * Latch的作用是为了等待所有子任务完成后再执行其他任务，因此可以对Latch进行再次的扩展，增加回调接口用于运行所有子任务完成后的其他任务。
 * @author 14378
 *
 */
public class Test {
	
	public static void main(String[] args) throws InterruptedException {
		//定义Latch，limit为4
		Latch latch = new CountDownLatch(4);
		new ProgrammerTravel(latch, "Alex", "Bus").start();
		new ProgrammerTravel(latch, "Gavin", "Walking").start();
		new ProgrammerTravel(latch, "Jack", "Subway").start();
		new ProgrammerTravel(latch, "Dillon", "Bicycle").start();
		//当前线程（main线程进入阻塞，直到四个程序员全部都到达目的地）
//		latch.await();	
//		System.out.println("== all of programmer arrived ==");
		
		try {
			latch.await(TimeUnit.SECONDS, 10);
			System.out.println("== all of programmer arrived ==");
		} catch (WaitTimeoutException e) {
			e.printStackTrace();
		}
	}
}
