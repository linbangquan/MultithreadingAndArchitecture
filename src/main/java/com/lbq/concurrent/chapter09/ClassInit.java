package com.lbq.concurrent.chapter09;

public class ClassInit {

	static {
//		System.out.println(x);//这里编译不通过，静态语句块只能对后面的静态变量进行赋值，但是不能对其进行访问。
		x = 100;
	}
	
	private static int x = 10;
}
