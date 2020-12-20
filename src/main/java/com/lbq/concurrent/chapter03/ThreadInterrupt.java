package com.lbq.concurrent.chapter03;

import java.util.concurrent.TimeUnit;
/**
 * 线程interrupt，是一个非常重要的API，也是经常使用的方法，与线程中断相关的API有如下几个：
 * public void interrupt()
 * public static boolean interrupted()
 * public boolean isInterrupted()
 * 
 * 如下方法的调用会使得当前线程进入阻塞状态，而调用当前线程的interrupt方法，就可以打断阻塞。
 * Object的wait方法。
 * Object的wait(long)方法。
 * Object的wait(long, int)方法。
 * Thread的sleep(long)方法。
 * Thread的sleep(long, int)方法。
 * Thread的join方法。
 * Thread的join(long)方法。
 * Thread的join(long, int)方法。
 * InterruptibleChannel的io操作。
 * Selector的wakeup方法。
 * 其他方法。
 * 
 * 上述若干方法都会使得当前线程进入阻塞状态，若另外的一个线程调用被阻塞线程的interrupt方法，则会打断这种阻塞，因此这种方法有时会被称为可中断方法，
 * 记住，打断一个线程并不等于该线程的生命周期结束，仅仅是打断了当前线程的阻塞状态。
 * 
 * 一旦一个线程在阻塞的情况下被打断，都会抛出一个称为InterruptedException的异常，这个异常就像一个signal(信号)一样通知当前线程被打断了。
 * 
 * interrupt这个方法到底做了什么样的事情呢?
 * 一个线程内部存在着名为interrupt flag的标识，如果一个线程被interrupt，那么它的flag将被设置，
 * 但是如果当前线程正在执行可中断方法被阻塞时，调用interrupt方法将其中断，反而会导致flag被清除，
 * 关于这点我们在后面还会做做详细的介绍。另外有一点需要注意的是，如果一个线程已经是死亡状态，那么尝试对其的interrupt会直接被忽略。
 * @author 14378
 *
 */
public class ThreadInterrupt {

	public static void main(String[] args) throws InterruptedException {
		Thread thread = new Thread() {
			@Override
			public void run() {
				try {
					System.out.println(Thread.currentThread().getName() +":"+isInterrupted());
					TimeUnit.MINUTES.sleep(1);
					System.out.println("---------");
				} catch (InterruptedException e) {
					System.out.println("Oh, i am be interrupted.");
					System.out.println(Thread.currentThread().getName() +":"+isInterrupted());
				}finally {
					System.out.println("#######");
				}
				System.out.println("++++++++++++++++++++");
			}
		};
		thread.start();
		Thread thread2 = new Thread() {
			@Override
			public void run() {
				try {
					while(!isInterrupted()) {
						System.out.println("---------");
					}
				} catch (Exception e) {
					System.out.println("Oh, i am be interrupted.");
				}finally {
					System.out.println("#######");
				}
				System.out.println("++++++++++++++++++++");
			}
		};
		thread2.start();
		TimeUnit.MILLISECONDS.sleep(2);
		System.out.println(thread.getName() +":"+thread.isInterrupted());
		thread.interrupt();
		System.out.println(thread.getName() +":"+thread.isInterrupted());
		thread2.interrupt();
	}

}
