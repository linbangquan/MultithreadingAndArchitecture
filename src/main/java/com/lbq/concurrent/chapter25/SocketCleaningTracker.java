package com.lbq.concurrent.chapter25;

import java.io.IOException;
import java.lang.ref.PhantomReference;
import java.lang.ref.ReferenceQueue;
import java.net.Socket;
/**
 * 在25.2.1节中，我们对ClientHandler增加了Two Phase Termination的机制，目的是为了在客户端线程结束的时候确保socket资源能够被回收，
 * 但是socket.close()方法并不能百分之百的保证一定能够成功关闭socket资源，我们可以借助PhantomReference的特性，在垃圾回收器对socket
 * 对象进行回收的时候再次尝试一次清理，虽然也不能百分之百地保证资源能够彻底释放，但是这样做能够提高资源释放的概率，示例代码如下：
 * private void release() {
 * 		try {
 * 			if(socket != null){
 * 				socket.close();
 * 			}
 * 		}catch (Throwable e) {
 * 			if(socket != null){
 * 				//将socket实例加入Tracker中
 * 				SocketCleaningTracker.tracker(socket);
 * 			}
 * 		}
 * }
 * 
 * 在SocketCleaningTracker中启动Cleaner线程(Cleaner线程被设置为守护线程，清理动作一般是系统的清理工作，用于防止JVM无法正常关闭，如同JVM的GC线程的作用一样)，
 * 不断地从ReferenceQueue中remove Tracker实例(Tracker是一个PhantomReference的子类)，然后尝试进行最后的清理工作。
 * 
 * 25.4 本章总结
 * Two Phase Termination是一个比较好的编程技巧，在线程正常/异常结束生命周期的时候，尽最大的努力释放资源是程序设计人员的职责。
 * 在本章中，我们通过对聊天程序增加Two Phase Termination，当线程结束的时候尽最大的努力进行socket资源的释放，
 * PhantomReference是一种比finalize()方法更好的跟踪引用被释放的机制，为了使我们的知识点尽可能地系统化，
 * 我们顺便介绍了SoftReference、WeakReference，并且实现了一个基于LRU算法的Cache。
 * @author 14378
 *
 */
public class SocketCleaningTracker {
	//定义ReferenceQueue
	private static final ReferenceQueue<Object> queue = new ReferenceQueue<>();
	
	static {
		//启动Cleaner线程
		new Cleaner().start();
	}
	
	private static void tracker(Socket socket) {
		new Tracker(socket, queue);
	}
	
	private static class Cleaner extends Thread {
		private Cleaner() {
			super("SocketCleaningTracker");
			setDaemon(true);
		}
		
		@Override
		public void run() {
			for(;;) {
				try {
					//当Tracker被垃圾回收器回收时会加入Queue中
					Tracker tracker = (Tracker) queue.remove();
					tracker.close();
				} catch (InterruptedException e) {
				}
			}
		}
	}
	//Tracker是一个PhantomReference的子类
	private static class Tracker extends PhantomReference<Object> {
		private final Socket socket;
		
		Tracker(Socket socket, ReferenceQueue<? super Object> queue){
			super(socket, queue);
			this.socket = socket;
		}
		
		public void close() {
			try {
				socket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
