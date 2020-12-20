package com.lbq.concurrent.chapter29.chat;

import com.lbq.concurrent.chapter29.eventdriven.AsyncEventDispatcher;
/**
 * 在测试程序中， 我们创建了三个User线程并且启动。
 * 
 * 29.4 本章总结
 * Message(Event)无论是在同步还是异步的EDA中，我们都没有使用任何同步的方式对其进行控制，根本原因是Event被设计成了不可变对象，
 * 因为Event在经过每一个Channel(Handler)的时候都会创建一个全新的Event，多个线程之间不会出现资源竞争，因此不需要同步的保护。
 * 再联想之前我们学习过的生产者消费者模式，如果使用EDA框架对其进行修改，是否是不需要多个线程之间的交互，也不需要对队列的同步呢？
 * 有消息直接往EDA里面提交就可以了，消费的Handler自然而然地会触发消费Message。
 * @author 14378
 *
 */
public class UserChatApplication {

	public static void main(String[] args) {
		//定义异步的Router
		final AsyncEventDispatcher dispatcher = new AsyncEventDispatcher();
		//为Router注册Channel和Event之间的关系
		dispatcher.registerChannel(UserOnlineEvent.class, new UserOnlineEventChannel());
		dispatcher.registerChannel(UserOfflineEvent.class, new UserOfflineEventChannel());
		dispatcher.registerChannel(UserChatEvent.class, new UserChatEventChannel());
		//启动三个登录聊天室的User
		new UserChatThread(new User("Leo"), dispatcher).start();
		new UserChatThread(new User("Alex"), dispatcher).start();
		new UserChatThread(new User("Tina"), dispatcher).start();
	}

}
