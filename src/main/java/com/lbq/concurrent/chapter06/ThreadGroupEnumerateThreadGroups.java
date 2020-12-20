package com.lbq.concurrent.chapter06;

import java.util.concurrent.TimeUnit;
/**
 * 复制ThreadGroup数组
 * public int enumerate(ThreadGroup[] list);
 * public int enumerate(ThreadGroup[] list, boolean recurse);
 * 和复制Thread数组类似，上述两个方法，主要用于复制当前ThreadGroup的子Group，同样recurse会决定是否已递归的方式复制。
 * @author 14378
 *
 */
public class ThreadGroupEnumerateThreadGroups {

	public static void main(String[] args) throws InterruptedException {
		ThreadGroup myGroup1 = new ThreadGroup("MyGroup1");
		ThreadGroup myGroup2 = new ThreadGroup(myGroup1, "MyGroup2");
		
		TimeUnit.MILLISECONDS.sleep(2);
		ThreadGroup mainGroup = Thread.currentThread().getThreadGroup();
		
		ThreadGroup[] list = new ThreadGroup[mainGroup.activeGroupCount()];	
		int recurseSize = mainGroup.enumerate(list);
		System.out.println(recurseSize);
		for(ThreadGroup tg : list) {
			System.out.println(tg);
		}
		
		ThreadGroup[] list2 = new ThreadGroup[mainGroup.activeGroupCount()];	
		recurseSize = mainGroup.enumerate(list2, false);
		System.out.println(recurseSize);
		for(ThreadGroup tg : list2) {
			System.out.println(tg);
		}
	}

}
