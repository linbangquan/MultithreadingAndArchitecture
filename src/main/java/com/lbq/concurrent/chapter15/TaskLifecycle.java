package com.lbq.concurrent.chapter15;
/**
 * TaskLifecycle接口定义了在任务执行的生命周期中会被触发的接口，其中EmptyLifecycle是一个空的实现，主要是为了让使用者保持对Thread类的使用习惯。
 * 1.onStart(Thread thread) 当任务开始执行时会被回调的方法。
 * 2.onRunning(Thread thread) 任务运行时被回调的方法，由于我们针对的是任务的生命周期，不同于线程的生命周期中的RUNNING状态，如果当前线程进入了休眠或者阻塞，那么任务都是running状态。
 * 3.onFinish(Thread thread, T result) 任务正确执行结束后会被调用，其中result是任务执行后的结果，可允许为null.
 * 4.onError(Thread thread, Exception e) 任务在运行过程中出现任何异常抛出时，onError方法都将被回调，并将异常信息一并传入。
 * @author 14378
 *
 * @param <T>
 */
public interface TaskLifecycle<T> {
	//任务启动时会触发onStart方法
	void onStart(Thread thread);
	
	//任务正在运行时会触发onRunning方法
	void onRunning(Thread thread);
	
	//任务运行结束时会触发onFinish方法，其中result是任务执行结束后的结果
	void onFinish(Thread thread, T result);
	
	//任务执行报错时会触发onError方法
	void onError(Thread thread, Exception e);
	//生命周期接口的空实现（Adapter）
	class EmptyLifecycle<T> implements TaskLifecycle<T>{

		@Override
		public void onStart(Thread thread) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onRunning(Thread thread) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onFinish(Thread thread, T result) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onError(Thread thread, Exception e) {
			// TODO Auto-generated method stub
			
		}
		
	}
}
