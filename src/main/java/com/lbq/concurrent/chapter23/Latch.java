package com.lbq.concurrent.chapter23;

import java.util.concurrent.TimeUnit;
/**
 * 23.2 无限等待的Latch
 * 首先定义了一个无限等待的抽象类Latch，在Latch抽象类中定义了await方法、countDown方法以及getUnarrived方法，
 * 这些方法的用途代码注释中都有详细介绍，当然在Latch中的limit属性至关重要，当limit降低到0时门阀将会被打开。
 * 
 * 子任务数量达到limit的时候，门阀才能打开，await()方法用于等待所有的子任务完成，如果到达数量未达到limit的时候，
 * 将无限等待下去，当子任务完成的时候调用countDown()方法使计数器减少一个，表明我已经完成任务了，
 * getUnarrived()方法主要用于查询当前还有多少个子任务还没有结束。
 * @author 14378
 *
 */
public abstract class Latch {

	/** 用于控制多少个线程完成任务时才能打开阀门 **/
	protected int limit;
	
	/** 通过构造函数传入limit **/
	public Latch(int limit) {
		this.limit = limit;
	}
	
	/** 该方法会使得当前线程一直等待，直到所有的线程都完成工作，被阻塞的线程是允许被中断的 **/
	public abstract void await() throws InterruptedException;
	
	/** 可超时的抽象方法，其中TimeUnit代表wait的时间单位，而time则是指定数量的时间单位，在该方法中又增加了WaitTimeoutException **/
	public abstract void await(TimeUnit unit, long time) throws InterruptedException, WaitTimeoutException;
	
	/** 当任务线程完成工作之后调用该方法使得计数器减一 **/
	public abstract void countDown();
	
	/** 获取当前还有多少个线程没有完成任务 **/
	public abstract int getUnarrived();
	
}
