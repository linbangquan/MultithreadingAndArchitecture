package com.lbq.concurrent.chapter27;

import com.lbq.concurrent.chapter19.FutureTask;
/**
 * ActiveFuture非常简单，是FutureTask的直接子类，其主要作用是重写finish方法，并且将protected的权限换成public，可以使得执行线程完成任务之后传递最终结果。
 * @author 14378
 *
 * @param <T>
 */
public class ActiveFuture<T> extends FutureTask<T>{

	@Override
	public void finish(T result) {
		super.finish(result);
	}

}
