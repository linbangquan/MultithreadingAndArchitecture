package com.lbq.concurrent.chapter01;
/**
 * 线程的真正的执行逻辑是在run方法中，通常我们会把run方法称为线程的执行单元，这也就回答了我们最开始提出的疑问，重写run方法，用start方法启动线程。
 * Thread中run方法的代码如下，如果我们没有使用Runnable接口对其进行构造，则可以认为Thread的run方法本身就是一个空的实现：
 * @Override
 * public void run(){
 * 		if(target != null){
 * 			target.run();
 * 		}
 * }
 * 其实Thread的run和start就是一个比较典型的模板设计模式，父类编写算法结构代码，子类实现逻辑细节。
 * 
 * print方法类似于Thread的start方法，而wrapPrint则类似于run方法，这样做的好处是，
 * 程序结构有父类控制，并且是final修饰的，不允许被重写，子类只需要实现想要的逻辑任务即可。
 * @author 14378
 *
 */
public class TemplateMethod {

	public static void main(String[] args) {
		TemplateMethod t1 = new TemplateMethod() {
			@Override
			protected void wrapPrint(String message) {
				System.out.println("*" + message + "*");
			}
		};
		
		t1.print("Hello Thread");
		
		TemplateMethod t2 = new TemplateMethod() {
			@Override
			protected void wrapPrint(String message) {
				System.out.println("+" + message + "+");
			}
		};
		
		t2.print("Hello Thread");
	}

	public final void print(String message) {
		System.out.println("################");
		wrapPrint(message);
		System.out.println("################");
	}

	protected void wrapPrint(String message) {
		
	}
}
