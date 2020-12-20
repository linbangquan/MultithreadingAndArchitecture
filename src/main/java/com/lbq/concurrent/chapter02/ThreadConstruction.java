package com.lbq.concurrent.chapter02;
/**
 * Thread的所有构造函数，最终都会去调用一个静态方法init，我们截取片段代码对其进行分析，不难发现新创建的任何一个线程都会有一个父线程：
 * private void init(ThreadGroup g, Runnable target, String name, long stackSize, AccessControlContext acc){
 * 		if(name == null){
 * 			throw new NullPointerException("name cannot be null");
 * 		}
 * 		this.name = name.toCharArray();
 * 		Thread parent = currentThread();//获取当前线程作为父线程
 * 		SecurityManager security = System.getSecurityManager();
 * 		if(g == null){
 * 			if(security != null){
 * 				g = security.getThreadGroup();
 * 			}
 * 			if(g == null){
 * 				g = parent.getThreadGroup();
 * 			}
 * 		}
 * 		...
 * }
 * 上面代码中的currentThread()是获取当前线程，在线程生命周期中，我们说过线程的最初状态为NEW，没有执行start方法之前，
 * 它只能算是一个Thread的实例，并不意味着一个新的线程被创建，因此currentThread()代表的将会是创建它的那个线程，因此我们可以得出以下结论。
 * 一个线程的创建肯定是由另一个线程完成的。
 * 被创建线程的父线程是创建它的线程。
 * 我们都知道main函数所在的线程是由jvm创建的，也就是main线程，那意味着我们前面创建的所有线程，其父线程都是main线程。
 * 
 * 通对源码进行分析，我们可以看出，如果在构造Thread的时候没有显示地指定一个ThreadGroup，那么子线程将会被加入父线程所在的线程组。
 * main线程所在的ThreadGroup称为main。
 * 
 * 在默认设置中，当然除了子线程会和父线程同属一个Group之外，它还会和父线程拥有同样的优先级，同样的deamon。
 * 
 * @author 14378
 *
 */
public class ThreadConstruction {

	public static void main(String[] args) {
		Thread t1 = new Thread("t1");
		
		ThreadGroup group = new ThreadGroup("TestGroup");
		
		Thread t2 = new Thread(group, "t2");
		ThreadGroup mainThreadGroup = Thread.currentThread().getThreadGroup();
		System.out.println("Main thread belong group：" + mainThreadGroup.getName());
		System.out.println("t1 and main belong the same group：" + (mainThreadGroup == t1.getThreadGroup()));
		System.out.println("t2 thread group not belong main group：" + (mainThreadGroup == t2.getThreadGroup()));
		System.out.println("t2 thread group belong main TestGroup：" + (group == t2.getThreadGroup()));
	}

}
