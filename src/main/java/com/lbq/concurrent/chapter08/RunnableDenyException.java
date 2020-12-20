package com.lbq.concurrent.chapter08;
/**
 * RunnableDenyExcepiton是RuntimeException的子类，主要用于通知任务提交者，任务队列已无法再接收新的任务。
 * @author 14378
 *
 */
public class RunnableDenyException extends RuntimeException {
	
	private static final long serialVersionUID = 3706628427726561186L;
	
	public RunnableDenyException(String message) {
		super(message);
	}

}
