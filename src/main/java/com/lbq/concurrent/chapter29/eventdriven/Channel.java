package com.lbq.concurrent.chapter29.eventdriven;
/**
 * (2) Channel
 * 第二个比较重要的概念就是Channels，channel主要用于接受来自Event Loop分配的消息，
 * 每一个Channel负责处理一种类型的消息(当然这取决于你对消息如何进行分配)。
 * @author 14378
 *
 * @param <E>
 */
public interface Channel<E extends Message> {
	/**
	 * dispatch方法用于负责Message的调度
	 * @param message
	 */
	void dispatch(E message);
}
