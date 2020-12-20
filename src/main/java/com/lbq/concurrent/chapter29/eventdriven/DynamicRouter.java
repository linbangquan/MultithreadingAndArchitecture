package com.lbq.concurrent.chapter29.eventdriven;
/**
 * (3) Dynamic Router
 * Router的作用类似于29.1节中的Event Loop，其主要是帮助Event找到合适的Channel并且传达给它。
 * Router如何知道要将Message分配给哪个Channel呢？换句话说，Router需要了解到Channel的存在，
 * 因此registerChannel()方法的作用就是将相应的Channel注册给Router，dispatch方法则是根据Message的类型进行路由匹配。
 * @author 14378
 *
 * @param <E>
 */
public interface DynamicRouter<E extends Message> {
	/**
	 * 针对每一种Message类型注册相关的Channel，只有找到合适的Channel该Message才会被处理
	 * @param messageType
	 * @param channel
	 */
	void registerChannel(Class<? extends E> messageType, Channel<? extends E> channel);
	/**
	 * 为相应的channel分配Message
	 * @param message
	 */
	void dispatch(E message);
}
