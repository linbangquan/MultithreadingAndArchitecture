package com.lbq.concurrent.chapter29.chat;

import com.lbq.concurrent.chapter29.eventdriven.AsyncChannel;
import com.lbq.concurrent.chapter29.eventdriven.Event;
/**
 * 29.3.2 Chat Channel(Handler)
 * 所有的Handler都非常简单，只是将接收到的信息输出到控制台，由于是在多线程的环境运行，因此我们需要继承AsyncChannel。
 * 
 * UserOnlineEventChannel，主要用于处理UserOnlineEvent事件。
 * 用户上线的Event，简单输出用户上线即可。
 * @author 14378
 *
 */
public class UserOnlineEventChannel extends AsyncChannel {

	@Override
	protected void handle(Event message) {
		UserOnlineEvent event = (UserOnlineEvent) message;
		System.out.println("The User[" + event.getUser().getName() + "] is online.");
	}

}
