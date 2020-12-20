package com.lbq.concurrent.chapter05;

import java.util.List;
import java.util.concurrent.TimeoutException;
/**
 * 在本节中，我们将利用前面所学的知识，构造一个显式的BooleanLock，使其在具备synchronized关键字所有功能的同时又具备可中断和lock超时的功能。
 * 1.lock()方法永远阻塞，除非获取到了锁，这一点和synchronized非常类似，但是该方法是可以被中断的，中断时会抛出InterruptedException异常。
 * 2.lock(long millis)方法除了可以被中断以外，还增加了对应的超时功能。
 * 3.unlock()方法可用来进行锁的释放。
 * 4.getBlockedThreads()用于获取当前有哪些线程被阻塞。
 * @author 14378
 *
 */
public interface Lock {

	void lock() throws InterruptedException;
	
	void lock(long millis) throws InterruptedException, TimeoutException;
	
	void unlock();
	
	List<Thread> getBlockedThreads();
}
