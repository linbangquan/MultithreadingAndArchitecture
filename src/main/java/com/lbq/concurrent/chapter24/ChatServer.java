package com.lbq.concurrent.chapter24;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import com.lbq.concurrent.chapter08.BasicThreadPool;
import com.lbq.concurrent.chapter08.ThreadPool;
/**
 * 24.3 多用户的网络聊天
 * Thread-Per-Message模式在网络通信中的使用也是非常广泛的，比如在本节中介绍的网络聊天程序，
 * 在服务器每一个连接的服务端的连接都将创建一个独立的线程进行处理，当客户端的连接数超过了服务端的
 * 最大受理能力时，客户端将被存放至排队队列中。
 * 24.3.1 服务端程序
 * 下面编写服务端程序ChatServer用于接收来自客户端的链接，并且与之进行TCP通信交互，
 * 当服务器端接收到了每一次的客户端连接后便会给线程池提交一个任务用于与客户端进行交互，
 * 进而提高并发响应能力。
 * 
 * 在下面的程序中，当接收到了新的客户端连接时，会为每一个客户端连接创建一个线程ClientHandler与客户端进行交互，
 * 当客户端的连接个数超过线程池的最大数量时，客户端虽然可以成功接入服务端，但是会进入阻塞队列。
 * @author 14378
 *
 */
public class ChatServer {

	//服务端端口
	private final int port;
	//定义线程池，该线程池是我们在第8章中定义的
	private ThreadPool threadPool;
	//服务端Socket
	private ServerSocket serverSocket;
	//通过构造函数传入端口
	public ChatServer(int port) {
		this.port = port;
	}
	//默认使用13312端口
	public ChatServer() {
		this(13312);
	}
	
	public void startServer() throws IOException {
		//创建线程池，初始化一个线程，核心线程数量为2，最大线程数量为4，阻塞队列中最大可加入1000任务
		this.threadPool = new BasicThreadPool(1, 4, 2, 1000);
		this.serverSocket = new ServerSocket(port);
		this.serverSocket.setReuseAddress(true);
		System.out.println("Chat server is started and listen at port：" + port);
		this.listen();
	}

	private void listen() throws IOException {
		for(;;) {
			//accept方法是阻塞方法，当有新的链接进入时才会返回，并且返回的是客户端的连接
			Socket client = serverSocket.accept();
			//将客户端连接作为一个Request封装成对应的Handler然后提交给线程池
			this.threadPool.execute(new ClientHandler(client));
		}
		
	}
}
