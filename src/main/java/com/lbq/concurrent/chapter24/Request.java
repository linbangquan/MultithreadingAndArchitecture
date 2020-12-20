package com.lbq.concurrent.chapter24;
/**
 * 24.2 每个任务一个线程
 * 在本节中，我们首先实现一个简单的Thread-Per-Message，但是在开发中不建议采用这种方式，在后文中笔者对此进行详细解释。
 * 
 * 客户提交的任何业务受理请求都会被封装成Request对象。
 * @author 14378
 *
 */
public class Request {

	private final String business;
	
	public Request(String business) {
		this.business = business;
	}
	
	@Override
	public String toString() {
		return business;
	}
}
