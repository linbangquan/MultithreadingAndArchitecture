package com.lbq.concurrent.chapter01;
/**
 * 重写Thread类的run方法和实现Runnable接口的run方法还有一个很重要的不同，
 * 那就是Thread类的run方法是不能共享的，也就是说A线程不能把B线程的run方法当作自己的执行单元，
 * 而使用Runnable接口则很容易就能实现这一点，使用同一个Runnable的实例构造不同的Thread实例。
 * 
 * 不管是用static修饰index还是用实现Runnable接口的方式，这两个程序多运行几次或者MAX的值从50增加到500、1000或者更大
 * 都会出现一个号码出现两次的情况，也会出现某个号码根本不会出现的情况，更会出现超过最大值的情况，
 * 这是因为共享资源index存在线程安全的问题，我们在后面学习数据同步的时候会详细介绍。
 * @author 14378
 *
 */
public class TicketWindowRunnable implements Runnable {

	private int index = 1;//不做static修饰
	
	private final static int MAX = 50;
	
	@Override
	public void run() {
		while (index <= MAX) {
			System.out.println(Thread.currentThread() + " 的号码是：" + (index++));
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	public static void main(String[] args) {
		final TicketWindowRunnable task = new TicketWindowRunnable();
		
		Thread windowThread1 = new Thread(task, "一号窗口");
		Thread windowThread2 = new Thread(task, "二号窗口");
		Thread windowThread3 = new Thread(task, "三号窗口");
		Thread windowThread4 = new Thread(task, "四号窗口");
		
		windowThread1.start();
		windowThread2.start();
		windowThread3.start();
		windowThread4.start();
	}
}
