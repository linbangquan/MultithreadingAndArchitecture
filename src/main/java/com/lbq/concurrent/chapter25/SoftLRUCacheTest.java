package com.lbq.concurrent.chapter25;

import java.util.concurrent.TimeUnit;
/**
 * 使用同25.3.1节一样的JVM参数，上面的程序无论运行多久都不会出现JVM溢出的问题
 * (但是不代表SoftReference引用不会引起内存溢出，如果cache中插入的速度太快，那么GC线程没有来得及回收对象，很有可能也会引起溢出)。
 * 通过GC输出信息和Reference的finalize方法输出，我们可以断定Reference对象在不断地被回收，
 * 图25-3是JVM进程的内存监控图(锯齿状是最理想的JVM内存状态)。
 * -Xmx128M -Xms64M -XX:+PrintGCDetails
 * @author 14378
 *
 */
public class SoftLRUCacheTest {

	public static void main(String[] args) throws InterruptedException {
		SoftLRUCache<Integer, Reference> cache = new SoftLRUCache<>(1000, key -> new Reference());
		System.out.println(cache);
		for(int i = 0; i < Integer.MAX_VALUE; i++) {
			cache.get(i);
			TimeUnit.SECONDS.sleep(1);
			System.out.println("The " + i + " reference stored at cache.");
		}
	}

}
