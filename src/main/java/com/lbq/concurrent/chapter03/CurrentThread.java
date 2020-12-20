package com.lbq.concurrent.chapter03;
/**
 * 获取线程ID
 * public long getId()获取线程的唯一id，线程的id在整个jvm进程中都会是唯一的，并且是从0开始逐次递增。
 * 如果你在main线程（main函数）中创建了一个唯一的线程，并且调用getId()后发现其并不等于0，也许你会纳闷，不应该是从0开始吗？
 * 之前已经说过了在一个jvm进程启动的时候，实际上是开辟了很多个线程，自增序列已经有了一定的消耗，因此我们自己创建的线程绝非第0号线程。
 * 
 * public static Thread currentThread()用于返回当前执行线程的引用。
 * 
 * 设置线程上下文类加载器
 * public ClassLoader getContextClassLoader()获取线程上下文的类加载器，简单来说就是这个线程是由哪个类加载器加载的，
 * 如果是在没有修改线程上下文类加载器的情况下，则保持与父线程同样的类加载器。
 * public void setContextClassLoader(ClassLoader cl)设置该线程的类加载器，这个方法可以打破Java类加载器的父委托机制，
 * 有时候该方法也被称为java类加载器的后门。
 * @author 14378
 *
 */
public class CurrentThread {

	public static void main(String[] args) {
		Thread thread = new Thread() {
			@Override
			public void run() {
				//always true
				System.out.println("1:"+Thread.currentThread().getId());
				System.out.println(Thread.currentThread() == this);
			}
		};
		thread.start();
		String name = Thread.currentThread().getName();
		System.out.println("2:"+Thread.currentThread().getId());
		System.out.println("main".equals(name));
	}

}
