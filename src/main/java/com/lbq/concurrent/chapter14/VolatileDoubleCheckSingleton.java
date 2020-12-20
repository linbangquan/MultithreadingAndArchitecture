package com.lbq.concurrent.chapter14;

import java.net.Socket;
import java.sql.Connection;
/**
 * Double-Check虽然是一种巧妙的程序设计，但是有可能会引起类成员变量的实例化conn和socket发生在instance实例化之后，这一切均是由于JVM在运行时指令重排序所导致的，
 * 而volatile关键字则可以防止这种重排序的发生，因此代码稍作修改即可满足多线程下的单例、懒加载以及获取实例的高效性。
 * @author 14378
 *
 */
//final不允许被继承
public final class VolatileDoubleCheckSingleton {
	//实例变量
	private byte[] data = new byte[1024];
	
	private volatile static VolatileDoubleCheckSingleton instance = null;
	
	Connection conn;
	
	Socket socket;
	
	private VolatileDoubleCheckSingleton() {
		this.conn = null;//初始化conn
		this.socket = null;//初始化socket
	}
	
	public static VolatileDoubleCheckSingleton getInstance() {
		//当instance为null时，进入同步代码块，同时该判断避免了每次都需要进行同步代码块，可以提高效率
		if(null == instance) {
			//只有一个线程能够获得Singleton.class关联的monitor
			synchronized (VolatileDoubleCheckSingleton.class) {
				//判断如果instance为null则创建
				if(null == instance) {
					instance = new VolatileDoubleCheckSingleton();
				}
			}
		}
		return instance;
	}

}
