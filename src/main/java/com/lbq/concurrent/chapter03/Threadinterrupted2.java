package com.lbq.concurrent.chapter03;

import java.util.concurrent.TimeUnit;
/**
 * 打开Thread的源码，不难发现，isInterrupted方法和interrupted方法都调用了同一个本地方法：
 * private native boolean isInterrupted(boolean ClearInterrupted);
 * 其中参数ClearInterrupted主要用来控制是否擦除线程interrupt的标识。
 * isInterrupted方法的源码中该参数为false，表示不想擦除：
 * public boolean isInterrupted(){
 * 		return isInterrupted(false);
 * }
 * 而interrupted静态方法中该参数则为true，表示想要擦除：
 * public static boolean interrupted(){
 * 		return currentThread().isInterrupted(true);
 * }
 * 
 * 在比较详细地学习了interrupt方法之后，大家思考一个问题，如果一个线程在没有执行可中断方法之前就被打断，那么其接下来将执行中断方法，比如sleep会发生什么样的情况呢?
 * 
 * 通过运行上面的程序，你会发现，如果一个线程设置了interrupt标识，那么接下来的可中断方法会立即中断，因此注释5的信号捕获部分代码会被执行.
 * @author 14378
 *
 */
public class Threadinterrupted2 {

	public static void main(String[] args) {
		//1.判断当前线程是否被中断
		System.out.println("Main thread is interrupted? " + Thread.interrupted());
		//2.中断当前线程
		Thread.currentThread().interrupt();
		//3.判断当前线程是否已经被中断
		System.out.println("Main thread is interrupted? " + Thread.currentThread().isInterrupted());
		
		try {
			//当前线程执行可中断方法
			TimeUnit.MINUTES.sleep(1);
		} catch (InterruptedException e) {
			//捕获中断信号
			System.out.println("I will be interrupted still.");
		}
		System.out.println("++++++++++++++++++++++++");
	}
}
