package com.lbq.concurrent.chapter21;
/**
 * 第一种方式只用一个ThreadLocal，这也就意味着与之对应的ThreadLocalMap有一份Entry，其中Key是ThreadLocal，Value是Context，
 * 第二种方法使用了两个ThreadLocal，那么与之对应的ThreadLocalMap中将会存在两个Entry。
 * 
 * 21.5 本章总结
 * 线程上下文ThreadLocal又被称之为“线程保险箱”，在很多开源软件源码中能看到ThreadLocal的应用，
 * ThreadLocal能够将指定的变量和当前线程进行绑定，线程之间彼此隔离，持有不同的对象实例，从而避免了数据资源的竞争。
 * 当然，ThreadLocal也存在着内存泄漏的问题，在本章中我们也进行了比较详细的解释，希望读者在使用ThreadLocal的时候引起注意。
 * @author 14378
 *
 */
public class ActionContext3 {
	//为Configuration创建ThreadLocal
	private static final ThreadLocal<Configuration> configuration = ThreadLocal.withInitial(Configuration::new);
	//为OtherResource创建ThreadLocal
	private static final ThreadLocal<OtherResource> otherResource = ThreadLocal.withInitial(OtherResource::new);
	
	public static void setConfiguration(Configuration conf) {
		configuration.set(conf);
	}
	public static Configuration getConfiguration() {
		return configuration.get();
	}
	public static void setOtherResource(OtherResource oResource) {
		otherResource.set(oResource);
	}
	public static OtherResource getOtherResource() {
		return otherResource.get();
	}
}
