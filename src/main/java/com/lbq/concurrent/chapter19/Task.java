package com.lbq.concurrent.chapter19;
/**
 * Task接口设计
 * Task接口主要是提供给调用者实现计算逻辑之用的，可以接受一个参数并且返回最终的计算结果，
 * 这一点非常类似于jdk1.5中的Callable接口。
 * @author 14378
 *
 * @param <IN>
 * @param <OUT>
 */
@FunctionalInterface
public interface Task<IN, OUT> {
	//给定一个参数，经过计算返回结果
	OUT get(IN input);
}
