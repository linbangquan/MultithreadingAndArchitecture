package com.lbq.concurrent.chapter29.eventdriven;
/**
 * 29.2 开发一个Event-Driven框架
 * 在29.1节中，我们通过非常简陋的例子大致介绍了EDA程序设计中几个关键的组件，在本节中，我们将分别它们进行抽象，设计一个迷你的EDA小框架，
 * 在29.3节中，该框架将会被用来实现一个简单的聊天小程序。
 * 
 * 通过29.1节的基础知识介绍，我们大致可以知道，一个基于事件驱动的架构设计，总体来讲会涉及如下几个重要组件：
 * 事件消息(Event)、针对该事件的具体处理器(handler)、接受事件消息的通道(29.1.3节中的queue)，以及对事件消息如何进行分配(Event Loop)。
 * 
 * 29.2.1 同步EDA框架设计
 * 我们先设计开发一个高度抽象的同步EDA框架，然后在29.2.2节中增加异步功能。
 * (1) Message
 * 回顾29.1节基础部分的介绍，在基于Message的系统中，每一个Event也可以被称为Message，Message是对Event更高一个层级的抽象，
 * 每一个Message都有一个特定的Type用于与对应的Handler做关联。
 * @author 14378
 *
 */
public interface Message {
	/**
	 * 返回Message类型
	 * @return
	 */
	Class<? extends Message> getType();
}
