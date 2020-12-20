package com.lbq.concurrent.chapter05;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeoutException;
/**
 * BooleanLock是Lock的一个Boolean实现，通过控制一个Boolean变量的开关来决定是否允许当前的线程获得该锁。
 * @author 14378
 *
 */
public class BooleanLock implements Lock{

	/**
	 * currentThread代表当前拥有锁的线程
	 */
	private Thread currentThread;
	/**
	 * locked是一个Boolean开关，false代表当前该锁没有被任何线程获得或者已经释放，true代表该锁已经被某个线程获得，该线程就是currentThread
	 */
	private boolean locked = false;
	/**
	 * blockedList用来存储哪些线程在获取当前线程时进入了阻塞状态。
	 */
	private final List<Thread> blockedList = new ArrayList<>();
	
	/**
	 * 1.Lock方法使用同步代码块的方式进行方法同步。
	 * 2.如果当前锁已经被某个线程获得，则该线程将加入阻塞队列，并且使当前线程wait释放对this monitor的所有权。
	 * 3.如果当前锁没有被其他线程获得，则该线程将尝试从阻塞队列中删除自己。
	 * 4.locked开关被指定为true。
	 * 5.记录获取锁的线程。
	 */
	@Override
	public void lock() throws InterruptedException {
		synchronized(this) {
			while(locked) {
				//暂存当前线程
				final Thread tempThread = Thread.currentThread();
				try {
					if(!blockedList.contains(Thread.currentThread())) {
						blockedList.add(Thread.currentThread());
					}
					this.wait();
				}catch(InterruptedException e) {
					//如果当前线程在wait时被中断，则从blockedList中将其删除，避免内存泄漏
					blockedList.remove(tempThread);
					System.out.println("tempThread:" + tempThread);
					System.out.println("tempThread:" + Thread.currentThread());
					//继续抛出中断异常
					throw e;
				}
				
			}
			blockedList.remove(Thread.currentThread());
			this.locked = true;
			this.currentThread = Thread.currentThread();
		}
		
	}

	/**
	 * 1.如果millis不合法，则默认调用lock()方法，当然也可以抛出参数非法的异常，一般来说，抛出异常是一种比较好的做法。
	 * 2.如果remainingMillis小于等于0，则意味着当前线程被其他线程唤醒或者在指定的wait时间到了之后还没有获得锁，这种情况下会抛出超时的异常。
	 * 3.等待remainingMillis的毫秒数，该值最开始是由其他线程传入的，但在多次wait的过程中会重新计算。
	 * 4.重新计算remainingMillis时间。
	 * 5.获得该锁，并且从block列表中删除当前线程，将locked的状态修改为true并且指定获得锁的线程就是当前线程。
	 */
	@Override
	public void lock(long millis) throws InterruptedException, TimeoutException {
		synchronized(this) {
			if(millis <= 0) {
				this.lock();
			}else {
				long remainingMillis = millis;
				long endMillis = System.currentTimeMillis() + remainingMillis;
				while(locked) {
					if(remainingMillis <= 0) {
						throw new TimeoutException("can not get the lock during " + millis + " ms.");
					}
					if(!blockedList.contains(Thread.currentThread())) {
						blockedList.add(Thread.currentThread());
					}
					this.wait(remainingMillis);
					remainingMillis = endMillis - System.currentTimeMillis();
				}
				blockedList.remove(Thread.currentThread());
				this.locked = true;
				this.currentThread = Thread.currentThread();
			}
		}
		
	}

	/**
	 * unlock()方法需要做的仅仅是将locked状态修改为false，并且唤醒wait set中的其他线程，再次争抢锁资源。
	 * 但是需要注意的一点是，哪个线程加的锁只能由该线程来解锁：
	 * 1.判断当前线程是否为获取锁的那个线程，只有加了锁的线程才有资格进行解锁。
	 * 2.将锁的locked状态修改为false。
	 * 3.通知其他在wait set中的线程，你们可以再次尝试抢锁了，这里使用notify和notifyAll都可以。
	 */
	@Override
	public void unlock() {
		synchronized(this) {
			if(currentThread == Thread.currentThread()) {
				this.locked = false;
				Optional.of(Thread.currentThread().getName() + " release the lock.").ifPresent(System.out::println);
				this.notifyAll();
			}
		}
		
	}

	@Override
	public List<Thread> getBlockedThreads() {
		return Collections.unmodifiableList(blockedList);
	}

}
