package com.lbq.concurrent.chapter23;
/**
 * 当子任务线程执行超时的时候将会抛出该异常
 * @author 14378
 *
 */
public class WaitTimeoutException extends Exception {

	private static final long serialVersionUID = -6602951446811996783L;

	public WaitTimeoutException(String message) {
		super(message);
	}
}
