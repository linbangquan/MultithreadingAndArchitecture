package com.lbq.concurrent.chapter23;

import java.util.concurrent.TimeUnit;
/**
 * 1.无限等待CountDownLatch实现
 * 下面来实现一个无限制等待门阀打开的Latch实现，当limit>0时调用await方法的线程将会进入无限的等待。
 * 
 * 在下述代码中，await()方法不断判断limit的数量，大于0时门阀将不能打开，需要持续等待直到limit数量为0为止；
 * countDown()方法调用之后会导致limit--操作，并通知wait中的线程再次判断limit的值是否等于0，
 * 当limit被减少到了0以下，则抛出状态非法的异常；
 * getUnarrived()获取当前还有多少个子任务未完成，这个返回值并不一定就是准确的，在多线程的情况下，
 * 某个线程在获得Unarrived任务数量并且返回之后，有可能limit又被减少，因此getUnarrived()是个评估值。
 * 
 * @author 14378
 *
 */
public class CountDownLatch extends Latch {

	public Runnable runnable;
	
	public CountDownLatch(int limit) {
		super(limit);
	}

	public CountDownLatch(int limit, Runnable runnable) {
		super(limit);
		this.runnable = runnable;
	}
	
	@Override
	public void await() throws InterruptedException {
		synchronized (this) {
			//当limit>0时，当前线程进入阻塞状态
			while(limit > 0) {
				this.wait();
			}
		}
		if(null != runnable) {
			runnable.run();
		}
	}

	@Override
	public void countDown() {
		synchronized(this) {
			if(limit <= 0) {
				throw new IllegalStateException("all of task already arrived");
			}
			//使limit减一，并且通知阻塞线程
			limit--;
			this.notifyAll();
		}
		
	}

	@Override
	public int getUnarrived() {
		//返回有多少线程还未完成任务
		return limit;
	}

	@Override
	public void await(TimeUnit unit, long time) throws InterruptedException, WaitTimeoutException {
		if(time <= 0) {
			throw new IllegalArgumentException("The time is invalid.");
		}
		//将time转换为纳秒
		long remainingNanos = unit.toNanos(time);
		//等待任务将在endNanos纳秒后超时
		final long endNanos = System.nanoTime() + remainingNanos;
		synchronized (this) {
			while(limit > 0) {
				//如果超时则抛出WaitTimeException异常
				if(TimeUnit.NANOSECONDS.toMillis(remainingNanos) <= 0) {
					throw new WaitTimeoutException("The wait time over specify time.");
				}
				//等待remainingNanosecond，在等待的过程中有可能会被中断，需要重新计算remainingNanos
				this.wait(TimeUnit.NANOSECONDS.toMillis(remainingNanos));
				remainingNanos = endNanos - System.nanoTime();
			}
		}
		if(null != runnable) {
			runnable.run();
		}
	}

}
