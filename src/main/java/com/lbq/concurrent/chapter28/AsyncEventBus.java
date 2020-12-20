package com.lbq.concurrent.chapter28;

import java.util.concurrent.ThreadPoolExecutor;
/**
 * 28.1.3 异步EventBus详解
 * 异步的EventBus比较简单，继承自同步Bus，然后将Thread-Per-Message用异步处理任务的Executor替换EventBus中的同步Executor即可。
 * 
 * 下述代码重写了父类的EventBus的构造函数，使用ThreadPoolExecutor替换Executor。
 * @author 14378
 *
 */
public class AsyncEventBus extends EventBus {

	AsyncEventBus(String busName, EventExceptionHandler exceptionHandler, ThreadPoolExecutor executor){
		super(busName, exceptionHandler, executor);
	}
	
	public AsyncEventBus(String busName, ThreadPoolExecutor executor) {
		this(busName, null, executor);
	}
	
	public AsyncEventBus(ThreadPoolExecutor executor) {
		this("default-async", null, executor);
	}
	
	public AsyncEventBus(EventExceptionHandler exceptionHandler, ThreadPoolExecutor executor) {
		this("default-async", exceptionHandler, executor);
	}
}
