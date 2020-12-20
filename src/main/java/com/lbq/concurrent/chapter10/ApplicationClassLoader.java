package com.lbq.concurrent.chapter10;
/**
 * 系统类加载器是一种常见的类加载器，其负责加载classpath下的类库资源。我们在进行项目开发的时候引入的第三方jar包，
 * 系统类加载器的父加载器是扩展类加载器，同时它也是自定义类加载器的默认父加载器，系统类加载器的加载路径一般通过-classpath或者-cp指定，
 * 同样也可以通过系统属性java.class.path进行获取。
 * @author 14378
 *
 */
public class ApplicationClassLoader {

	public static void main(String[] args) {
		System.out.println(System.getProperty("java.class.path"));//D:\DevInstall\eclipse-jee-2019-12-R-win32-x86_64\workspace\MultithreadingAndArchitecture\target\classes
		System.out.println(ApplicationClassLoader.class.getClassLoader());//sun.misc.Launcher$AppClassLoader@73d16e93

	}

}
