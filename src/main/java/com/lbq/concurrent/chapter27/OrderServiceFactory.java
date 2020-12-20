package com.lbq.concurrent.chapter27;
/**
 * 27.2.6 OrderServiceFactory及测试
 * 我们基本已经完成了一个标准Active Objects的设计，接口方法的每一次调用实际上都是向Queue中提交一个对应的Message信息，
 * 当然这个工作主要由Proxy完成的，但是为了让Proxy的构造透明化，我们需要设计一个Factory工具类。
 * toActiveObject方法主要用于创建OrderServiceProxy。
 * @author 14378
 *
 */
public final class OrderServiceFactory {
	//将ActiveMessageQueue定义成static的目的是，保持其在整个JVM进程中是唯一的，并且ActiveDaemonThread会在此刻启动
	private final static ActiveMessageQueue activeMessageQueue = new ActiveMessageQueue();
	//不允许外部通过new的方式构建
	private OrderServiceFactory() {
		
	}
	//返回OrderServiceFactory
	public static OrderService toActiveObject(OrderService orderService) {
		return new OrderServiceProxy(orderService, activeMessageQueue);
	}

}
