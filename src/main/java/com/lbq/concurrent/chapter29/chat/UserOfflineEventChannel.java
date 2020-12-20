package com.lbq.concurrent.chapter29.chat;

import com.lbq.concurrent.chapter29.eventdriven.AsyncChannel;
import com.lbq.concurrent.chapter29.eventdriven.Event;
/**
 * UserOnlineEventChannel主要用于处理UserOfflineEvent事件。
 * 用户下线的Event，简单输出用户下线即可。
 * @author 14378
 *
 */
public class UserOfflineEventChannel extends AsyncChannel {

	@Override
	protected void handle(Event message) {
		UserOfflineEvent event = (UserOfflineEvent) message;
		System.out.println("The User[" + event.getUser().getName() + "] is offline.");
	}

}
