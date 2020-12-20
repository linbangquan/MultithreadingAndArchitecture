package com.lbq.concurrent.chapter28;

import java.lang.reflect.Method;
/**
 * 28.1.6 其他类接口设计
 * 除了上面一些比较核心的类之外，还需要Subscriber封装类以及EventContext、EventExceptionHandler接口。
 * 
 * (1) Subscriber类
 * Subscriber类封装了对象实例和被@Subscribe标记的方法，也就是说一个对象实例有可能会被封装成若干个Subscriber。
 * @author 14378
 *
 */
public class Subscriber {

	private final Object subscribeObject;
	private final Method subscribeMethod;
	private boolean disable =false;
	public Subscriber(Object subscribeObject, Method subscribeMethod) {
		this.subscribeObject = subscribeObject;
		this.subscribeMethod = subscribeMethod;
	}
	public boolean isDisable() {
		return disable;
	}
	public void setDisable(boolean disable) {
		this.disable = disable;
	}
	public Object getSubscribeObject() {
		return subscribeObject;
	}
	public Method getSubscribeMethod() {
		return subscribeMethod;
	}
}
