package com.lbq.concurrent.chapter29.chat;

import com.lbq.concurrent.chapter29.eventdriven.AsyncChannel;
import com.lbq.concurrent.chapter29.eventdriven.Event;
/**
 * UserChatEventChannel主要用于处理UserChatEvent事件。
 * 用户聊天的Event，直接在控制台输出即可。
 * @author 14378
 *
 */
public class UserChatEventChannel extends AsyncChannel {

	@Override
	protected void handle(Event message) {
		UserChatEvent event = (UserChatEvent) message;
		System.out.println("The User[" + event.getUser().getName() + "] say: " + event.getMessage());
	}

}
