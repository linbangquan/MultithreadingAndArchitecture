package com.lbq.concurrent.chapter21;

import java.util.concurrent.ConcurrentHashMap;
/**
 * 21.2 线程上下文设计
 * 在有些时候，单个线程执行的任务步骤会非常多，后一个步骤的输入有可能是前一个步骤的输出，比如在单个线程多步骤(阶段)执行时，
 * 为了使得功能单一，有时候我们会采用GoF职责链设计模式。
 * 虽然有些时候后一个步骤未必会需要前一个步骤的输出结果，但是都需要将context从头到尾进行传递，假如方法参数比较少还可以容忍，
 * 如果方法参数比较多，在七八次的调用甚至十几次的调用，都需要从头到尾地传递context，很显然这是一种比较烦琐的设计，
 * 那么我们就可以尝试采用线程的上下文设计来解决这样的问题。
 * 我们在ApplicationContext中增加ActionContext(线程上下文)相关的内容，代码如下：
 * 不同的线程访问getActionContext()方法，每个线程都将会获得不一样的ActionContext实例，
 * 原因是我们采用Thread.currentThread()作为contexts的key值，这样就可以保证线程之间上下文的独立性，
 * 同时也不用考虑ActionContext的线程安全性(因为始终只有一个线程访问ActionContext)，因此线程上下文又被称为“线程级别的单例”。
 * 
 * 注意：
 * 通过这种方式定义线程上下文很可能会导致内存泄漏，contexts是一个Map的数据结构，用当前线程做key，当线程的生命周期结束后，
 * contexts中的Thread实例不会被释放，与之对应的Value也不会被释放，时间长了就会导致内存泄漏(Memory Leak)，
 * 当然可以通过soft reference或者weak reference等引用类型，JVM会主动尝试回收(关于Java中的四种引用类型，可以参考25.3节)。
 * @author 14378
 *
 */
public class ActionContext {

	private ConcurrentHashMap<Thread, ActionContext> contexts = new ConcurrentHashMap<>();
	
	public ActionContext getActionContext() {
		ActionContext actionContext = contexts.get(Thread.currentThread());
		if(actionContext == null) {
			actionContext = new ActionContext();
			contexts.put(Thread.currentThread(), actionContext);
		}
		return actionContext;
	}
}
