package com.lbq.concurrent.chapter29.chat;

import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

import com.lbq.concurrent.chapter29.eventdriven.AsyncEventDispatcher;
/**
 * 29.3.3 Chat User线程
 * 我们定义完Event和接受Event的Channel后，现在定义一个代表聊天室的参与者的User线程。
 * 当User线程启动的时候，首先发送Online Event，然后发送五条聊天信息，之后下线，在下线的时候发送Offline Event。
 * @author 14378
 *
 */
public class UserChatThread extends Thread {

	private final User user;
	private final AsyncEventDispatcher dispatcher;
	public UserChatThread(User user, AsyncEventDispatcher dispatcher) {
		super(user.getName());
		this.user = user;
		this.dispatcher = dispatcher;
	}
	
	@Override
	public void run() {
		try {
			//User上线，发送Online Event
			dispatcher.dispatch(new UserOnlineEvent(user));
			for(int i = 0; i < 5; i++) {
				//发送User的聊天信息
				dispatcher.dispatch(new UserChatEvent(user, getName() + "-Hello-" + i));
				//短暂休眠1~10秒
				TimeUnit.SECONDS.sleep(ThreadLocalRandom.current().nextInt(10));
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}finally {
			//User下线，发送Offline Event
			dispatcher.dispatch(new UserOfflineEvent(user));
		}
	}
}
