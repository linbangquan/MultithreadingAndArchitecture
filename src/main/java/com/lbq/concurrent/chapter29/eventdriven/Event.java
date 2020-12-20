package com.lbq.concurrent.chapter29.eventdriven;
/**
 * (4) Event
 * Event是对Message的一个最简单的实现，在以后的使用中，将Event直接作为其他Message的基类即可(这种做法有点类似于适配器模式)。
 * @author 14378
 *
 */
public class Event implements Message {

	@Override
	public Class<? extends Message> getType() {
		return getClass();
	}

}
