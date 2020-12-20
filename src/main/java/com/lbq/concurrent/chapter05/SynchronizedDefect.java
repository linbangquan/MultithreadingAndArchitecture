package com.lbq.concurrent.chapter05;

import java.util.concurrent.TimeUnit;
/**
 * synchronized关键字提供了一种排他式的数据同步机制，某个线程在获取monitor lock的时候可能会被阻塞，而这种阻塞有两个很明显的缺陷：
 * 第一：无法控制阻塞时长。
 * 第二：阻塞不可被中断。synchronized阻塞不像sleep和wait方法一样能够捕获得到中断信号。
 * @author 14378
 *
 */
public class SynchronizedDefect {

	public synchronized void syncMethod() {
		try {
			TimeUnit.HOURS.sleep(1);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) throws InterruptedException {
		SynchronizedDefect defect = new SynchronizedDefect();
		Thread t1 = new Thread(defect::syncMethod, "T1");
		t1.start();

		TimeUnit.MILLISECONDS.sleep(2);
		
		Thread t2 = new Thread(defect::syncMethod, "T2");
		t2.start();
	}

}
