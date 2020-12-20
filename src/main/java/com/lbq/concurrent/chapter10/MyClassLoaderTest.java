package com.lbq.concurrent.chapter10;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
/**
 * Java文件编译之后，将class文件复制到你想要的目录，比如笔者将编译之后的文件复制到了G:\classloader1目录，
 * 同时在程序运行的classpath中删除掉HelloWorld这个class。如果你用的是集成开发环境，那么应该连HelloWorld.java也一并删除，
 * 否则HelloWorld将会系统类加载器加载，这是由于类加载器的委托机制所导致的。
 * 如果没有错误发生，程序会正常输出类加载器以及对世界的一句问候，在测试代码中注释掉①以下所有的代码你会发现，
 * 虽然aClass被成功加载并且输出了类加载器的信息，但是HelloWorld的静态代码块并没有得到输出，
 * 那是因为使用类加载器loadClass并不会导致类的主动初始化，它只是执行了加载过程中的加载阶段而已，这一点需要读者注意。
 * @author 14378
 *
 */
public class MyClassLoaderTest {

	public static void main(String[] args) throws ClassNotFoundException, InstantiationException, IllegalAccessException, NoSuchMethodException, SecurityException, IllegalArgumentException, InvocationTargetException {
		MyClassLoader classLoader = new MyClassLoader("D:\\DevInstall\\eclipse-jee-2019-12-R-win32-x86_64\\workspace\\MultithreadingAndArchitecture\\src\\main\\java");//\\com\\lbq\\concurrent\\chapter10
		
		Class<?> aClass = classLoader.loadClass("com.lbq.concurrent.chapter10.HelloWorld");
		System.out.println(aClass.getClassLoader());
		//①注释
		Object helloWorld = aClass.newInstance();
		System.out.println(helloWorld);
		Method welcomeMethod = aClass.getMethod("welcome");
		String result = (String) welcomeMethod.invoke(helloWorld);
		System.out.println("Result: " + result);
//		My ClassLoader
//		Hello World Class is Initialized.
//		com.lbq.concurrent.chapter10.HelloWorld@5c647e05
//		Result: Hello World
	}

//	public static void main(String[] args) throws ClassNotFoundException, InstantiationException, IllegalAccessException, NoSuchMethodException, SecurityException, IllegalArgumentException, InvocationTargetException {
//		ClassLoader extClassLoader = MyClassLoaderTest.class.getClassLoader().getParent();
//		MyClassLoader classLoader = new MyClassLoader("D:\\DevInstall\\eclipse-jee-2019-12-R-win32-x86_64\\workspace\\MultithreadingAndArchitecture\\src\\main\\java", extClassLoader);
//		Class<?> aClass = classLoader.loadClass("com.lbq.concurrent.chapter10.HelloWorld");
//		System.out.println(aClass);
//		System.out.println(aClass.getClassLoader());
//		Object helloWorld = aClass.newInstance();
//		System.out.println(helloWorld);
//		Method welcomeMethod = aClass.getMethod("welcome");
//		String result = (String) welcomeMethod.invoke(helloWorld);
//		System.out.println("Result: " + result);
//	}
	
//	public static void main(String[] args) throws ClassNotFoundException, InstantiationException, IllegalAccessException, NoSuchMethodException, SecurityException, IllegalArgumentException, InvocationTargetException {
//		MyClassLoader classLoader = new MyClassLoader("D:\\DevInstall\\eclipse-jee-2019-12-R-win32-x86_64\\workspace\\MultithreadingAndArchitecture\\src\\main\\java", null);
//		Class<?> aClass = classLoader.loadClass("com.lbq.concurrent.chapter10.HelloWorld");
//		System.out.println(aClass);
//		System.out.println(aClass.getClassLoader());
//		Object helloWorld = aClass.newInstance();
//		System.out.println(helloWorld);
//		Method welcomeMethod = aClass.getMethod("welcome");
//		String result = (String) welcomeMethod.invoke(helloWorld);
//		System.out.println("Result: " + result);
//	}
}
