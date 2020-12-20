package com.lbq.concurrent.chapter05;

import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
/**
 * 可中断被阻塞的线程
 * 运行下面的程序，在T2线程启动10毫秒以后，主动将其中断，T2线程会收到中断信号，也就是InterruptedException异常，
 * 这样也就弥补了Synchronized同步方式不可被中断的缺陷。
 * @author 14378
 *
 */
public class BooleanLockTest2 {
	/**
	 * 定义BooleanLock
	 */
	private final Lock lock = new BooleanLock();
	
	/**
	 * 使用try..finally语句块确保lock每次都能被正确释放
	 */
	public void syncMethod() {
		try {
			//加锁
			lock.lock();
			int randomInt = ThreadLocalRandom.current().nextInt(10);
			System.out.println(Thread.currentThread() + " get the lock.");
			TimeUnit.SECONDS.sleep(randomInt);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}finally {
			//释放锁
			lock.unlock();
		}
	}
	public static void main(String[] args) throws InterruptedException {
		BooleanLockTest2 blt = new BooleanLockTest2();
		new Thread(blt::syncMethod, "T1").start();
		TimeUnit.MILLISECONDS.sleep(2);
		Thread t2 = new Thread(blt::syncMethod, "T2");
		t2.start();
		TimeUnit.MILLISECONDS.sleep(10);
		t2.interrupt();
	}

}
