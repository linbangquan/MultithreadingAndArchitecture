package com.lbq.concurrent.chapter05;

import java.util.concurrent.TimeUnit;

public class EventClient2 {

	public static void main(String[] args) {
		final EventQueue2 eventQueue = new EventQueue2();
		new Thread(() -> {
			for(;;) {
				eventQueue.offer(new EventQueue2.Event());
			}
		}, "Producer").start();
		new Thread(() -> {
			for(;;) {
				eventQueue.offer(new EventQueue2.Event());
			}
		}, "Producer2").start();

		new Thread(() -> {
			for(;;) {
				eventQueue.take();
				try {
					TimeUnit.MILLISECONDS.sleep(10);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}, "Consumer" ).start();
		new Thread(() -> {
			for(;;) {
				eventQueue.take();
				try {
					TimeUnit.MILLISECONDS.sleep(10);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}, "Consumer2" ).start();
	}

}
