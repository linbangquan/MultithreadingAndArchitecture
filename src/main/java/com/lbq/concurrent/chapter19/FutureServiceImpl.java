package com.lbq.concurrent.chapter19;

import java.util.concurrent.atomic.AtomicInteger;
/**
 * FutureServiceImpl的主要作用在于当提交任务时创建一个新的线程来受理该任务，进而达到任务异步执行的效果。
 * 
 * 在FutureServiceImpl的submit方法中，分别启动了新的线程运行任务，起到了异步的作用，在任务最终运行成功之后，会通知FutureTask任务已完成。
 * @author 14378
 *
 * @param <IN>
 * @param <OUT>
 */
public class FutureServiceImpl<IN, OUT> implements FutureService<IN, OUT> {
	//为执行的线程指定名字前缀(再三强调，为线程起一个特殊的名字是一个非常好的编程习惯)
	private final static String FUTURE_THREAD_PREFIX = "FUTURE-";
	
	private final AtomicInteger nextCounter = new AtomicInteger(0);
	
	private String getNextName() {
		return FUTURE_THREAD_PREFIX + nextCounter.getAndIncrement();
	}
	
	@Override
	public Future<?> submit(Runnable runnable) {
		final FutureTask<Void> future = new FutureTask<>();
		new Thread(() -> {
			runnable.run();
			//任务执行结束之后将null作为结果传给future
			future.finish(null);
		}, getNextName()).start();
		return future;
	}

	@Override
	public Future<OUT> submit(Task<IN, OUT> task, IN input) {
		final FutureTask<OUT> future = new FutureTask<>();
		new Thread(() -> {
			OUT result = task.get(input);
			//任务执行结束之后，将真实的结果通过finish方法传递给future
			future.finish(result);
		}).start();
		return future;
	}
	//增加回调接口Callback，当任务执行结束之后，Callback会得到执行
	@Override
	public Future<OUT> submit(Task<IN, OUT> task, IN input, Callback<OUT> callback) {
		final FutureTask<OUT> future = new FutureTask<>();
		new Thread(() -> {
			OUT result = task.get(input);
			future.finish(result);
			//执行回调接口
			if(null != callback) {
				callback.call(result);
			}
		}, getNextName()).start();
		return future;
	}

}
