package com.lbq.concurrent.chapter03;
/**
 * sleep是一个静态方法，其中有两个重载方法，其中一个需要传入毫秒数，另外一个既需要毫秒数页需要纳秒数。
 * 		1.public static void sleep(long millis) throws InterruptedException
 * 		2.public static void sleep(long millis, int nanos) throws InterruptedException
 * sleep方法会使当前线程进入指定的毫秒数的休眠，暂停执行，虽然给定了一个休眠的时间，但是最终要以系统的定时器和调度器的精度为准，
 * 休眠有一个非常重要的特性，那就是其不会放弃monitor锁的所有权。
 * 
 * 在下面的例子中，我们分别在自定义的线程和主线程中进行了休眠，每个线程的休眠互不影响，Thread.sleep只会导致当前线程进入指定时间的休眠。
 * 
 * 在jdk1.5以后，jdk引入了一个枚举TimeUnit，其对sleep方法提供了很好的封装，使用它可以省去时间单位的换算步骤，
 * 比如线程想休眠3小时24分17秒88毫秒，使用TimeUnit来实现就非常的简便优雅了：
 * Thread.sleep(12257088L);
 * TimeUnit.HOURS.sleep(3);
 * TimeUnit.MINUTES.sleep(24);
 * TimeUnit.SECONDS.sleep(17);
 * TimeUnit.MILLISECONDS.sleep(88);
 * @author 14378
 *
 */
public class ThreadSleep {

	public static void main(String[] args) {
		new Thread(() -> {
			long startTime = System.currentTimeMillis();
			sleep(2000L);
			long endTime = System.currentTimeMillis();
			System.out.println(String.format("Total spend %d ms", (endTime - startTime)));
		}).start() ;
		long startTime = System.currentTimeMillis();
		sleep(3000L);
		long endTime = System.currentTimeMillis();
		System.out.println(String.format("Total spend %d ms", (endTime - startTime)));
	}

	private static void sleep(long ms) {
		try {
			Thread.sleep(ms);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
	}

}
