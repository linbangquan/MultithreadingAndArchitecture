package com.lbq.concurrent.chapter03;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
/**
 * Thread的join方法同样是一个非常重要的方法，使用它的特性可以实现很多比较强大的功能，与sleep一样它也是一个可中断的方法，
 * 也就是说，如果有其他线程执行了对当前线程的interrupt操作，它也会捕获到中断信号，并且擦除线程的interrupt标识。
 * Thread为我们提供了三个不同的join方法，具体如下，
 * public final void join() throws InterruptedException
 * public final synchronized void join(long millis, int nanos) throws InterruptedException
 * public final synchronized void join(long millis) throws interruptedException
 * 
 * join某个线程A，会使当前线程B进入等待，直到线程A结束生命周期，或者到达给定的时间，那么在此期间B线程是处于blocked的，而不是A线程。
 * 
 * join方法会使当前线程永远地等待下去，直到期间被另外的线程中断，或者join的线程执行结束，当然你也可以使用join的另外两个重载的方法，指定毫秒数，
 * 在指定的时间到达之后，当前线程也会退出阻塞。同样思考一个问题，如果一个线程已经结束了生命周期，那么调用它的join方法的当前线程会被阻塞吗？
 * @author 14378
 *
 */
public class ThreadJoin {

	public static void main(String[] args) throws InterruptedException{
		//1.定义两个线程，并保存在threads中
		List<Thread> threads = IntStream.range(1, 3).mapToObj(ThreadJoin::create).collect(Collectors.toList());
		
		//2.启动这两个线程
		threads.forEach(Thread::start);
		
		//执行这两个线程的join方法
		for(Thread thread : threads) {
			thread.join();
		}
		
		//4.main线程循环输出
		for(int i = 0; i < 10; i++) {
			System.out.println(Thread.currentThread().getName() + "#" + i);
			shortSleep();
		}
	}
	
	//构造一个简单的线程，每个线程只是简单的循环输出
	private static Thread create(int seq) {
		return new Thread(() -> {
			for(int i = 0; i < 10; i++) {
				System.out.println(Thread.currentThread().getName() + "#" + i);
				shortSleep();
			}
		}, String.valueOf(seq)) ;
	}

	private static void shortSleep() {
		try {
			TimeUnit.SECONDS.sleep(1);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
	}
}
