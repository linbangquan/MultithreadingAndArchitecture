package com.lbq.concurrent.chapter29.chat;
/**
 * 代表用户下线的Event
 * @author 14378
 *
 */
public class UserOfflineEvent extends UserOnlineEvent {

	public UserOfflineEvent(User user) {
		super(user);
	}

}
