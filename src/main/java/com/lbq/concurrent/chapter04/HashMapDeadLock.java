package com.lbq.concurrent.chapter04;

import java.util.HashMap;
/**
 * jdk中HashMap，文档很明显地指出了该数据结构不是线程安全的类，如果在多线程同时写操作的情况下不对其进行同步化封装，则很容易出现死循环引起的死锁，
 * 程序运行一段时间后CPU等资源高居不下，各种诊断工具很难派上用场，因为死锁引起的进程往往会榨干CPU等几乎所有资源，诊断工具由于缺少资源一时间也很难启动。
 * 
 * HashMap不具备线程安全的能力，如果想要使用线程安全的map结构请使用ConcurrentHashMap或者使用Collections.synchronizedMap来代替。
 * 
 * 运行HashMapDeadLock程序，也可以使用jstack、jconsole、jvisualvm工具或者jProfiler(收费的)工具进行诊断，但是不会给出很明显的提示，
 * 因为工作的线程并未blocked，而是始终处于runnable状态，CPU使用率高居不下，甚至都不能够正常运行你的命令。
 * 
 * 严格意义上来说，死循环会导致程序假死，算不上真正的死锁，但是某个线程对CPU消耗过多，导致其他线程等待CPU，内存等资源也会陷入死锁等待。
 * 
 * @author 14378
 *
 */
public class HashMapDeadLock {

	private final HashMap<String, String> map = new HashMap<>();
	
	public void add(String key, String value) {
		this.map.put(key, value);
	}
	
	public static void main(String[] args) {
		final HashMapDeadLock hmdl = new HashMapDeadLock();
		for(int x = 0; x < 2; x++) {
			new Thread(() -> {
				for(int i = 1; i < Integer.MAX_VALUE; i++) {
					hmdl.add(String.valueOf(i), String.valueOf(i));
				}
			}) .start();
		}
	}

}
