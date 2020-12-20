package com.lbq.concurrent.chapter28;

import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
/**
 * 运行下面的程序，然后在子目录G:\\monitor下不断地创建、删除、修改文件，这些事件都将被收集并且提交给EventBus。
 * 在Apache的Flume框架中提供了Spooling source功能，其内部使用的就是WatchService。
 * 
 * 28.3 本章总结
 * 在本章中，我们实现了一个EventBus模式的小框架，EventBus有点类似于GoF设计模式中的监听者模式，但是EventBus提供的功能更加强大，
 * 使用起来也更加灵活，EventBus中的Subscriber不需要继承任何类或者实现任何接口，在使用EventBus时只是需要持有Bus的引用即可。
 * 
 * 在EventBus的设计中有三个非常重要的角色(Bus、Registry和Dispatcher)，
 * Bus主要提供给外部使用的操作方法，
 * Registry注册表用来整理记录所有注册在EventBus上的Subscriber，
 * Dispatcher主要负责对Subscriber消息进行推送(用反射的方式执行方法)，
 * 但是考虑到程序的灵活性，Dispatcher方法中又提供了Executor的多态方式。
 * @author 14378
 *
 */
public class TestDirectoryTargetMonitor {

	public static void main(String[] args) throws Exception {
		ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() * 2);
		final EventBus eventBus = new AsyncEventBus(executor);
		eventBus.register(new FileChangeListener());
		DirectoryTargetMoitor monitor = new DirectoryTargetMoitor(eventBus, "G:\\monitor");
		monitor.startMonitor();
	}

}
