package com.lbq.concurrent.chapter09;

public class ClassInit2 {
	//父类中有静态变量value
	static class Parent{
		static int value = 10;
		static {
			value = 20;
		}
	}
	//子类使用父类的静态变量为自己的静态变量赋值
	static class Child extends Parent{
		static int i = value;
	}
	
	public static void main(String[] args) {
		//这里输出是20，而不是10，因为父类的<clinit>()方法优先得到了执行。
		System.out.println(Child.i);
	}

}
