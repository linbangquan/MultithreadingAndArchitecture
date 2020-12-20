package com.lbq.concurrent.chapter19;
/**
 * FutureService接口设计
 * FutureService主要用于提交任务，提交的任务主要有两种，第一种不需要返回值，第二种则需要获得最终得计算结果。
 * FutureService接口中提供了对FutureServiceImpl构建得工厂方法，jdk8中不仅支持default方法还支持静态方法，
 * jdk9甚至还支持接口私有方法。
 * @author 14378
 *
 * @param <IN>
 * @param <OUT>
 */
public interface FutureService<IN, OUT> {
	//提交不需要返回值的任务，Future.get方法返回的将会是null
	Future<?> submit(Runnable runnable);
	//提交需要返回值的任务，其中Task接口代替了Runnable接口
	Future<OUT> submit(Task<IN, OUT> task, IN input);
	
	Future<OUT> submit(Task<IN, OUT> task, IN input, Callback<OUT> callback);
	//使用静态方法创建一个FutureService的实现
	static <T, R> FutureService<T, R> newService() {
		return new FutureServiceImpl<>();
	}
}
