package com.lbq.concurrent.chapter21;

/**
 * 线程上下文设计模式
 * 21.1 什么是上下文
 * 关于上下文(context)，我们在开发的过程中经常会遇到，比如开发struts2的ActionContext、Spring中AppliactionContext，
 * 上下文是贯穿整个系统或阶段生命周期的对象，其中包含了系统全局的一些信息，比如登录之后的用户信息、账号信息，以及在程序每个阶段运行时的数据。
 * 
 * 在第14章中定义的单例对象实际上也是上下文，并且它是贯穿整个程序运行的生命周期的上下文，比如清单21-1中的代码就是典型的使用单例对象充当系统级别上下文的例子：
 * 
 * 在下面的代码中我们不难发现，如果configuration和runtimeInfo的生命周期会随着被创建一直到系统运行结束，我们就可以将ApplicationContext称为系统的上下文，
 * 诸如configuration和runtimeInfo等其他实例属性则称为系统上下文成员。
 * 
 * 当然在设计系统上下文时，除了要考虑到它的全局唯一性(单例设计模式保证)之外，还要考虑到有些成员只能被初始化一次，
 * 比如配置信息的加载(在第22章所讲的Balking设计模式就可以保证这一点)，以及在多线程环境下，上下文成员的线程安全性
 * (第16章 “Single Thread Execution设计模式”，第18章 “不可变对象设计模式”等资源保护方法)。
 * @author 14378
 *
 */
public class ApplicationContext {

	private ApplicationConfiguration configuration;
	
	private RuntimeInfo runtimeInfo;
	
	private static class Holder {
		private static ApplicationContext instance = new ApplicationContext();
	}
	
	public static ApplicationContext getContext() {
		return Holder.instance;
	}
	
	public void setConfiguration(ApplicationConfiguration configuration) {
		this.configuration = configuration;
	}
	
	public ApplicationConfiguration getConfiguration() {
		return this.configuration;
	}

	public RuntimeInfo getRuntimeInfo() {
		return runtimeInfo;
	}

	public void setRuntimeInfo(RuntimeInfo runtimeInfo) {
		this.runtimeInfo = runtimeInfo;
	}
}
