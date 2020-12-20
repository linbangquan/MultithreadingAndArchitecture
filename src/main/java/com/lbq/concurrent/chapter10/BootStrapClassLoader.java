package com.lbq.concurrent.chapter10;
/**
 * 根加载器又称为Bootstrap类加器，该类加载器是最为顶层的加载器，其没有任何父类加载器，
 * 它是由C++编写的，主要负责虚拟机核心类库的加载，比如整个java.lang包都是由根加载器所加载的，
 * 可以通过-Xbootclasspath来指定根加载器的路径，也可以通过系统属性来得知当前JVM的根加载器
 * 都加载了哪些资源。
 * 
 * @author 14378
 *
 */
public class BootStrapClassLoader {

	public static void main(String[] args) {
		System.out.println("Bootstrap:" + String.class.getClassLoader());
		System.out.println(System.getProperty("sun.boot.class.path"));
	}
	/**
	 * Bootstrap:null
	 * D:\DevInstall\Java\jdk1.8.0_202\jre\lib\resources.jar;
	 * D:\DevInstall\Java\jdk1.8.0_202\jre\lib\rt.jar;
	 * D:\DevInstall\Java\jdk1.8.0_202\jre\lib\sunrsasign.jar;
	 * D:\DevInstall\Java\jdk1.8.0_202\jre\lib\jsse.jar;
	 * D:\DevInstall\Java\jdk1.8.0_202\jre\lib\jce.jar;
	 * D:\DevInstall\Java\jdk1.8.0_202\jre\lib\charsets.jar;
	 * D:\DevInstall\Java\jdk1.8.0_202\jre\lib\jfr.jar;
	 * D:\DevInstall\Java\jdk1.8.0_202\jre\classes
	 */
}
