package com.lbq.concurrent.chapter06;

import java.util.concurrent.TimeUnit;
/**
 * ThreadGroup的基本操作
 * 1.activeCount()用于获取group中活跃的线程，这只是个估计值，并不能百分之百地保证数字一定正确，原因前面已经分析过，该方法会递归获取其他子group中活跃线程。
 * 2.activeGroupCount()用于获取group中活跃的子group，这也是一个近似估值，该方法也会递归获取所有的子group。
 * 3.getMaxPriority()用于获取group的优先级，默认情况下，Group的优先级为10，在该group中，所有线程的优先级都不能大于group的优先级。
 * 4.getName()用于获取group的名字。
 * 5.getParent()用于获取group的父group，如果父group不存在，则会返回null，比如system group的父group就为null。
 * 6.list()该方法没有返回值，执行该方法会将group中所有的活跃线程信息全部输出到控制台，也就是System.out。
 * 7.parentOf(ThreadGroup g)会判断当前group是不是给定group的父group，另外如果给定的group就是自己本身，那么该方法也会返回true。
 * 8.setMaxPriority(int pri)会指定group的最大优先级，最大优先级不能超过父group的最大优先级，执行该方法不仅会改变当前group的最大优先级，还会改变所有子group的最大优先级。
 * @author 14378
 *
 */
public class ThreadGroupBasic {

	public static void main(String[] args) throws InterruptedException {
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
		thread.setPriority(7);
		thread.start();
		
		TimeUnit.MILLISECONDS.sleep(1);
		
		ThreadGroup mainGroup = Thread.currentThread().getThreadGroup();
		
		System.out.println("activeCount=" + mainGroup.activeCount());
		System.out.println("activeGroupCount=" + mainGroup.activeGroupCount());
		System.out.println("getMaxPriority=" + mainGroup.getMaxPriority());
		System.out.println("getName=" + mainGroup.getName());
		System.out.println("getParent=" + mainGroup.getParent());
		mainGroup.list();
		System.out.println("-----------------------------------------------");
		System.out.println("parentOf=" + mainGroup.parentOf(group));
		System.out.println("parentOf=" + mainGroup.parentOf(mainGroup));
	}

}
