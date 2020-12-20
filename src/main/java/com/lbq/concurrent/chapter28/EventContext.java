package com.lbq.concurrent.chapter28;

import java.lang.reflect.Method;
/**
 * (3)EventContext接口
 * Event接口提供了获取消息源、消息体，以及该消息是由哪一个Subscriber的哪个subscribe方法所接受，
 * 主要用于消息推送出错时被回调接口EventExceptionHandler使用。
 * @author 14378
 *
 */
public interface EventContext {

	String getSource();
	Object getSubscriber();
	Method getSubscribe();
	Object getEvent();
}
