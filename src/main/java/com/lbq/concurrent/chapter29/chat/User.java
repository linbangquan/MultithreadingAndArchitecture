package com.lbq.concurrent.chapter29.chat;
/**
 * 29.3 Event-Driven的使用
 * 在本节中，我们模拟一个简单的聊天应用程序，借助于我们在29.2节开发的EDA小框架，首先我们要为聊天应用程序定义如下几个类型的Event。
 * 1.User Online Event：当用户上线时来到聊天室的Event。
 * 2.User Offline Event：当用户下线时退出聊天室的Event。
 * 3.User Chat Event：用户在聊天室中发送聊天信息的Event。
 * 
 * 29.3.1 Chat Event
 * 首先，我们定义一个User对象，代表聊天室的参与者，比较简单就是一个名字，代码如下：
 * @author 14378
 *
 */
public class User {

	private final String name;

	public User(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}
}
