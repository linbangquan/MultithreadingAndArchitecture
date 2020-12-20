package com.lbq.concurrent.chapter14;

import java.net.Socket;
import java.sql.Connection;
/**
 * Double-Check是一种比较聪明的设计方式，他提供了一个高效的数据同步策略，那就是首次初始化时加锁，
 * 之后则允许多个线程同时进行getInstance方法的调用来获得类的实例。
 * 当两个线程发现null==instance成立时，只有一个线程有资格进入同步代码块，完成对instance的实例化，
 * 随后的线程发现null==instance不成立则无须进行任何动作，以后对getInstance的访问就不需要数据同步的保护了。
 * 这种方式看起来是那么的完美和巧妙，既满足了懒加载，又保证了instance实例的唯一性，D
 * ouble-Check的方式提供了高效的数据同步策略，可以允许多个线程同时对getInstance进行访问，
 * 但是这种方式在多线程的情况下有可能会引起空指针异常，下面来分析一下引发异常的原因。
 * 在Singleton的构造函数中，需要分别实例化conn和socket两个资源，还有Singleton自身，
 * 根据JVM运行时指令重排序和Happens-Before规则，这三者之间的实例化顺序并无前后关系的约束，那么极有可能是instance最先被实例化，
 * 而conn和socket并未完成实例化，未完成初始化的实例调用其方法将会抛出空指针异常。
 * @author 14378
 *
 */
//final不允许被继承
public final class DoubleCheckSingleton {
	//实例变量
	private byte[] data = new byte[1024];
	
	private static DoubleCheckSingleton instance = null;
	
	Connection conn;
	
	Socket socket;
	
	private DoubleCheckSingleton() {
		this.conn = null;//初始化conn
		this.socket = null;//初始化socket
	}
	
	public static DoubleCheckSingleton getInstance() {
		//当instance为null时，进入同步代码块，同时该判断避免了每次都需要进行同步代码块，可以提高效率
		if(null == instance) {
			//只有一个线程能够获得Singleton.class关联的monitor
			synchronized (DoubleCheckSingleton.class) {
				//判断如果instance为null则创建
				if(null == instance) {
					instance = new DoubleCheckSingleton();
				}
			}
		}
		return instance;
	}

}
