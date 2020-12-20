package com.lbq.concurrent.chapter06;

import java.util.concurrent.TimeUnit;
/**
 * 运行上面的程序，会出现thread的优先级大于所在group最大优先级的情况，如下所示：
 * group.getMaxPriority()=10
 * thread.getPriority()=5
 * group.getMaxPriority()=3
 * thread.getPriority()=5
 * 
 * 虽然出现了已经加入该group的线程的优先级大于group最大优先级的情况，
 * 但是后面加入该group的线程再不会大于新设置的值：3，这一点需要大家注意。
 * @author 14378
 *
 */
public class ThreadGroupPriority {

	public static void main(String[] args) {
		ThreadGroup group = new ThreadGroup("group1");
		Thread thread = new Thread(group, () -> {
			while(true) {
				try {
					TimeUnit.SECONDS.sleep(1);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}, "thread");
		thread.setDaemon(true);
		thread.start();
		
		System.out.println("group.getMaxPriority()=" + group.getMaxPriority());
		System.out.println("thread.getPriority()=" + thread.getPriority());
		group.setMaxPriority(3);
		System.out.println("group.getMaxPriority()=" + group.getMaxPriority());
		System.out.println("thread.getPriority()=" + thread.getPriority());
	}

}
