package com.lbq.concurrent.chapter14;
/**
 * 所谓懒汉式就是在使用类实例时候再去创建（用时创建），这样就可以避免类在初始化时提前创建。
 * Singleton的类变量instance=null，因此当Singleton.class被初始化的时候instance并不会被实例化，
 * 在getInstance方法中会判断instance实例是否被实例化，看起来没有什么问题，但是将getInstance方法放在多线程环境下进行分析，
 * 则会导致instance被实例化一次以上，并不能保证单例的唯一性。
 * 当两个线程同时看到instance==null，那么instance将无法保证单例的唯一性。
 * @author 14378
 *
 */
//final不允许被继承
public final class LazySingleton {
	static {
		System.out.println("LazySingleton static ...");
	}
	{
		System.out.println("LazySingleton ...");
	}
	public static int x = 1;
	//实例变量
	private byte[] data = new byte[1024];
	//定义实例，但是不直接初始化
	private static LazySingleton instance = null;
	
	private LazySingleton() {
		System.out.println("new LazySingleton()");
	}
	
	public static LazySingleton getInstance() {
		if(null == instance) {
			instance = new LazySingleton();
		}
		return instance;
	}

}
