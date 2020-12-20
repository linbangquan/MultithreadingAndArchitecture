package com.lbq.concurrent.chapter05;

import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;
/**
 * 多个线程通过lock()方法争抢锁
 * @author 14378
 *
 */
public class BooleanLockTest {
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
	public static void main(String[] args) {
		BooleanLockTest blt = new BooleanLockTest();
		//定义一个线程并且启动
		IntStream.range(0, 10).mapToObj(i -> new Thread(blt :: syncMethod)).forEach(Thread::start);

	}

}
