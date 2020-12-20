package com.lbq.concurrent.chapter06;
/**
 * 在Java程序中，默认情况下，新的线程都会被加入到main线程所在的group中，main线程的group名同线程名。
 * 如同线程存在父子关系一样，ThreadGroup同样也存在父子关系。
 * 无论如何，线程都会被加入某个ThreadGroup之中。
 * @author 14378
 *
 */
public class ThreadGroupCreator {

	public static void main(String[] args) {
		//①获取当前线程的group
		ThreadGroup currentGroup = Thread.currentThread().getThreadGroup();
		//②定义一个新的group
		ThreadGroup group1 = new ThreadGroup("Group1");
		//③程序输出true
		System.out.println(group1.getParent() == currentGroup);
		//④定义group2，指定group1为其父group
		ThreadGroup group2 = new ThreadGroup(group1, "Group2");
		//⑤程序输出true
		System.out.println(group2.getParent() == group1);
	}

}
