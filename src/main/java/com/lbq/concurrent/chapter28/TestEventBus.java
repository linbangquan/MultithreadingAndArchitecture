package com.lbq.concurrent.chapter28;
/**
 * Event Bus 设计模式
 * 相信每一位读者都有使用过消息中间件的经历，比如Apache ActiveMQ和Apache Kafka等，
 * 某subscriber在消息中间件上注册了某个topic(主题)，当有消息发送到了该topic上之后，
 * 注册在该topic上的所有subscriber都将会收到消息。
 * 消息中间件提供了系统之间的异步处理机制，比如在某电商网站上支付订单之后，会触发库存计算、物流调度计算，
 * 甚至是营销人员绩效计算、报表统计等，诸如此类的操作一般会耗费比订单购买商品本身更多的时间，
 * 加之这样的操作没有即时的时效性要求，用户在下单之后完全没有必要等待电商后端做完所有的操作才算成功，
 * 那么此时消息中间件便是一种非常好的解决方案，用户下单成功支付之后即可向用户返回购买成功的通知，
 * 然后提交各种消息至消息中间件，这样注册在消息中间件的其他系统就可以顺利地接收订单通知了，
 * 然后执行各自的业务逻辑。消息中间件主要用于解决进程之间的消息异步处理的解决方案，
 * 在本章中，我们使用消息中间件的思想设计一个Java进程内部的消息中间件————Event Bus。
 * 
 * 28.1 Event Bus设计
 * Event Bus的设计稍微复杂一些，所涉及的类比较多（10个左右）。
 * 1.Bus接口对外提供了几种主要的使用方式，比如post方法用来发送Event，
 *   register方法用来注册Event接收者(Subscriber)接受响应事件，
 *   EventBus采用同步的方式推送Event，
 *   AsyncEventBus采用异步的方式(Thread-Per-Message)推送Event。
 * 2.Registry注册表，主要用来记录对应的Subscriber以及受理消息的回调方法，回调方法我们用注解@Subscribe来标识。
 * 3.Dispatcher主要用来将event广播给注册表中监听了topic的Subscriber。
 * 
 * 28.1.7 Event Bus测试
 * 关于EventBus的设计已经完成，虽然代码比较多，但是原理其实并不复杂，在本节中，我们将分别对同步的Event Bus和异步的Event Bus进行简单的测试。
 * 
 * (2) 同步Event Bus
 * 下面的这段程序定义了同步的EventBus，然后将两个普通的对象注册给了bus，当bus发送Event的时候topic相同，
 * Event类型相同的subscribe方法将被执行。
 * @author 14378
 *
 */
public class TestEventBus {

	public static void main(String[] args) {
		Bus bus = new EventBus("TestBus");
		bus.register(new SimpleSubscriber1());
		bus.register(new SimpleSubscriber2());
		bus.post("Hello");
		System.out.println("---------------------------");
		bus.post("Hello1", "test");
	}

}
