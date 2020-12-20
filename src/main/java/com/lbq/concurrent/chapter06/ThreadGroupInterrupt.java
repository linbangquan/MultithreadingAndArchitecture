package com.lbq.concurrent.chapter06;

import java.util.concurrent.TimeUnit;
/**
 * interrupt一个thread group会导致该group中所有的active线程都被interrupt，也就是说该group中每个线程的interrupt标识都被设置了，
 * 下面是ThreadGroup interrupt方法的源码：
 * public final void interrupt(){
 * 		int ngroupsSnapshot;
 * 		ThreadGroup[] groupsSnapshot;
 * 		synchronized(this){
 * 			checkAccess();
 * 			for(int i = 0; i < nthreads; i++){
 * 				threads[i].interrupt();
 * 			}
 * 			ngroupsSnapshot = ngroups;
 * 			if(groups != null){
 * 				groupsSnapshot = Arrays.copyOf(groups, ngroupsSnapshot);
 * 			}else{
 * 				groupsSnapshot = null;
 * 			}
 * 		}
 * 		for(int i = 0; i < ngroupsSnapshot; i++){
 * 			groupsSnapshot[i].interrupt();
 * 		}
 * }
 * 分析上述源码，我们可以看出在interrupt内部会执行所有thread的interrupt方法，并且会递归获取子group，然后执行它们各自的interrupt方法。
 * @author 14378
 *
 */
public class ThreadGroupInterrupt {

	public static void main(String[] args) throws InterruptedException {
		ThreadGroup group = new ThreadGroup("TestGroup");
		new Thread(group, () -> {
			while(true) {
				try {
					TimeUnit.MILLISECONDS.sleep(2);
				} catch (InterruptedException e) {
					break;
				}
			}
			System.out.println("t1 will exit.");
		}, "t1").start();

		new Thread(group, () -> {
			while(true) {
				try {
					TimeUnit.MILLISECONDS.sleep(1);
				} catch (InterruptedException e) {
					break;
				}
			}
			System.out.println("t2 will exit.");
		}, "t2").start();
		
		TimeUnit.MILLISECONDS.sleep(2);
		group.interrupt();
	}

}
