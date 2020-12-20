package com.lbq.concurrent.chapter21;
/**
 * 21.4 使用ThreadLocal设计线程上下文
 * 在本章中，关于ThreadLocal内容的介绍很多，在本节中，我们将使用ThreadLocal实现一个简单的线程上下文
 * (一般某个动作或者任务都是交给一个线程去执行的，因此线程上下文的类可以命名为ActionContext或者TaskContext之类的)。
 * 
 * 为了确保线程间数据隔离的绝对性，重写initialValue()方法是一个比较不错的编程体检，ThreadLocal.withInitial(Context::new);
 * 在这个Context例子中，我们将所有需要被线程访问和操作的数据都封装在了Context中，每个线程拥有不一样的上下文数据，
 * 当然你也可以选择为每一个数据定义一个ThreadLocal。
 * @author 14378
 *
 */
public class ActionContext2 {
	//定义ThreadLocal，并且使用Supplier的方式重写initValue
	private static final ThreadLocal<Context> context = ThreadLocal.withInitial(Context::new);
	
	public static Context get() {
		return context.get();
	}
	//每个线程都会有一个独立的Context实例
	static class Context {
		//在Context中的其他成员
		private Configuration configuration;
		private OtherResource otherResource;
		public Configuration getConfiguration() {
			return configuration;
		}
		public void setConfiguration(Configuration configuration) {
			this.configuration = configuration;
		}
		public OtherResource getOtherResource() {
			return otherResource;
		}
		public void setOtherResource(OtherResource otherResource) {
			this.otherResource = otherResource;
		}
	}
}
