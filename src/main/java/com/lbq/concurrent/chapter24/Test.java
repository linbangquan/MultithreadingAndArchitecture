package com.lbq.concurrent.chapter24;

import java.io.IOException;
/**
 * Thread-Per-Message模式
 * 24.1 什么是Thread-Per-Message模式
 * Thread-Per-Message的意思是为每一个消息的处理开辟一个线程使得消息能够以并发的方式进行处理，从而提供系统整体的吞吐能力。
 * 这就好比电话接线员一样，收到的每一个电话投诉或者业务处理请求，都会提交对应的工单，然后交由对应的工作人员来处理。
 * 
 * 24.3.3 聊天程序测试
 * 启动服务端程序，为了简单起见，我们直接使用telnet命令进行客户端测试即可。
 * 但是当创建第五个客户端连接的时候，将会被加入到线程池的任务队列中而无法与服务器进行交互，
 * 其原因是因为当前线程池中再没有任何线程可以负责处理与该客户端的交互了。
 * 
 * 24.4 本章总结
 * Thread-Per-Message设计模式在日常的开发中非常常见，但是也要灵活使用，比如为了避免频繁创建线程带来的系统开销，可以用线程池来代替，
 * 在本书的第28章 “Event Bus设计” 中也有对Thread-Per-Message设计模式的使用。
 * @author 14378
 *
 */
public class Test {

	public static void main(String[] args) throws IOException {
		new ChatServer().startServer();
	}

}
