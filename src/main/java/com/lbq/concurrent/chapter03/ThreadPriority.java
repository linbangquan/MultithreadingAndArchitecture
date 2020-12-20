package com.lbq.concurrent.chapter03;

/**
 * 设置线程线程的优先级
 * public final void setPriority(int newPriority)为线程设定优先级。
 * public final int getPriority()获取线程的优先级。
 * 
 * 进程有进程的优先级，线程同样也有优先级，理论上是优先级比较高的线程会获取优先级被CPU调度器的机会，
 * 但是事实上往往并不会如你所愿，设置线程的优先级同样是一个hint操作，
 * 1.对于root用户，它会hint操作系统设置你想要设置的优先级别，否则它会被忽略。
 * 2.如果CPU比较忙，设置优先级可能会获得更多的CPU时间片，但是闲时优先级的高低几乎不会有任何作用。
 * 所以，不要在程序设计当中企图使用线程优先级绑定某些特定的业务，或者让业务严重依赖于线程优先级，这可能会让你大失所望。
 * @author 14378
 *
 */
public class ThreadPriority {

	public static void main(String[] args) {

		Thread t1 = new Thread(() -> {
			int i = 1;
			while(i > 0) {
				System.out.println("t1");
				i--;
			}
		}) ;
		t1.setPriority(3);
		Thread t2 = new Thread(() -> {
			int i = 1;
			while(i > 0) {
				System.out.println("t2");
				i--;
			}
		}) ;
		t2.setPriority(10);
		System.out.println(Thread.currentThread().getPriority());
		t1.start();
		t2.start();
		
	}

}
