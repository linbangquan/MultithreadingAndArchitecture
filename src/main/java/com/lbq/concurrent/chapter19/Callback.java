package com.lbq.concurrent.chapter19;
/**
 * Callback接口非常简单，非常类似于jdk 8中的Consumer函数式接口。
 * @author 14378
 *
 * @param <T>
 */
@FunctionalInterface
public interface Callback<T> {
	//任务完成后会调用该方法，其中T为任务执行后的结果
	void call(T t);
}
