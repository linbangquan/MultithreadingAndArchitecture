package com.lbq.concurrent.chapter08;

import java.util.concurrent.TimeUnit;
/**
 * 自JDK1.5起，utils包提供了ExecutorService线程池的实现，主要目的是为了重复利用线程，提供系统效率。
 * 通过前文的学习我们得知Thread是一个重量级的资源，创建、启动以及销毁都是比较耗费系统资源的，
 * 因此对线程的重复利用是一种非常好的程序设计习惯，加之系统中可创建的线程数量是有限的，线程数量和系统性能是一种抛物线的关系，
 * 也就是说当线程数量达到某个数值的时候，性能反倒会降低很多，因此对线程的管理，尤其是数量的控制更能直接决定程序的性能。
 * 
 * 在本书中，不会讲解JUC（JDK的Java utilities concurrent），而是从原理入手，设计一个线程池，其目的并不是重复地发明轮子，
 * 而是为了帮助读者弄清楚一个线程池应该具备哪些功能，线程池的实现需要注意哪些细节。
 * 
 * 所谓线程池，通俗的理解就是有一个池子，里面存放着已经创建好的线程，当有任务提交给线程池执行时，池子中的某个线程会主动执行该任务。
 * 如果池子中的线程数量不够应付数量众多的任务时，则需要自动扩充新的线程到池子中，但是该数量是有限的，就好比池塘的水界线一样。
 * 当任务比较少的时候，池子中线程能够自动回收，释放资源。为了能够异步地提交任务和缓存未被处理的任务，需要有一个任务队列。
 * 
 * 通过上面的描述可知，一个完整的线程池应该具备如下要素。
 * 1.任务队列：用于缓存提交的任务。
 * 2.线程数量管理功能：一个线程池必须能够很好地管理和控制线程数量，可通过如下三个参数来实现，比如创建线程池时初始的线程数量init;
 * 	线程池自动扩充时最大的线程数量max；在线程池空闲时需要释放线程但是也要维护一定数量的活跃数量或者核心数量core。有了这三个参数，
 * 就能够很好地控制线程池中的线程数量，将其维护在一个合理的范围之内，三者之间的关系是init<=core<=max。
 * 3.任务拒绝策略：如果线程数量已达到上限且任务队列已满，则需要有相应的拒绝策略来通知任务提交者。
 * 4.线程工厂：主要用于个性化定制线程，比如将线程设置为守护线程以及设置线程名称等。
 * 5.QueueSize：任务队列主要存放提交的Runnable，但是为了防止内存溢出，需要有limit数量对其进行控制。
 * 6.Keepedalive时间：该时间主要决定线程各个重要参数自动维护的时间间隔。
 * @author 14378
 *
 */
public class ThreadPoolTest {

	public static void main(String[] args) throws InterruptedException {
		//定义线程池，初始化线程数为2，核心线程数为4，最大线程数为6，任务队列最多允许1000个任务
		final ThreadPool threadPool = new BasicThreadPool(2, 6, 4, 1000);
		//定义20个任务并且提交给线程池
		for(int i = 0; i < 20; i++) {
			threadPool.execute(() -> {
				try {
					TimeUnit.SECONDS.sleep(10);
					System.out.println(Thread.currentThread().getName() + " is running and done.");
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			});		
		}
//		for(;;) {
//			//不断输出线程池的信息
//			System.out.println("getActiveCount:" + threadPool.getActiveCount());
//			System.out.println("getQueueSize:" + threadPool.getQueueSize());
//			System.out.println("getCoreSize:" + threadPool.getCoreSize());
//			System.out.println("getMaxSize:" + threadPool.getMaxSize());
//			System.out.println("=============================================");
//			TimeUnit.SECONDS.sleep(5);
//		}
		
		TimeUnit.SECONDS.sleep(12);
		//线程池在12秒之后将被shutdown
		threadPool.shutdown();
		//使main线程join，方便通过工具观察线程堆栈信息
		Thread.currentThread().join();
	}

}
