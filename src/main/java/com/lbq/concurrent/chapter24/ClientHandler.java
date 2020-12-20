package com.lbq.concurrent.chapter24;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.Socket;
/**
 * 24.3.2 响应客户端连接的Handler
 * 待服务器端接收到客户端的连接之后，便会创建一个新的ChatHandler任务提交给线程池，
 * ChatHandler任务是Runnable接口的实现，主要负责和客户端进行你来我往的简单通信交互。
 * 
 * 下面的通信方式是一种典型的一问一答的聊天方式，客户端连接后发送消息给服务端，服务端回复消息给客户端，每一个线程负责处理一个来自客户端的连接。
 * @author 14378
 *
 */
public class ClientHandler implements Runnable {
	//客户端的socket连接
	private final Socket socket;
	//客户端的identity
	private final String clientIdentify;
	//通过构造函数传入客户端的连接
	public ClientHandler(final Socket socket) {
		this.socket = socket;
		this.clientIdentify = socket.getInetAddress().getHostAddress() + ":" + socket.getPort();
	}
	@Override
	public void run() {
		try {
			this.chat();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
	private void chat() throws IOException {
		BufferedReader bufferedReader = wrap2Reader(this.socket.getInputStream());
		PrintStream printStream = wrap2Print(this.socket.getOutputStream());
		String received;
		while((received = bufferedReader.readLine()) != null) {
			//将客户端发送的消息输出到控制台
			System.out.printf("client:%s-message:%s\n", clientIdentify, received);
			if(received.equals("quit")) {
				//如果客户端发送了quit指令，则断开与客户端的连接
				write2Client(printStream, "client will close");
				socket.close();
				break;
			}
		}
		//向客户端发送消息
		write2Client(printStream, "Server:" + received);
	}
	/**
	 * 将输入字节流封装成BufferedReader缓冲字符流
	 * @param printStream
	 * @param message
	 */
	private void write2Client(PrintStream printStream, String message) {
		printStream.println(message);
		printStream.flush();
	}
	/**
	 * 将输出字节流封装成PrintStream
	 * @param outputStream
	 * @return
	 */
	private PrintStream wrap2Print(OutputStream outputStream) {
		return new PrintStream(outputStream);
	}
	/**
	 * 将输入字节流封装成BufferedReader缓冲字符流
	 * @param inputStream
	 * @return
	 */
	private BufferedReader wrap2Reader(InputStream inputStream) {
		return new BufferedReader(new InputStreamReader(inputStream));
	}

}
