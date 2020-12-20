package com.lbq.concurrent.chapter28;
/**
 * 非常普通的对象
 * SimpleObject的实例被注册到了Event Bus之后，test2和test3这两个方法将会被加入到注册表中，分别用来接受来自alex-topic和test-topic的event。
 * @author 14378
 *
 */
public class SimpleObject {

	/**
	 * subscribe方法，比如使用@Subscribe标记，并且是void类型且有一个参数
	 * @param x
	 */
	@Subscribe(topic = "alex-topic")
	public void test2(Integer x) {
		
	}
	@Subscribe(topic = "test-topic")
	public void test3(Integer x) {
		
	}
}
