package com.lbq.concurrent.chapter10;
/**
 * 扩展类加载器的父加载器是根加载器，它主要用于加载JAVA_HOME下的jre\lib\ext子目录里面的类库。
 * 扩展类加载器是由纯java语言实现的，它是java.lang.URLClassLoader的子类，
 * 它的完整类名是sun.misc.Launcher$ExtClassLoader。
 * 扩展类加载器所加载的类库可以通过系统属性java.ext.dirs获得。
 * @author 14378
 *
 */
public class ExtClassLoader {

	public static void main(String[] args) throws ClassNotFoundException {
		System.out.println(System.getProperty("java.ext.dirs"));//D:\DevInstall\Java\jdk1.8.0_202\jre\lib\ext;C:\WINDOWS\Sun\Java\lib\ext

		Class<?> helloClass = Class.forName("Hello");
		System.out.println(helloClass.getClassLoader());
	}

}
