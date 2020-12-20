package com.lbq.concurrent.chapter29.eventdriven;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
/**
 * 其次，还需要提供新的EventDispatcher类AsyncEventDispatcher负责以并发的方式dispatcher Message，
 * 其中Event对应的Channel只能是AsyncChannel类型，并且也对外暴露了shutdown方法。
 * 
 * 在AsyncEventDispatcher中，routerTable使用线程安全的Map定义，在注册Channel的时候，如果其不是AsyncChannel的类型，则会抛出异常。
 * @author 14378
 *
 */
public class AsyncEventDispatcher implements DynamicRouter<Event> {
	//使用线程安全的ConcurrentHashMap替换HashMap
	private final ConcurrentMap<Class<? extends Event>, AsyncChannel> routerTable;
	
	public AsyncEventDispatcher() {
		this.routerTable = new ConcurrentHashMap<>();
	}

	@Override
	public void registerChannel(Class<? extends Event> messageType, Channel<? extends Event> channel) {
		//在AsyncEventDispatcher中，Channel必须是AsyncChannel类型
		if(!(channel instanceof AsyncChannel)) {
			throw new IllegalArgumentException("The channel must be AsyncChannel Type.");
		}
		this.routerTable.put(messageType, (AsyncChannel) channel);
	}

	@Override
	public void dispatch(Event message) {
		if(routerTable.containsKey(message.getType())) {
			routerTable.get(message.getType()).dispatch(message);
		}else {
			throw new MessageMatcherException("Can't matcher the channel for [" + message.getType() + "] type.");
		}
	}

	public void shutdown() {
		//关闭所有的Channel以释放资源
		routerTable.values().forEach(AsyncChannel::stop);
	}
}
