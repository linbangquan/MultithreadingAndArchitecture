package com.lbq.concurrent.chapter14;
/**
 * Holder的方式完全是借助了类加载的特点，下面我们对整个单例模式进行重构，然后结合类加载器的知识点分析这样做的好处在哪里。
 * 在Singleton类中并没有instance的静态成员，而是将其放到了静态内部类Holder之中，
 * 因此在Singleton类的初始化过程中并不会创建Singleton的实例，Holder类中定义了Singleton的静态变量，并且直接进行了实例化，
 * 当Holder被主动引用的时候则会创建Singleton的实例，Singleton实例的创建过程在Java程序编译期收集至<clinit>()方法中，
 * 该方法又是同步方法，同步方法可以保证内存的可见性、JVM指令的顺序性和原子性、Holder方式的单例设计是最好的设计之一，
 * 也是目前使用比较广的设计之一。
 * @author 14378
 *
 */
//final不允许被继承
public final class HolderSingleton {

	static {
		System.out.println("HolderSingleton static ...");
	}
	{
		System.out.println("HolderSingleton ...");
	}
	public static int x = 1;
	
	//实例变量
	private byte[] data = new byte[1024];
	
	private HolderSingleton() {
		System.out.println("new HolderSingleton()");
	}
	//在静态内部类中持有Singleton的实例，并且可直接初始化
	private static class Holder{
		private static HolderSingleton instance = new HolderSingleton();
	}
	//调用getInstance方法，事实上是获得Holder的instance静态属性
	public static HolderSingleton getInstance() {
		return Holder.instance;
	}

}
