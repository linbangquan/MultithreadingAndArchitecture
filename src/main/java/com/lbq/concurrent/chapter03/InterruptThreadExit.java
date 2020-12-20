package com.lbq.concurrent.chapter03;

import java.util.concurrent.TimeUnit;
/**
 * jdk有一个Deprecated方法stop，但是该方法存在一个问题，jdk官方早已经不推荐使用，其在后面的版本中有可能会被移除，
 * 根据官方的描述，该方法在关闭线程时可能不会释放掉monitor的锁，所以强烈建议不要使用该方法结束线程，本节将主要介绍几种关闭线程的方法。
 * 
 * 正常关闭
 * 1.线程结束生命周期正常结束
 * 线程运行结束，完成了自己的使命之后，就会正常退出，如果线程中的任何耗时比较短，或者时间可控，那么放任他正常结束就好了。
 * 2.捕获中断信号关闭线程
 * 我们通过new Thread的方式创建线程，这种方式看似很简单，其实它的派生成本是比较高的，因此在线程中往往会循环地执行某个任务，
 * 比如心跳检查，不断地接收网络信息报文等，系统决定退出的时候，可以借助中断线程的方式使其退出。
 * 
 * @author 14378
 *
 */
public class InterruptThreadExit {

	public static void main(String[] args) throws InterruptedException {
		Thread t = new Thread() {
			@Override
			public void run() {
				System.out.println("I will start work");
				//通过检查线程interrupt的标识来决定是否退出的，如果在线程中执行某个可中断方法，则可以通过捕获中断信号来决定是否退出。
				while(!isInterrupted()) {
					//working.
				}
				System.out.println("I will be exiting.");
			}
		};

		t.start();
		TimeUnit.MINUTES.sleep(1);
		System.out.println("System will be shutdown.");
		t.interrupt();
	}

}
