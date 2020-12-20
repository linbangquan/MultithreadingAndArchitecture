package com.lbq.concurrent.chapter07;

import java.util.concurrent.TimeUnit;
/**
 * 本章中，将介绍如何获取线程在运行时期的异常信息，以及如何向Java程序注入Hook线程(Hook通常也称钩子)。
 * 7.1 获取线程运行时异常
 * 	在Thread类中，关于处理运行时异常的API总共有四个，如下所示：
 * 	1.public void setUncaughtExceptionHandler(UncaughtExceptionHandler eh): 为某个特定线程指定UncaughtExceptionHandler。
 * 	2.public static void setDefaultUncaughtExceptionHandler(UncaughtExceptionHandler eh): 设置全局的UncaughtExceptionHandler。
 * 	3.public UncaughtExceptionHandler getUncaughtExceptionHandler():获取特定线程的UncaughtExceptionHandler。
 * 	4.public static UncaughtExceptionHandler getDefaultUncaughtExceptionHandler():获取全局的UncaughtExcetpionHandler。
 * 
 * 7.1.1 UncaughtExceptionHandler的介绍
 * 线程在执行单元中是不允许抛出checked异常的，这一点前文中已经有过交代，而且线程运行在自己的上下文中，派生它的线程将无法直接获得它的运行中出现的异常信息。
 * 对此，Java为我们提供了一个UncaughtExceptionHandler接口，当线程在运行过程中出现异常时，会回调UncaughtExceptionHandler接口，
 * 从而得知是哪个线程在运行时出错，以及出现了什么样的错误，示例代码如下：
 * @FunctionalInterface
 * public interface UncaughtExceptionHandler {
 * 		void uncaughtException(Thread t, Throwable e);
 * }
 * 在上述代码中，UncaughtExceptionHandler是一个FunctionalInterface，只有一个抽象方法，该回调接口会被Thread中的dispatchUncaughtException方法调用，
 * 如下所示：
 * private void dispatchUncaughtException(Throwable e){
 * 		getUncaughtExceptionHandler().uncaughtException(this, e);
 * }
 * 当前线程在运行过程中出现异常时，JVM会调用dispatchUncaughtException方法，该方法会将对应的线程实例以及异常信息传递给回调接口。
 * 
 * 执行下面的程序，线程Test-Thread在运行两秒之后会抛出一个unchecked异常，我们设置的回调接口将获得该异常信息，程序的执行结果如下：
 * Test-Thread occur exception2
 * java.lang.ArithmeticException: / by zero
 *	at com.lbq.concurrent.chapter07.CaptureThreadException.lambda$1(CaptureThreadException.java:25)
 *	at java.lang.Thread.run(Thread.java:748)
 * 
 * 在平时的工作中，这种设计方式是比较常见的，尤其是那种异步执行方法，比如Google的guava toolkit就提供了EventBus，
 * 在EventBus中事件源和实践的subscriber两者借助于EventBus实现了完全的解耦合，但是subscriber执行任务时有可能会出现异常情况，
 * EventBus同样也是借助于一个ExceptionHandler进行回调处理的。
 * @author 14378
 *
 */
public class CaptureThreadException {

	public static void main(String[] args) {
		//①设置回调接口
		Thread.setDefaultUncaughtExceptionHandler((t, e) ->{
			System.out.println(t.getName() + " occur exception");
			e.printStackTrace();
		});
		final Thread thread = new Thread(() -> {
			try {
				TimeUnit.SECONDS.sleep(2);
			}catch(InterruptedException e) {
				
			}
//			InputStream in = new FileInputStream("");//Unhandled exception type FileNotFoundException
			//②这里会出现unchecked异常
			//here will throw unchecked exception.
			System.out.println(1/0);
		}, "Test-Thread") ;

		thread.setUncaughtExceptionHandler((t, e) -> {
			System.out.println(t.getName() + " occur exception2");
			e.printStackTrace();
		});
		thread.start();
	}

}
