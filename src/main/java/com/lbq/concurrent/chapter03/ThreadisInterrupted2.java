package com.lbq.concurrent.chapter03;

import java.util.concurrent.TimeUnit;
/**
 * 可中断方法捕获到了中断信号（signal）之后，也就是捕获了InterruptedException异常之后会擦除掉interrupt的标识。
 * 其实这也不难理解，可中断方法捕获到了中断信号之后，为了不影响线程中其他方法的执行，将线程的interrupt标识复位是一种很合理的设计。
 * @author 14378
 *
 */
public class ThreadisInterrupted2 {

	public static void main(String[] args) throws InterruptedException {
		Thread thread = new Thread() {
			@Override
			public void run() {
				while(true) {
					try {
						TimeUnit.MINUTES.sleep(1);
					}catch(InterruptedException e) {
						System.out.printf("I am be interrupted ? %s\n", isInterrupted());
					}
				}
			}
		};
		thread.setDaemon(true);
		thread.start();
		TimeUnit.MILLISECONDS.sleep(2);
		System.out.printf("Thread is interrupted ? %s\n", thread.isInterrupted());
		thread.interrupt();
		System.out.printf("Thread is interrupted ? %s\n", thread.isInterrupted());
		TimeUnit.MILLISECONDS.sleep(2);
		System.out.printf("Thread is interrupted ? %s\n", thread.isInterrupted());
	}

}
