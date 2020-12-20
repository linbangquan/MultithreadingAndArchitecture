package com.lbq.concurrent.chapter09;

public class Child extends Parent{
	
	static {
		System.out.println("The child will be initialized");
	}
	
	public static int x = 10;
}
