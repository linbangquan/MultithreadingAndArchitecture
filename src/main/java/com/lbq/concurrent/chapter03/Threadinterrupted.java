package com.lbq.concurrent.chapter03;

import java.util.concurrent.TimeUnit;
/**
 * interrupted是一个静态方法，虽然其用于判断当前线程是否被中断，
 * 但是它和成员方法isInterrupted还是有很大的区别的，调用该方法会直接擦除线程的interrupt标识，
 * 需要注意的是，如果当前线程被打断了，那么第一次调用interrupted方法会返回true，并且立即擦除了interrupt标识；
 * 第二次包括以后的调用永远都会返回false，除非在此期间线程又一次地被打断。
 * @author 14378
 *
 */
public class Threadinterrupted {

	public static void main(String[] args) throws InterruptedException {
		Thread thread = new Thread() {
			@Override
			public void run() {
				while(true) {
					System.out.println(Thread.interrupted());
				}
			}
		};
		thread.setDaemon(true);
		thread.start();
		
		//shortly block make sure the thread is started.
		TimeUnit.MILLISECONDS.sleep(2);
		thread.interrupt();
//		...
//		false
//		false
//		true
//		false
//		false
//		...
	}

}
