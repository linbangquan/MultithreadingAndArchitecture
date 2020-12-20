package com.lbq.concurrent.chapter27.activeobject;
/**
 * 与ActiveMessageQueue紧密关联的ActiveDaemonThread也得进行简单修改
 * @author 14378
 *
 */
public class ActiveDaemonThread extends Thread {

	private final ActiveMessageQueue queue;
	
	public ActiveDaemonThread(ActiveMessageQueue queue) {
		super("ActiveDaemonThread");
		this.queue = queue;
		setDaemon(true);
	}
	
	@Override
	public void run() {
		for(;;) {
			ActiveMessage activeMessage = this.queue.take();
			activeMessage.execute();
		}
	}
}
