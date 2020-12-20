package com.lbq.concurrent.chapter04;

import java.util.concurrent.TimeUnit;

public class ClassMonitor {

	public static synchronized void method1() {
		System.out.println(Thread.currentThread().getName() + " enter to method1");
		try {
			TimeUnit.MINUTES.sleep(10);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	public static void method2() {
		synchronized (ClassMonitor.class) {
			System.out.println(Thread.currentThread().getName() + " enter to method2");
			try {
				TimeUnit.MINUTES.sleep(10);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	public static void main(String[] args) {
		new Thread(ClassMonitor::method1, "T1").start();
		new Thread(ClassMonitor::method2, "T2").start();
	}

}

//		"T2" #12 prio=5 os_prio=0 tid=0x000000001ad38800 nid=0x2c0c waiting for monitor entry [0x000000001b71f000]
//		   java.lang.Thread.State: BLOCKED (on object monitor)
//			at com.lbq.concurrent.chapter04.ClassMonitor.method2(ClassMonitor.java:17)
//			- waiting to lock <0x00000000d5e56d38> (a java.lang.Class for com.lbq.concurrent.chapter04.ClassMonitor)
//			at com.lbq.concurrent.chapter04.ClassMonitor$$Lambda$2/135721597.run(Unknown Source)
//			at java.lang.Thread.run(Thread.java:748)
//
//		"T1" #11 prio=5 os_prio=0 tid=0x000000001ad38000 nid=0xe68 waiting on condition [0x000000001b61e000]
//		   java.lang.Thread.State: TIMED_WAITING (sleeping)
//			at java.lang.Thread.sleep(Native Method)
//			at java.lang.Thread.sleep(Thread.java:340)
//			at java.util.concurrent.TimeUnit.sleep(TimeUnit.java:386)
//			at com.lbq.concurrent.chapter04.ClassMonitor.method1(ClassMonitor.java:10)
//			- locked <0x00000000d5e56d38> (a java.lang.Class for com.lbq.concurrent.chapter04.ClassMonitor)
//			at com.lbq.concurrent.chapter04.ClassMonitor$$Lambda$1/531885035.run(Unknown Source)
//			at java.lang.Thread.run(Thread.java:748)
