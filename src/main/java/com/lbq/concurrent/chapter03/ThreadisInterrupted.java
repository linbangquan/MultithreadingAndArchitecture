package com.lbq.concurrent.chapter03;

import java.util.concurrent.TimeUnit;
/**
 * isInterrupted是Thread的一个成员方法，它主要判断当前线程是否被中断，
 * 该方法仅仅是对interrupt标识的一个判断，并不会影响标识发生任何改变，
 * 这与我们即将学习到的interrupted是存在差别的。
 * @author 14378
 *
 */
public class ThreadisInterrupted {

	public static void main(String[] args) throws InterruptedException {
		Thread thread = new Thread() {
			@Override
			public void run() {
				while(true) {
					//do nothing, just empty loop.
				}
			}
		};

		thread.start();
		TimeUnit.MILLISECONDS.sleep(2);
		System.out.printf("Thread is interrupted ? %s\n", thread.isInterrupted());//false
		thread.interrupt();
		System.out.printf("Thread is interrupted ? %s\n", thread.isInterrupted());//true
		thread.interrupt();
		System.out.printf("Thread is interrupted ? %s\n", thread.isInterrupted());//true
	}

}
