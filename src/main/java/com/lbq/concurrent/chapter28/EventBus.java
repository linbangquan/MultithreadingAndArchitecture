package com.lbq.concurrent.chapter28;

import java.util.concurrent.Executor;
/**
 * 28.1.2 同步EventBus详解
 * 同步EventBus是最核心的一个类，它实现了Bus的所有功能，但是该类对Event的广播推送采用的是同步的方式，
 * 如果想要使用异步的方式进行推送，可使用EventBus的子类AsyncEventBus。
 * 在下述代码中：
 * 1.EventBus的构造除了名称之外，还需要由ExceptionHandler和Executor，后两个主要是给Dispatcher使用的。
 * 2.registry和unregister都是通过Subscripter注册表来完成的。
 * 3.Event的提交则是由Dispatcher来完成的。
 * 4.Executor并没有使用我们在第8章中开发的线程池，而是使用JDK中的Executor接口，我们自己开发的ThreadPool天生就是多线程并发执行任务的线程池，
 * 自带异步处理能力，但是无法做到同步任务处理，因此我们使用Executor可以任意扩展同步、异步的处理方式。
 * @author 14378
 *
 */
public class EventBus implements Bus {
	//用于维护Subscriber的注册表
	private final Registry registry = new Registry();
	//Event Bus的名字
	private String busName;
	//默认的Event Bus的名字
	private final static String DEFAULT_BUS_NAME = "default";
	//默认的topic的名字
	private final static String DEFAULT_TOPIC = "default-topic";
	//用于分发广播消息到各个Subscriber的类
	private final Dispatcher dispatcher;
	
	public EventBus() {
		this(DEFAULT_BUS_NAME, null, Dispatcher.SEQ_EXECUTOR_SERVICE);
	}
	
	public EventBus(String busName) {
		this(busName, null, Dispatcher.SEQ_EXECUTOR_SERVICE);
	}
	
	EventBus(String busName, EventExceptionHandler exceptionHandler, Executor executor){
		this.busName = busName;
		this.dispatcher = Dispatcher.newDispatcher(exceptionHandler, executor);
	}
	
	public EventBus(EventExceptionHandler exceptionHandler) {
		this(DEFAULT_BUS_NAME, exceptionHandler, Dispatcher.SEQ_EXECUTOR_SERVICE);
	}
	/**
	 * 将注册Subscriber的动作直接委托给Registry
	 */
	@Override
	public void register(Object subscriber) {
		this.registry.bind(subscriber);
		
	}
	/**
	 * 取消注册通用委托给Registry
	 */
	@Override
	public void unregister(Object subscriber) {
		this.registry.unbind(subscriber);
		
	}
	/**
	 * 提交Event到默认的topic
	 */
	@Override
	public void post(Object event) {
		this.post(event, DEFAULT_TOPIC);
		
	}
	/**
	 * 提交Event到指定的topic，具体的动作是由Dispatcher来完成的
	 */
	@Override
	public void post(Object event, String topic) {
		this.dispatcher.dispatch(this, registry, event, topic);
		
	}
	/**
	 * 关闭销毁Bus
	 */
	@Override
	public void close() {
		this.dispatcher.close();
		
	}
	/**
	 * 返回Bus的名称
	 */
	@Override
	public String getBusName() {
		return this.busName;
	}

}
