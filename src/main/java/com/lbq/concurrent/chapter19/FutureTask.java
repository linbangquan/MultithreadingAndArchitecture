package com.lbq.concurrent.chapter19;
/**
 * FutureTask是Future的一个实现，除了实现Future中定义的get()以及done()方法，
 * 还额外增加了protected方法finish，该方法主要用于接收任务被完成的通知。
 * FutureTask中充分利用了线程间的通信wait和notifyAll，当前任务没有被完成之前通过get方法获取结果，调用者进入阻塞，直到任务完成并接收到其他线程的唤醒信号，
 * finish方法接收到了任务完成通知，唤醒了因调用get而进入阻塞的线程。
 * @author 14378
 *
 * @param <T>
 */
public class FutureTask<T> implements Future<T> {
	//计算结果
	private T result;
	//任务是否完成
	private boolean isDone = false;
	//定义对象锁
	private final Object LOCK = new Object();
	
	@Override
	public T get() throws InterruptedException {
		synchronized (LOCK) {
			//当任务还没完成时，调用get方法会被挂起而进入阻塞
			while(!isDone) {
				LOCK.wait();
			}
			//返回最终计算结果
			return result;
		}
	}
	//返回当前任务是否已经完成
	@Override
	public boolean done() {
		return isDone;
	}
	//finish方法主要用于为FutureTask设置计算结果
	protected void finish(T result) {
		synchronized(LOCK) {
			//balking设计模式
			if(isDone) {
				return;
			}
			//计算完成，为result指定结果，并且将isDone设为true，同时唤醒阻塞中的线程
			this.result = result;
			this.isDone = true;
			LOCK.notifyAll();
		}
	}
}
