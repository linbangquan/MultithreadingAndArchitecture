package com.lbq.concurrent.chapter04;

import java.util.concurrent.TimeUnit;

public class ThisMonitor {

	public synchronized void method1() {
		System.out.println(Thread.currentThread().getName() + " enter to method1");
		try {
			TimeUnit.MINUTES.sleep(1);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	public void method2() {
		synchronized (this) {
			System.out.println(Thread.currentThread().getName() + " enter to method2");
			try {
				TimeUnit.MINUTES.sleep(1);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	public static void main(String[] args) {
		ThisMonitor thisMonitor = new ThisMonitor();
		new Thread(thisMonitor::method1, "T1").start();
		new Thread(thisMonitor::method2, "T2").start();
	}

}

//		"T2" #12 prio=5 os_prio=0 tid=0x000000001a980800 nid=0x20a0 waiting for monitor entry [0x000000001b35f000]
//		   java.lang.Thread.State: BLOCKED (on object monitor)
//			at com.lbq.concurrent.chapter04.ThisMonitor.method2(ThisMonitor.java:17)
//			- waiting to lock <0x00000000d5e57b40> (a com.lbq.concurrent.chapter04.ThisMonitor)
//			at com.lbq.concurrent.chapter04.ThisMonitor$$Lambda$2/303563356.run(Unknown Source)
//			at java.lang.Thread.run(Thread.java:748)
//
//		"T1" #11 prio=5 os_prio=0 tid=0x000000001a97f800 nid=0x12d4 waiting on condition [0x000000001b25e000]
//		   java.lang.Thread.State: TIMED_WAITING (sleeping)
//			at java.lang.Thread.sleep(Native Method)
//			at java.lang.Thread.sleep(Thread.java:340)
//			at java.util.concurrent.TimeUnit.sleep(TimeUnit.java:386)
//			at com.lbq.concurrent.chapter04.ThisMonitor.method1(ThisMonitor.java:10)
//			- locked <0x00000000d5e57b40> (a com.lbq.concurrent.chapter04.ThisMonitor)
//			at com.lbq.concurrent.chapter04.ThisMonitor$$Lambda$1/531885035.run(Unknown Source)
//			at java.lang.Thread.run(Thread.java:748)
