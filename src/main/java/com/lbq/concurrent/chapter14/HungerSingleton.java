package com.lbq.concurrent.chapter14;
/**
 * 饿汉式的关键在于instance作为类变量并且直接得到了初始化，根据本书第二部分的知识我们可以得知，
 * 如果主动使用Singleton类，那么instance实例将会直接完成创建，包括其中的实例变量都会得到初始化，
 * 比如1k空间的data将会同时被创建。
 * instance作为类变量在类初始化的过程中会被收集进<clinit>()方法中，该方法能够百分之百地保证同步，
 * 也就是说instance在多线程的情况下不可能被实例化两次，但是instance被ClassLoader加载后可能很长一段时间才被使用，
 * 那就意味着instance实例所开辟的堆内存会驻留更久的时间。
 * 如果一个类中的成员的属性比较少，且占用的内存资源不多，饿汉的方式也未尝不可，相反，如果一个类中的成员都是比较重的资源，那么这种方式就会有些不妥。
 * 总结起来，饿汉式的单例设计模式可以保证多个线程下的唯一实例，getInstance方法性能也比较高，但是无法进行懒加载。
 * @author 14378
 *
 */
//final不允许被继承
public final class HungerSingleton {
	static {
		System.out.println("HungerSingleton static ...");
	}
	{
		System.out.println("HungerSingleton ...");
	}
	public static int x = 1;
	//实例变量
	private byte[] data = new byte[1024];
	//在定义实例对象的时候直接初始化
	private static HungerSingleton instance = new HungerSingleton();
	//私有构造函数，不允许外部new
	private HungerSingleton() {
		System.out.println("new HungerSingleton()");
	}
	
	public static HungerSingleton getInstance() {
		return instance;
	}
}
