package com.lbq.concurrent.chapter25;

import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;
import java.util.concurrent.TimeUnit;
/**
 * 25.3.3 Weak Reference
 * 无论是young GC 还是full GC Weak Reference 的引用都会被垃圾回收器回收，Weak Reference(弱引用)可以用来做Cache，但是一般很少使用。
 * (1)任何类型的GC都可导致Weak Reference 对象被回收
 * Reference ref = new Reference();
 * WeakReference<Reference> reference = new WeakReference<>(ref);
 * ref = null;
 * System.gc();
 * 在上面的代码中我们定义了一个WeakReference，并且显式将ref设置为null，当调用gc方法时Weak Reference对象将被立即回收。
 * 
 * 
 * -XX:+PrintGCDetails
 * 
 * @author 14378
 *
 */
public class WeakReferenceTest {

	public static void main(String[] args) throws InterruptedException {
//		(1)任何类型的GC都可导致Weak Reference 对象被回收
//		Reference ref = new Reference();
//		WeakReference<Reference> reference = new WeakReference<>(ref);
//		ref = null;
//		//执行 GC操作
//		System.gc();
//		在上面的代码中我们定义了一个WeakReference，并且显式将ref设置为null，当调用gc方法时Weak Reference对象将被立即回收。
//		[GC (System.gc()) [PSYoungGen: 3020K->1704K(38400K)] 3020K->1712K(125952K), 0.0013563 secs] [Times: user=0.00 sys=0.00, real=0.00 secs] 
//		[Full GC (System.gc()) [PSYoungGen: 1704K->0K(38400K)] [ParOldGen: 8K->1544K(87552K)] 1712K->1544K(125952K), [Metaspace: 2630K->2630K(1056768K)], 0.0048634 secs] [Times: user=0.00 sys=0.00, real=0.01 secs] 
//		the reference will be GC.
//		Heap
//		 PSYoungGen      total 38400K, used 998K [0x00000000d5d00000, 0x00000000d8780000, 0x0000000100000000)
//		  eden space 33280K, 3% used [0x00000000d5d00000,0x00000000d5df9b20,0x00000000d7d80000)
//		  from space 5120K, 0% used [0x00000000d7d80000,0x00000000d7d80000,0x00000000d8280000)
//		  to   space 5120K, 0% used [0x00000000d8280000,0x00000000d8280000,0x00000000d8780000)
//		 ParOldGen       total 87552K, used 1544K [0x0000000081600000, 0x0000000086b80000, 0x00000000d5d00000)
//		  object space 87552K, 1% used [0x0000000081600000,0x0000000081782128,0x0000000086b80000)
//		 Metaspace       used 2638K, capacity 4486K, committed 4864K, reserved 1056768K
//		  class space    used 281K, capacity 386K, committed 512K, reserved 1048576K

//		(2)获取被垃圾回收器回收的对象
//		无论是SoftReference还是WeakReference引用，被垃圾回收器回收后，都会被存放到与之关联的ReferenceQueue中，代码如下：
		//被垃圾回收的Reference会被加入与之关联的Queue中
		ReferenceQueue<Reference> queue = new ReferenceQueue<>();
		Reference ref = new Reference();
		//定义WeakReference并且指定关联的Queue
		WeakReference<Reference> reference = new WeakReference<>(ref, queue);
		ref = null;
		System.out.println(reference.get());
		
		//手动执行gc操作
		System.gc();
		TimeUnit.SECONDS.sleep(1);
		//remove方法是阻塞方法
		java.lang.ref.Reference<? extends Reference> gcedRef = queue.remove();
		//被垃圾回收之后，会从队列中获得
		System.out.println(gcedRef);
//		在上面的代码中，我们定义了WeakReference，同时传入了ReferenceQueue队列，当有对象被回收的时候，WeakReference实例会被加入到ReferenceQueue中，输出如下
//		com.lbq.concurrent.chapter25.Reference@15db9742
//		[GC (System.gc()) [PSYoungGen: 3020K->1672K(38400K)] 3020K->1680K(125952K), 0.0011941 secs] [Times: user=0.00 sys=0.00, real=0.00 secs] 
//		[Full GC (System.gc()) [PSYoungGen: 1672K->0K(38400K)] [ParOldGen: 8K->1544K(87552K)] 1680K->1544K(125952K), [Metaspace: 2632K->2632K(1056768K)], 0.0051843 secs] [Times: user=0.02 sys=0.00, real=0.01 secs] 
//		the reference will be GC.
//		java.lang.ref.WeakReference@6d06d69c
//		Heap
//		 PSYoungGen      total 38400K, used 1664K [0x00000000d5d00000, 0x00000000d8780000, 0x0000000100000000)
//		  eden space 33280K, 5% used [0x00000000d5d00000,0x00000000d5ea0198,0x00000000d7d80000)
//		  from space 5120K, 0% used [0x00000000d7d80000,0x00000000d7d80000,0x00000000d8280000)
//		  to   space 5120K, 0% used [0x00000000d8280000,0x00000000d8280000,0x00000000d8780000)
//		 ParOldGen       total 87552K, used 1544K [0x0000000081600000, 0x0000000086b80000, 0x00000000d5d00000)
//		  object space 87552K, 1% used [0x0000000081600000,0x0000000081782370,0x0000000086b80000)
//		 Metaspace       used 2666K, capacity 4486K, committed 4864K, reserved 1056768K
//		  class space    used 286K, capacity 386K, committed 512K, reserved 1048576K
	}

}
