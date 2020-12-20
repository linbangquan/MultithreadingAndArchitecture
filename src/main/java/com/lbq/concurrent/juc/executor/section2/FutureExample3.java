package com.lbq.concurrent.juc.executor.section2;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
/**
 * 获取异步执行任务的结果：当异步任务被正常执行完毕，可以通过get方法或者其重载方法（指定超时单位时间）获取最终的结果。
 * @author 14378
 *
 */
public class FutureExample3 {

	public static void main(String[] args) throws InterruptedException, ExecutionException {
		ExecutorService executor = Executors.newSingleThreadExecutor();
		Future<Double> future = executor.submit(() -> {
			TimeUnit.SECONDS.sleep(20);
			return 53.3d;
		});
		System.out.println("The task result:" + future.get());
		System.out.println("The task is done?" + future.isDone());
	}

}
