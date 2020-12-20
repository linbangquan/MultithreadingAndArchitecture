package com.lbq.concurrent.chapter09;
/**
 * 我们通过前面学习和掌握的内容一起来解释一下：
 * private static int x = 0;
 * private static int y;
 * private static Singleton instance = new Singleton();
 * 在连接阶段的准备过程中，每一个类变量都被赋予了相应的初始值：x=0,y=0,instance=null
 * 下面跳过解析过程，来看类的初始化阶段，初始化阶段会为每一个类变量赋予正确的值，也就是执行<clinit>()方法的过程：
 * x=0,y=0,instance=new Singleton()
 * 在new Singleton的时候会执行类的构造函数，而在构造函数中分别对x和y进行了自增，因此结果为：x=1,y=1
 * 
 * 我们再来看调换顺序之后的程序输出：
 * private static Singleton instance = new Singleton();
 * private static int x = 0;
 * private static int y;
 * 在连接阶段的准备过程中，每个类变量都被赋予了相应的初始值：instance=null,x=0,y=0
 * 在类的初始化阶段，需要为每一个类赋予程序编写时期所期待的正确的初始值，首先会进入instance的构造函数中
 * ()，执行完instance的构造函数之后，各个静态变量的值如下：
 * instance=Singleton@3f99bd52,x=1,y=1
 * 然后，为x初始化，由于x显示地进行赋值，因此0才是所期望的正确赋值，而y由于没有给定初始值，在构造函数中计算所得的值就是所谓的正确赋值，
 * 因此结果又会变成：instance=Singleton@3f99bd52,x=01,y=1
 * @author 14378
 *
 */
public class Singleton {

	private static Singleton instance = new Singleton();
	
	private static int x = 0;
	
	private static int y;
	
//	private static Singleton instance = new Singleton();
	
	private Singleton() {
		x++;
		y++;
	}
	
	public static Singleton getInstance() {
		return instance;
	}
	
	public static void main(String[] args) {
		Singleton singleton = Singleton.getInstance();
		System.out.println(singleton.x);
		System.out.println(singleton.y);

	}

}
