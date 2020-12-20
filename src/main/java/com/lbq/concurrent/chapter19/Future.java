package com.lbq.concurrent.chapter19;
/**
 * Future接口设计
 * Future提供了获取计算结果和判断任务是否完成的两个接口，其中获取计算结果将会导致调用阻塞(在任务还未完成的情况下)。
 * @author 14378
 *
 * @param <T>
 */
public interface Future<T> {
	//返回计算后的结果，该方法会陷入阻塞状态
	T get() throws InterruptedException;
	//判断任务是否已经被执行完成
	boolean done();
}
