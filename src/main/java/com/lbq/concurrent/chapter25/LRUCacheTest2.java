package com.lbq.concurrent.chapter25;

import java.util.concurrent.TimeUnit;
/**
 * 通过测试，我们发现LRU算法的基本功能都能满足，好了，该说我们的重点了。我们在前面定义了Reference类，创建一个Reference的实例会产生1MB的内存开销，
 * 当不断地往Cache中存放数据或者存放固定数量大小(capacity)的数据时，由于是Strong Reference的缘故，可能会引起内存溢出的情况。
 * 代码如下：
 * 为了方便测试，在程序启动时我们增加了如下几个JVM参数。
 * -Xmx128M:指定最大的堆内存大小。
 * -Xms64M:指定初始化的堆内存大小。
 * -XX:+PrintGCDetails:在控制台输出GC的详细信息。
 * 运行程序大约在插入了98个Reference左右的时候，JVM出现了堆内存溢出，输出如下：
 * The 98 reference stored at cache.
	[Full GC (Ergonomics) [PSYoungGen: 15680K->15362K(18944K)] [ParOldGen: 86705K->86704K(87552K)] 102386K->102067K(106496K), [Metaspace: 3609K->3609K(1056768K)], 0.0059882 secs] [Times: user=0.00 sys=0.00, real=0.01 secs] 
	[Full GC (Allocation Failure) [PSYoungGen: 15362K->15362K(18944K)] [ParOldGen: 86704K->86704K(87552K)] 102067K->102067K(106496K), [Metaspace: 3609K->3609K(1056768K)], 0.0023636 secs] [Times: user=0.00 sys=0.00, real=0.00 secs] 
	Exception in thread "main" Heap
	 PSYoungGen      total 18944K, used 15981K [0x00000000fd580000, 0x0000000100000000, 0x0000000100000000)
	  eden space 16384K, 97% used [0x00000000fd580000,0x00000000fe51b508,0x00000000fe580000)
	  from space 2560K, 0% used [0x00000000fe580000,0x00000000fe580000,0x00000000fe800000)
	  to   space 14336K, 0% used [0x00000000ff200000,0x00000000ff200000,0x0000000100000000)
	 ParOldGen       total 87552K, used 86704K [0x00000000f8000000, 0x00000000fd580000, 0x00000000fd580000)
	  object space 87552K, 99% used [0x00000000f8000000,0x00000000fd4ac330,0x00000000fd580000)
	 Metaspace       used 3640K, capacity 4624K, committed 4864K, reserved 1056768K
	  class space    used 403K, capacity 453K, committed 512K, reserved 1048576K
	java.lang.OutOfMemoryError: Java heap space
		at com.lbq.concurrent.chapter25.Reference.<init>(Reference.java:105)
		at com.lbq.concurrent.chapter25.LRUCacheTest2.lambda$0(LRUCacheTest2.java:13)
		at com.lbq.concurrent.chapter25.LRUCacheTest2$$Lambda$1/531885035.load(Unknown Source)
		at com.lbq.concurrent.chapter25.LRUCache.get(LRUCache.java:59)
		at com.lbq.concurrent.chapter25.LRUCacheTest2.main(LRUCacheTest2.java:15)
 * 98个Reference大约占用了100MB左右的堆内存大小，JVM自身启动时也需要加载和初始化很多对象实例。
 * 
 * 既然数据是被Cache的，那么能不能在JVM进行垃圾回收的时候帮助我们进行数据清除呢？当需要的时候再次加载就可以了，这就是我们接下来需要介绍的内容Soft Reference。
 * 
 * -Xmx128M -Xms64M -XX:+PrintGCDetails
 * @author 14378
 *
 */
public class LRUCacheTest2 {

	public static void main(String[] args) throws InterruptedException {
		LRUCache<Integer, Reference> cache = new LRUCache<>(200, key -> new Reference());
		for(int i = 0; i < Integer.MAX_VALUE; i++) {
			cache.get(i);
			TimeUnit.SECONDS.sleep(1);
			System.out.println("The " + i + " reference stored at cache.");
		}
	}

}
