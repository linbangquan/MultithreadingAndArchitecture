package com.lbq.concurrent.chapter28;
/**
 * 28.1.1 Bus接口详解
 * 
 * Bus接口定义了EventBus的所有使用方法
 * @author 14378
 *
 */
public interface Bus {
	/**
	 * 将某个对象注册到Bus上，从此之后该类就成为Subscriber了
	 * @param subscriber
	 */
	void register(Object subscriber);
	/**
	 * 将某个对象从Bus上取消注册，取消注册之后就不会再接收到来自Bus的任何消息
	 * @param subscriber
	 */
	void unregister(Object subscriber);
	/**
	 * 提交Event到默认的topic
	 * @param event
	 */
	void post(Object event);
	/**
	 * 提交Event到指定的topic
	 * @param event
	 * @param topic
	 */
	void post(Object event, String topic);
	/**
	 * 关闭该bus
	 */
	void close();
	/**
	 * 返回Bus的名称标识
	 * @return
	 */
	String getBusName();
}
