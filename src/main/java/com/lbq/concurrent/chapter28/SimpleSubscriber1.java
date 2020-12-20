package com.lbq.concurrent.chapter28;
/**
 * (1)简单的Subscriber
 * 我们简单地定义两个普通对象SimpleSubscriber1和SimpleSubscriber2，由于两者比较类似，因此省略了SimpleSubscriber。
 * @author 14378
 *
 */
public class SimpleSubscriber1 {

	@Subscribe
	public void method1(String message) {
		System.out.println("==SimpleSubscriber1==method1==" + message);
	}
	@Subscribe(topic = "test")
	public void method2(String message) {
		System.out.println("==SimpleSubscriber1==method2==" + message);
	}
}
