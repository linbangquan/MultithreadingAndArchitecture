package com.lbq.concurrent.chapter29.eventdriven;

import java.util.HashMap;
import java.util.Map;
/**
 * (5) EventDispatcher
 * EventDispatcher是对DynamicRouter的一个最基本的实现，适合在单线程的情况下进行使用，因此不需要考虑线程安全的问题。
 * 
 * EventDispatcher不是一个线程安全的类
 * 
 * 在EventDispatcher中有一个注册表routerTable，主要用于存放不同类型Message对应的Channel，
 * 如果没有于Message相对应的Channel，则会抛出无法匹配的异常。
 * @author 14378
 *
 */
public class EventDispatcher implements DynamicRouter<Message> {
	//用于保存Channel和Message之间的关系
	private final Map<Class<? extends Message>, Channel> routerTable;
	
	public EventDispatcher() {
		//初始化RouterTable，但是在该实现中，我们使用HashMap作为路由表
		this.routerTable = new HashMap<>();
	}
	@Override
	public void registerChannel(Class<? extends Message> messageType, Channel<? extends Message> channel) {
		this.routerTable.put(messageType, channel);
	}

	@Override
	public void dispatch(Message message) {
		if(routerTable.containsKey(message.getType())) {
			//直接获取对应的Channel处理Message
			routerTable.get(message.getType()).dispatch(message);
		}else {
			throw new MessageMatcherException("Can't match the channel for [" + message.getType() + "] type");
		}
	}

}
