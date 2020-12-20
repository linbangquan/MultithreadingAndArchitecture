package com.lbq.concurrent.chapter06;

import java.util.concurrent.TimeUnit;
/**
 * 在一个ThreadGroup中会加入若干个线程以及子ThreadGroup，ThreadGroup为我们提供了若干个方法，可以复制出线程和线程组。
 * 复制Thread数组
 * public int enumerate(Thread[] list);
 * public int enumerate(Thread[] list, boolean recurse);
 * 上述两个方法，会将ThreadGroup中的active线程全部复制到Thread数组中，其中recurse参数如果为true，则该方法会将所有子group中的active线程都递归到Thread数组中，
 * enumerate(Thread[] list)实际上等价于enumerate(Thread[] list, true)，上面两个方法都调用了ThreadGroup的私有方法enumerate：
 * 
 * private int enumerate(Thread list[], int n, boolean recurse){
 * 		int ngroupsSnapshot = 0;
 * 		ThreadGroup[] groupsSnapshot = null;
 * 		synchronized (this){
 * 			if(destroyed){
 * 				return 0;
 * 			}
 * 			int nt = nthreads;
 * 			if(nt > list.length - n){
 * 				nt = list.length - n;
 * 			}
 * 			for(int i = 0; i < nt; i++){
 * 				if(threads[i].isAlice()){
 * 					list[n++] = threads[i];
 * 				}
 * 			}
 * 			if(recurse){
 * 				ngroupsSnapshot = ngroups;
 * 				if(groups != null){
 * 					groupsSnapShot = Arrays.copyOf(groups, ngroupsSnapshot);
 * 				}else{
 * 					groupsSnapshot = null;
 * 				}
 * 			}
 * 		}
 * 		if(recurse){
 * 			for(int i = 0; i < ngroupsSnapshot; i++){
 * 				n = groupsSnapshot[i].enumerate(list, n, true);
 * 			}
 * 		}
 * 		return n;
 * }
 * 
 * 1.enumerate方法获取的线程仅仅是个预估值，并不能百分之百地保证当前group的活跃线程，
 * 比如在调用复制之后，某个线程结束了生命周期或者新的线程加入了进来，都会导致数据的不准确。
 * 2.enumerate方法的返回值int相较Thread[]的长度更为真实，比如定义了数组长度的Thread数组，
 * 那么enumerate方法仅仅会将当前活跃的thread分别放进数组中，而返回值int则代表真实的数量，并非Thread数组的长度，
 * 可能是早期版本就有这个方法的缘故（jdk1.0），其实用List（jdk1.1版本才引入）会更好一些。
 * @author 14378
 *
 */
public class ThreadGroupEnumerateThreads {

	public static void main(String[] args) throws InterruptedException {
		//创建一个ThreadGroup
		ThreadGroup myGroup = new ThreadGroup("MyGroup");
		//创建线程传入threadGroup
		Thread thread = new Thread(myGroup, () -> {
			while(true) {
				try {
					TimeUnit.SECONDS.sleep(1);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}, "MyThread");
		thread.start();
		
		TimeUnit.MILLISECONDS.sleep(2);
		ThreadGroup mainGroup = Thread.currentThread().getThreadGroup();
		
		Thread[] list = new Thread[mainGroup.activeCount()];
		int recurseSize = mainGroup.enumerate(list);
		System.out.println(recurseSize);
		for(Thread t : list) {
			System.out.println(t);
		}
		Thread[] list2 = new Thread[mainGroup.activeCount()];
		recurseSize = mainGroup.enumerate(list2, false);
		System.out.println(recurseSize);
		for(Thread t : list2) {
			System.out.println(t);
		}
	}

}
