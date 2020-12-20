package com.lbq.concurrent.chapter29.chat;

import com.lbq.concurrent.chapter29.eventdriven.Event;
/**
 * 代表用户上线的Event
 * @author 14378
 *
 */
public class UserOnlineEvent extends Event{

	private final User user;

	public UserOnlineEvent(User user) {
		this.user = user;
	}

	public User getUser() {
		return user;
	}
}
