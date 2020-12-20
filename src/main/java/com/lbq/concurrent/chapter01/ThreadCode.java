package com.lbq.concurrent.chapter01;

import java.util.concurrent.TimeUnit;

/**
 * 对计算机来说每个任务就是一个进程(Process)，在每个进程内部至少要有一个线程(Thread)是在运行中，有时线程也称为轻量级的进程。
 * 线程是程序执行的一个路径，每个线程都有自己的局部变量表、程序计数器(指向正在执行的指令指针)以及各自的生命周期，
 * 现代操作系统中一般不止一个线程在运行，当启动了一个Java虚拟机(JVM)时，从操作系统开始就会创建一个新的进程(JVM进程)，
 * JVM进程中将会派生或者创建很多线程。
 * 
 * 创建一个线程，并且重写其run方法。启动新的线程，只有调用了Thread的start方法，才代表派生了一个新的线程，
 * 否则Thread和其他普通的Java对象没有什么区别，start方法是一个立即返回方法，并不会让程序陷入阻塞。
 * 
 * 线程可以借助Jconsole或者Jstack命令来查看，这两个JVM工具都是由jdk自身提供的。
 * 
 * 线程的生命周期大体可以分为如下5个主要的阶段：
 * new
 * runnable
 * running
 * blocked
 * terminated
 * 
 * 线程的new状态：
 * 当我们用关键字new创建一个Thread对象时，此时它并不处于执行状态，因为没有调用start方法启动该线程，那么线程的状态为new状态，
 * 准确地说，它只是Thread对象的状态，因为在没有start之前，该线程根本不存在，与你用关键字new创建一个普通的Java对象没什么区别。
 * new状态通过start方法进入runnable状态。
 * 
 * 线程的runnable状态：
 * 线程对象进入runnable状态必须调用start方法，那么此时才是真正地在JVM进程中创建了一个线程，
 * 线程已经启动就可以立即得到执行吗？答案是否定的，线程的运行与否和进程一样都要听令于cpu的调度，
 * 那么我们把这个中间状态称为可执行状态(runnable)，也就是说它具备执行的资格，但是并没有真正地执行起来
 * 而是在等待cpu的调度。
 * 由于存在runnable状态，所以不会直接进入blocked状态和terminated状态，
 * 即使是在线程的执行逻辑中调用wait、sleep或者其他block的IO操作等，也必须先获得cpu的调度执行权才可以，
 * 严格来讲，runnable的线程只能意外终止或者进入running状态。
 * 
 * 线程的running状态：
 * 一旦CPU通过轮询或者其他方式从任务可执行队列中选中了线程，那么此时它才能真正地执行自己的逻辑代码，
 * 需要说明的一点是一个正在running状态的线程事实上也是runnable的，但是反过来则不成立。
 * 		在该状态中，线程的状态可以发生如下的状态转换。
 * 		1.直接进入terminated状态，比如调用jdk已经不推荐使用的stop方法或者判断某个逻辑标识。
 * 		2.进入blocked状态，比如调用了sleep，或者wait方法而加入waitSet中。
 * 		3.进行某个阻塞的IO操作，比如因网络数据的读写而进入了blocked状态。
 * 		4.获取某个锁资源，从而加入到该锁的阻塞队列中而进入了blocked状态。
 * 		5.由于CPU的调度器轮询使该线程放弃执行，进入runnable状态。
 * 		6.线程主动调用yield方法，放弃CPU执行权，进入runnable状态。
 * 
 * 线程的blocked状态：
 * 上面已经列举了线程进入blocked状态的原因，此处就不再赘述了，线程在blocked状态中可以切换至如下几个状态。
 * 		1.直接进入terminated状态，比如调用jdk已经不推荐使用的stop方法或者意外死亡(JVM Crash)。
 * 		2.线程阻塞的操作结束，比如读取了想要的数据字节进入到runnable状态。
 * 		3.线程完成了指定时间的休眠，进入到了runnable状态。
 * 		4.wait中的线程被其他线程notify/notifyall唤醒，进入runnable状态。
 * 		5.线程获取到了某个锁资源，进入runnable状态。
 * 		6.线程在阻塞过程中被打断，比如其他线程调用了interrupt方法，进入runnable状态。
 * 
 * 线程的terminated状态
 * terminated是一个线程的最终状态，在该状态中线程将不会切换到其他如何状态，
 * 线程进入terminated状态，意味着该线程的整个生命周期都结束了，
 * 下列这些情况将会使线程进入terminated状态。
 * 		1.线程运行正常结束，结束生命周期。
 * 		2.线程运行出错意外结束。
 * 		3.JVM Crash，导致所有的线程都结束。
 * 
 * 总结：
 * 本章中最为重要的内容就是线程的生命周期，在使用多线程的过程中，线程的生命周期将会贯穿始终，
 * 只有清晰地掌握生命周期各个阶段的切换，才能更好地理解线程的阻塞以及唤醒机制，
 * 同时也为掌握同步锁等概念打下一个良好的基础。
 * 
 * @author 14378
 *
 */
public class ThreadCode implements RunnableCode{

	private int threadStatus = 0;
	private RunnableCode target = null;
	//private ThreadGroup group;
	private ThreadCode(RunnableCode _target) {
		this.target = _target;
	}
	public synchronized void start() {
		if(threadStatus != 0) {
			throw new IllegalThreadStateException();
		}
		//group.add(this);
		
		boolean started = false;
		try {
			start0();
			started = true;
		}finally {
			try {
				if(!started) {
					//group.threadStartFailed(this);
				}
			}catch(Throwable ignore) {
				
			}
		}
	}
	
	private native void start0();
	
	@Override
	public void run() {
		//如果构造Thread时传递了Runnable,则会执行runnable的run方法
		if(target != null) {//我们并没有使用runnable构造Thread
			target.run();
		}
		//否则需要重写Thread类的run方法
	}
	
	/**
	 * 程序同样会抛出IllegalThreadStateException异常，但是这两个异常的抛出却有本质上的区别，
	 * 第一个是重复启动，只是第二次启动是不允许的，但是此时该线程是处于运行状态的，
	 * 而第二次企图重新激活也抛出了非法状态的异常，但是此时没有线程，因为该线程的生命周期已经被终结。
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception{
		Thread thread = new Thread() {
			@Override
			public void run() {
				try {
					TimeUnit.SECONDS.sleep(10);
					System.out.println("线程运行结束");
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		};
		thread.start();//启动线程
		
		//thread.start();//再次启动，Exception in thread "main" java.lang.IllegalThreadStateException
		
		TimeUnit.SECONDS.sleep(11);//休眠主要是确保thread结束生命周期
		thread.start();//企图重新激活该线程，Exception in thread "main" java.lang.IllegalThreadStateException
	}
}

interface RunnableCode{
	void run();
}
