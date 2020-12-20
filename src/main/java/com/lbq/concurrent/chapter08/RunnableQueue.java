package com.lbq.concurrent.chapter08;
/**
 * RunnableQueue主要用于存放提交的Runnable，该Runnable是一个BlockedQueue，并且有limit的限制。
 * 任务队列，主要用于缓存提交到线程池中的任务
 * @author 14378
 *
 */
public interface RunnableQueue {

	//当有新的任务进来时，首先会offer到队列中
	void offer(Runnable runnable);
	
	//工作线程通过take方法获取Runnable
	Runnable take() throws InterruptedException;
	
	//获取任务队列中任务的数量
	int size();
}
