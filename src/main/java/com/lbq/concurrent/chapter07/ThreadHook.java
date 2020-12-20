package com.lbq.concurrent.chapter07;

import java.util.concurrent.TimeUnit;
/**
 * 注入钩子线程
 * 7.2.1 Hook线程介绍
 * 	JVM进程的退出是由于JVM进程中没有活跃的非守护线程，或者收到了系统中断信号，
 * 	向JVM程序注入一个Hook线程，在JVM进程退出的时候，Hook线程会启动执行，
 * 	通过Runtime可以为JVM注入多个Hook线程。
 * 
 * 下面程序中，给Java程序注入了两个Hook线程，在main线程中结束，也就是JVM中没有了活动的非守护线程，
 * JVM进程即将退出时，两个Hook线程会被启动并且运行，输出结果如下：
 * The program will is stopping
 * The hook thread 1 is running.
 * The hook thread 2 is running.
 * The hook thread 2 will exit.
 * The hook thread 1 will exit.
 * @author 14378
 *
 */
public class ThreadHook {

	public static void main(String[] args) {
		// 为应用程序注入钩子线程
		Runtime.getRuntime().addShutdownHook(
			new Thread() {
				@Override
				public void run() {
					try {
						System.out.println("The hook thread 1 is running.");
						TimeUnit.SECONDS.sleep(1);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					System.out.println("The hook thread 1 will exit.");
				}
			}
		);

		//钩子线程可以注册多个
		Runtime.getRuntime().addShutdownHook(
			new Thread() {
				@Override
				public void run() {
					try {
						System.out.println("The hook thread 2 is running.");
						TimeUnit.SECONDS.sleep(1);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					System.out.println("The hook thread 2 will exit.");
				}
			}
		);
		
		
		System.out.println("The program will is stopping");
		try {
			TimeUnit.SECONDS.sleep(10);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

}
