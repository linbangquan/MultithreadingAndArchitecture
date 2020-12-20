package com.lbq.concurrent.chapter29.chat;
/**
 * 代表用户发送了聊天信息的Event
 * UserChatEvent比其他两个Event多了代表聊天内容的message属性。
 * @author 14378
 *
 */
public class UserChatEvent extends UserOnlineEvent {
	//ChatEvent需要有聊天的信息
	private final String message;
	
	public UserChatEvent(User user, String message) {
		super(user);
		this.message = message;
	}

	public String getMessage() {
		return message;
	}

}
