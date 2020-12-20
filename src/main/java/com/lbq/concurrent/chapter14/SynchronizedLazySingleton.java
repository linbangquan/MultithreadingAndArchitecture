package com.lbq.concurrent.chapter14;
/**
 * 懒汉式的方式可以保证实例的懒加载，但无法保证实例的唯一性，根据本书第4章的内容，在多线程的情况下，instance又称为共享资源（数据），
 * 当多个线程对其访问使用时，需要保证数据的同步性，对LazySingleton.class代码稍加修改，增加同步的约束即可。
 * 采用懒汉式+数据同步的方式既满足了懒加载又能够百分之百地保证instance实例的唯一性，
 * 但是synchronized关键字天生的排他性导致了getInstance方法只能在同一时刻被一个线程所访问，性能低下。
 * @author 14378
 *
 */
//final不允许被继承
public final class SynchronizedLazySingleton {
	//实例变量
	private byte[] data = new byte[1024];
	//定义实例，但是不直接初始化
	private static SynchronizedLazySingleton instance = null;
	
	private SynchronizedLazySingleton() {}
	
	public static synchronized SynchronizedLazySingleton getInstance() {
		if(null == instance) {
			instance = new SynchronizedLazySingleton();
		}
		return instance;
	}

}
