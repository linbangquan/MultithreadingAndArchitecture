package com.lbq.concurrent.chapter07;

import java.util.concurrent.TimeUnit;
/**
 * 7.1.3 UncaughtExceptionHandler源码分析
 * 在没有向线程注入UncaughtExceptionHandler回调接口的情况下，线程若出现了异常又将如何处理呢?
 * 本节中我们将通过对Thread的源码进行剖析来追踪一下，示例代码如下：
 * public UncaughtExceptionHandler getUncaughtExceptionHandler() {
 * 		return uncaughtExceptionHandler != null ? uncaughtExceptionHandler : group;
 * }
 * getUncaughtExceptionHandler方法首先会判断当前线程是否设置了handler，
 * 如果有则执行线程自己的uncaughtException方法，否则就到所在的ThreadGroup中获取，
 * ThreadGroup同样也实现了UncaughtExceptionHandler接口，下面再来看看ThreadGroup的uncaughtExcetpion方法。
 * 
 * public void uncaughtException(Thread t, Throwable e){
 * 		if(parent != null){
 * 			parent.uncaughtException(t, e);
 * 		}else{
 * 			Thread.UncaughtExceptionHandler ueh = Thread.getDefaultUncaughtExceptionHandler();
 * 			if(ueh != null){
 * 				ueh.uncaughtException(t, e);
 * 			}else if(!(e instanceof ThreadDeath)){
 * 				System.err.print("Exception in thread \"" + t.getName() + "\" ");
 * 				e.printStackTrace(System.err);
 * 			}
 * 		}
 * }
 * 该ThreadGroup如果有父ThreadGroup，则直接调用父Group的uncaughtException方法。
 * 如果设置了全局默认的UncaughtExceptionHandler，则调用uncaughtExcetpion方法。
 * 若既没有父ThreadGroup，也没有设置全局默认的UncaughtExceptionHandler，则会直接将异常的堆栈信息定向到System.err中。
 * 
 * 下面程序中没有设置默认的Handler，也没有对thread指定Handler，因此当thread出现异常时，会向上寻找Group的uncaughtException方法，
 * 线程出现异常——>MainGroup——>SystemGroup——>System.err
 * @author 14378
 *
 */
public class EmptyExceptionHandler {

	public static void main(String[] args) {
		// get current thread's thread group
		ThreadGroup mainGroup = Thread.currentThread().getThreadGroup();
		System.out.println(mainGroup.getName());
		System.out.println(mainGroup.getParent());
		System.out.println(mainGroup.getParent().getParent());
		
		final Thread thread = new Thread(() -> {
			try {
				TimeUnit.SECONDS.sleep(2);
			} catch (InterruptedException e) {
				
			}
			//here will throw unchecked exception.
			System.out.println(1/0);
		}, "Test-Thread");
		
		thread.start();
	}

}
