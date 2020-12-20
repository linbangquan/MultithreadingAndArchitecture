package com.lbq.concurrent.chapter09;

import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;
/**
 * <clinit>()方法虽然是真实存在的，但是它只能被虚拟机执行，在主动使用触发了类的初始化之后就会调用这个方法，
 * 如果有多个线程同时访问这个方法，那么会不会引起线程安全问题呢？
 * 运行下面的代码，你会发现在同一时间，只能有一个线程执行到静态代码块中的内容，并且静态代码块仅仅只会被执行一次，
 * jvm保证了<clinit>方法在多个线程的执行环境下的同步语义，因此在单例设计模式下，采用Holder的方式是一种最佳的设计方案。
 * @author 14378
 *
 */
public class ClassInit3 {
	
	static {
		try {
			System.out.println("The ClassInit static code block will be invoke.");
			TimeUnit.MINUTES.sleep(10);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	public static void main(String[] args) {
		IntStream.range(0, 5).forEach(i -> new Thread(ClassInit::new));
	}

}
