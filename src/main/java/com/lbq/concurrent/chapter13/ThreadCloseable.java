package com.lbq.concurrent.chapter13;

import java.util.concurrent.TimeUnit;

public class ThreadCloseable extends Thread {
	//volatile关键字保证了started线程的可见性
	private volatile boolean started = true;
	
	@Override
	public void run() {
		while(started) {
			try {
				TimeUnit.SECONDS.sleep(3);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			System.out.println("----------");
		}
	}
	
	public void shutdown() {
		this.started = false;
	}
	public static void main(String[] args) throws InterruptedException {
		ThreadCloseable t1 = new ThreadCloseable();
		t1.start();
		TimeUnit.SECONDS.sleep(15);
		t1.shutdown();
		System.out.println("++++++++++++++++");
	}

}
