package com.lbq.concurrent.chapter02;

import java.util.stream.IntStream;
/**
 * 在构造线程的时候可以为线程起一个有特殊意义的名字，尤其在一个线程比较多的程序中，为线程赋予一个包含特殊意义的名字有助于问题的排查和线程的跟踪。
 * 
 * 下面的几个构造函数中，并没有提供为线程命名的参数，那么此时线程会有一个怎样的命名？
 * Thread()
 * Thread(Runnable target)
 * Thread(ThreadGroup group, Runnable target)
 * 打开jdk的源码会看到下面的代码：
 * public Thread(Runnable target){
 * 		init(null, target, "Thread-", nextThreadNum(), 0);
 * }
 * //For autonumbering anonymous threads.
 * private static int threadInitNumber;
 * private static synchronized int nextThreadNum(){
 * 		return threadInitNumber++;
 * }
 * 如果没有为线程显式地指定一个名字，那么线程将会以“Thread-”作为前缀与一个自增数字进行组合，这个数字在整个jvm进程中将会不断自增：
 * 
 * 笔者强烈推荐在构造Thread的时候，为线程赋予一个特殊的名字是一种比较好的实战方式，Thread同样也提供了这样的构造函数，具体如下。
 * Thread(Runnable target, String name)
 * Thread(String name)
 * Thread(ThreadGroup group, Runnable target, String name)
 * Thread(ThreadGroup group, Runnable target, String name, long stackSize)
 * Thread(ThreadGroup group, String name)
 * 
 * 不论你使用的是默认的函数命名规则，还是指定了一个特殊的名字，在线程启动之前还有一个机会可以对其进行修改，一旦线程启动，名字将不再被修改，
 * 下面是Thread的setName源码：
 * public final synchronized void setName(String name){
 * 		checkAccess();
 * 		this.name = name.toCharArray();
 * 		if(threadStatus != 0){	//线程不是new状态，对其的修改将不会生效
 * 			setNativeName(name);
 * 		}
 * }
 * @author 14378
 *
 */
public class ThreadName {

	private final static String PREFIX = "ALEX-";
	public static void main(String[] args) {
		IntStream.range(0, 5).boxed().
		map(i -> new Thread(
				() -> System.out.println(Thread.currentThread().getName())
				)
		)
		.forEach(Thread::start);
//		Thread-0
//		Thread-3
//		Thread-4
//		Thread-1
//		Thread-2

		IntStream.range(0, 5).mapToObj(ThreadName::createThread).forEach(Thread::start);
//		ALEX-0
//		ALEX-1
//		ALEX-2
//		ALEX-3
//		ALEX-4
	}

	private static Thread createThread(final int intName) {
		return new Thread(() -> System.out.println(Thread.currentThread().getName()), PREFIX + intName);
	}
}

