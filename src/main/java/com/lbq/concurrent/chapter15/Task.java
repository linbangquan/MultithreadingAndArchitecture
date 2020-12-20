package com.lbq.concurrent.chapter15;
/**
 * 由于我们需要对线程中的任务执行增加可观察的能力，并且需要获取最后的计算结果，因此Runnable接口在可观察的线程中将不再使用，
 * 取而代之的是Task接口，其作用与Runnable类似，主要用于承载任务的逻辑执行单元。
 * @author 14378
 *
 * @param <T>
 */
public interface Task<T> {
	//任务执行接口，该接口允许有返回值
	T call();
}
