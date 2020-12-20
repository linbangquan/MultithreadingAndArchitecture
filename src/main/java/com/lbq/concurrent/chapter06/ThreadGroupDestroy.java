package com.lbq.concurrent.chapter06;
/**
 * destroy用于销毁ThreadGroup，该方法只是针对一个没有任何active线程的group进行一次destroy标记，调用该方法的直接结果是在父group中将自己移除：
 * （销毁ThreadGroup及其子ThreadGroup，在该ThreadGroup中所有的线程必须是空的，也就是说ThreadGroup或者子ThreadGroup所有的线程都已经停止运行，
 * 如果有active线程存在，调用destroy方法则抛出异常。）
 * @author 14378
 *
 */
public class ThreadGroupDestroy {

	public static void main(String[] args) {
		ThreadGroup group = new ThreadGroup("TestGroup");
		
		ThreadGroup mainGroup = Thread.currentThread().getThreadGroup();
		System.out.println("group.isDestroyed=" + group.isDestroyed());
		mainGroup.list();
		
		group.destroy();
		
		System.out.println("group.isDestroyed=" + group.isDestroyed());
		mainGroup.list();
	}

}
