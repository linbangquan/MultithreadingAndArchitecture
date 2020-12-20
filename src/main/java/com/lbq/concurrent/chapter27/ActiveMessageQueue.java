package com.lbq.concurrent.chapter27;

import java.util.LinkedList;
/**
 * 27.2.5 ActiveMessageQueue
 * ActiveMessageQueue对应于Worker-Thread模式中的传送带，主要用于传送调用线程通过Proxy提交过来的MethodMessage，
 * 但是这个传送带允许存放无限的MethodMessage(没有limit的约束，理论上可以放无限多个MethodMessage直到发生堆内存溢出的异常)。
 * 下述代码中：
 * 1.在创建ActiveMessageQueue的同时启动ActiveDaemonThread线程，ActiveDaemonThread主要用来进行异步的方法执行，后面我们会介绍。
 * 2.执行offer方法没有进行limit的判断，允许提交无限个MethodMessage(直到发生堆内存溢出)，并且当有新的Message加入时会通知ActiveDaemonThread线程。
 * 3.take方法主要是被ActiveDaemonThread线程使用，当message队列为空时ActiveDaemonThread线程将会被挂起(Guarded Suspension)。
 * @author 14378
 *
 */
public class ActiveMessageQueue {
	//用于存放提交的MethodMessage消息
	private final LinkedList<MethodMessage> messages = new LinkedList<>();
	
	public ActiveMessageQueue() {
		//启动Worker线程
		new ActiveDaemonThread(this).start();
	}
	
	public void offer(MethodMessage methodMessage) {
		synchronized(this) {
			messages.addLast(methodMessage);
			//因为只有一个线程负责take数据，因此没有必要使用notifyAll方法
			this.notify();
		}
	}
	
	protected MethodMessage take() {
		synchronized(this) {
			//当MethodMessage队列中没有Message的时候，执行线程进入阻塞
			while(messages.isEmpty()) {
				try {
					this.wait();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			//获取其中一个MethodMessage并且从队列中移除
			return messages.removeFirst();
		}
	}
}
