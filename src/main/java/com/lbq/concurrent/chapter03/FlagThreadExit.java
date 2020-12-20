package com.lbq.concurrent.chapter03;

import java.util.concurrent.TimeUnit;
/**
 * 由于线程的interrupt标识很有可能被擦除，或者逻辑单元中不会调用任何可中断方法，
 * 所以使用volatile修饰的开关flag关闭线程也是一种常用的做法。
 * 
 * 异常退出
 * 在一个线程的执行单元中，是不允许抛出checked异常的，不论Thread中的run方法，还是Runnable中的run方法，
 * 如果线程在运行过程中需要捕获checked异常并且判断是否还有运行下去的必要，那么此时可以将checked异常封装成
 * unchecked异常（RuntimeException）抛出进而结束线程的生命周期。
 * 
 * 进程假死
 * 相信很多程序员都会遇到进程假死的情况，所谓假死就是进程虽然存在，但没有日志输出，程序不进行任何的作业，看起来就像死了一样，
 * 但事实上它是没有死的，程序之所以出现这样的情况，绝大部分的原因就是某个线程阻塞了，或者线程出现了死锁的情况。
 * 我们需要借助一些工具来帮助诊断，比如jstack、jconsole、jvisualvm等工具。
 * @author 14378
 *
 */
public class FlagThreadExit {

	static class MyTask extends Thread{
		private volatile boolean closed = false;
		
		@Override
		public void run() {
			System.out.println("I will start work");
			while(!closed && !isInterrupted()) {
				//正在运行
			}
			System.out.println("I will be exiting.");
		}
		
		public void close() {
			this.closed = true;
			this.interrupt();
		}
	}
	public static void main(String[] args) throws InterruptedException {
		MyTask t = new MyTask();
		t.start();
		TimeUnit.MINUTES.sleep(1);
		System.out.println("System will be shutdown.");
		t.close();
	}

}
