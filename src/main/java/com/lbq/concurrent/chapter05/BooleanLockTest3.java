package com.lbq.concurrent.chapter05;

import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
/**
 * 阻塞的线程可超时
 * 最后再写一个具有超时功能lock的使用示例，同样定义两个线程T1和T2，确保T1先执行能够最先获得锁，T2稍后启动，在1000ms以内未获得锁则会抛出超时异常。
 * @author 14378
 *
 */
public class BooleanLockTest3 {
	/**
	 * 定义BooleanLock
	 */
	private final Lock lock = new BooleanLock();
	
	/**
	 * 使用try..finally语句块确保lock每次都能被正确释放
	 */
	public void syncMethodTimeoutable() {
		try {
			//加锁
			lock.lock(1000);
			int randomInt = ThreadLocalRandom.current().nextInt(10);
			System.out.println(Thread.currentThread() + " get the lock.");
			TimeUnit.SECONDS.sleep(randomInt);
		} catch (InterruptedException | TimeoutException e) {
			e.printStackTrace();
		}finally {
			//释放锁
			lock.unlock();
		}
	}
	public static void main(String[] args) throws InterruptedException {
		BooleanLockTest3 blt = new BooleanLockTest3();
		new Thread(blt::syncMethodTimeoutable, "T1").start();
		TimeUnit.MILLISECONDS.sleep(2);
		Thread t2 = new Thread(blt::syncMethodTimeoutable, "T2");
		t2.start();
		TimeUnit.MILLISECONDS.sleep(10);
	}

}
