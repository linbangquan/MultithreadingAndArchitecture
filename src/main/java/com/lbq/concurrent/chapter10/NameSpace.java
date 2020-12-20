package com.lbq.concurrent.chapter10;
/**
 * 类加载器命名空间、运行时包、类的卸载等
 * 1.类加载器命名空间
 * 每个类加载器实例都有各自的命名空间，命名空间是由该加载器及其所有父加载器所构成的，因此在每个类加载器中同一个class都是独一无二的。
 * 但是，使用不同的类加载器，或者同一个类加载器的不同实例，去加载同一个class，则会在堆内存和方法区产生多个class的对象。
 * @author 14378
 *
 */
public class NameSpace {
	/**
	 * 运行下面的代码，不论load多少次Test，你都将会发现他们始终是同一份class对象，
	 * @param args
	 * @throws ClassNotFoundException
	 */
//	public static void main(String[] args) throws ClassNotFoundException {
//		//获取系统类加载器
//		ClassLoader classLoader = NameSpace.class.getClassLoader();
//		Class<?> aClass = classLoader.loadClass("com.lbq.concurrent.chapter10.HelloWorld2");
//		Class<?> bClass = classLoader.loadClass("com.lbq.concurrent.chapter10.HelloWorld2");
//		
//		System.out.println(aClass.hashCode());//366712642
//		System.out.println(bClass.hashCode());//366712642
//		System.out.println(aClass == bClass);//true
//	}
	/**
	 * 不同类加载器加载同一个class
	 * 程序的输出结果显示，aClass和bClass不是同一个class实例。
	 * @param args
	 * @throws ClassNotFoundException
	 */
//	public static void main(String[] args) throws ClassNotFoundException {
//		MyClassLoader classLoader1 = new MyClassLoader("D:\\DevInstall\\eclipse-jee-2019-12-R-win32-x86_64\\workspace\\MultithreadingAndArchitecture\\src\\main\\java", null);
//		
//		BrokerDelegateClassLoader classLoader2 = new BrokerDelegateClassLoader("D:\\DevInstall\\eclipse-jee-2019-12-R-win32-x86_64\\workspace\\MultithreadingAndArchitecture\\src\\main\\java", null);
//		
//		Class<?> aClass = classLoader1.loadClass("com.lbq.concurrent.chapter10.HelloWorld");
//		Class<?> bClass = classLoader2.loadClass("com.lbq.concurrent.chapter10.HelloWorld");
//		
//		System.out.println(aClass.getClassLoader());//My ClassLoader
//		System.out.println(bClass.getClassLoader());//BrokerDelegateClassLoader
//		System.out.println(aClass.hashCode());//1028566121
//		System.out.println(bClass.hashCode());//1118140819
//		System.out.println(aClass == bClass);//false
//	}
	/**
	 * 相同类加载器不同实例加载同一个class
	 * 程序的输出结果显示，aClass和bClass不是同一个class实例。
	 * @param args
	 * @throws ClassNotFoundException
	 */
	public static void main(String[] args) throws ClassNotFoundException {
		MyClassLoader classLoader1 = new MyClassLoader("D:\\DevInstall\\eclipse-jee-2019-12-R-win32-x86_64\\workspace\\MultithreadingAndArchitecture\\src\\main\\java", null);
		MyClassLoader classLoader2 = new MyClassLoader("D:\\DevInstall\\eclipse-jee-2019-12-R-win32-x86_64\\workspace\\MultithreadingAndArchitecture\\src\\main\\java", null);
		Class<?> aClass = classLoader1.loadClass("com.lbq.concurrent.chapter10.HelloWorld");
		Class<?> bClass = classLoader2.loadClass("com.lbq.concurrent.chapter10.HelloWorld");
		System.out.println(aClass.getClassLoader());//My ClassLoader
		System.out.println(bClass.getClassLoader());//My ClassLoader
		System.out.println(aClass.hashCode());//1442407170
		System.out.println(bClass.hashCode());//1028566121
		System.out.println(aClass == bClass);//false
	}
}
