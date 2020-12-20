package com.lbq.concurrent.chapter29.eventdriven;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
/**
 * 29.2.2 异步EDA框架设计
 * 在29.2.1节中，我们实现了一个基本的EDA框架，但是这个框架，但是这个框架在应对高并发的情况下还是存在一些问题的，具体如下：
 * 1.EventDispatcher不是线程安全的类，在多线程的情况下，registerChannel方法会引起数据不一致的问题。
 * 2.就目前而言，我们实现的所有Channel都无法并发消费Message，比如InputEventHandler只能逐个处理Message，
 * 低延迟的消息处理还会导致Dispatcher出现积压。
 * 
 * 在本节中，我们将对29.2.1节中的EDA框架进行扩充，使其可支持并发任务的执行，下面定义了一个新的AsyncChannel作为基类，
 * 该类中提供了Message的并发处理能力。
 * 
 * 为了防止子类在继承AsyncChannel基类的时候重写dispatcher方法，用final关键字对其进行修饰(Template Method Design Pattern)，
 * handler方法用于子类对Message进行具体的处理，stop方法则用来停止ExecutorService。
 * @author 14378
 *
 */
public abstract class AsyncChannel implements Channel<Event> {
	//在AsyncChannel中将使用ExecutorService多线程的方式提交给Message
	private final ExecutorService executorService;
	//默认构造函数，提供了CPU的核数*2的线程数量
	public AsyncChannel() {
		this(Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() * 2));
	}
	//用户自定义的ExecutorService
	public AsyncChannel(ExecutorService executorService) {
		this.executorService = executorService;
	}
	//重写dispatcher方法，并且用final修饰，避免子类重写
	@Override
	public final void dispatch(Event message) {
		executorService.submit(() -> this.handle(message));
	}
	//提供抽象方法，供子类实现具体的Message处理
	protected abstract void handle(Event message);
	//提供关闭ExecutorService的方法
	void stop() {
		if(null != executorService && !executorService.isShutdown()) {
			executorService.shutdown();
		}
	}
}
