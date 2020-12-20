package com.lbq.concurrent.chapter27.activeobject;

import java.util.LinkedList;
/**
 * 27.3.4 ActiveMessageQueue及其他
 * 在27.2.5节中，插入到ActiveMessageQueue中的数据为MethodMessage，由于我们定义了更加通用的ActiveMessage，因此需要修改Queue中的数据类型。
 * @author 14378
 *
 */
public class ActiveMessageQueue {
	//与27.2节中的标准Active Objects不一样的是，通用的ActiveMessageQueue只需要提交ActiveMessage
	private final LinkedList<ActiveMessage> messages = new LinkedList<>();
	
	public ActiveMessageQueue() {
		//同样启动ActiveDaemonThread
		new ActiveDaemonThread(this).start();
	}
	
	public void offer(ActiveMessage activeMessage) {
		synchronized(this) {
			messages.addLast(activeMessage);
			this.notify();
		}
	}
	
	public ActiveMessage take() {
		synchronized(this) {
			while(messages.isEmpty()) {
				try {
					this.wait();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			return messages.removeFirst();
		}
	}
}
