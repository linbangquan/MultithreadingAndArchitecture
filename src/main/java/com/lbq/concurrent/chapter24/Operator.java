package com.lbq.concurrent.chapter24;

import com.lbq.concurrent.chapter08.BasicThreadPool;
import com.lbq.concurrent.chapter08.ThreadPool;
/**
 * Operator代表了接线员，当有电话打进来时，话务员会将客户的请求封装成一个工单Request，然后开辟一个线程（工作人员）去处理。
 * 
 * 截至目前，我们完成了关于Thread-Per-Message的设计，但是这种设计方式存在着很严重的问题，
 * 经过第2章的学习，我们知道每一个JVM中可创建的线程数量是有限的，针对每一个任务都创建一个新的线程，
 * 假如每一个线程执行的时间比较长，那么在某个时刻JVM会由于无法再创建新的线程而导致栈内存的溢出；
 * 再假如每一个任务的执行时间都比较短，频繁地创建销毁线程对系统性能的开销也是一个不小的影响。
 * 
 * 这种处理方式虽然有很多问题，但不代表其就一无是处了，其实它也有自己的使用场景。
 * 比如在基于Event的编程模型中，当系统初始化事件发生时，需要进行若干资源的后台加载，
 * 由于系统初始化时的任务数量并不多，可以考虑使用该模式响应初始化Event，或者系统在关闭时，
 * 进行资源回收也可以考虑将销毁事件触发的动作交给该模式。
 * 
 * 我们可以将call方法中的创建新线程的方式交给线程池去处理，这样可以避免线程频繁创建和销毁带来的系统开销，还能将线程数量控制在一个可控制的范围之内。
 * 
 * @author 14378
 *
 */
public class Operator {
	//使用线程池替代为每一个请求创建线程
	private final ThreadPool threadPool = new BasicThreadPool(2, 6, 4, 1000);
	public void call(String business) {
		//为每一个请求创建一个线程去处理
		TaskHandler taskHandler = new TaskHandler(new Request(business));
		//new Thread(taskHandler).start();
		threadPool.execute(taskHandler);
	}
}
