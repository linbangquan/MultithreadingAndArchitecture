package com.lbq.concurrent.chapter28;
/**
 * (2)EventExceptionHandler接口
 * EventBus会将方法的调用交给Runnable接口去执行，我们都知道Runnable接口不能抛出check异常信息，
 * 并且在每个subscribe方法中，也不允许将异常抛出从而影响EventBus对后续Subscriber进行消息推送，
 * 但是异常信息又不能被忽略掉，因此注册一个异常回调接口就可以知道在进行消息广播推送时都发生了什么。
 * @author 14378
 *
 */
public interface EventExceptionHandler {

	void handle(Throwable cause, EventContext context);
}
