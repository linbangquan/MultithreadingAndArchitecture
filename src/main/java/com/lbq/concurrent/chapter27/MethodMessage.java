package com.lbq.concurrent.chapter27;

import java.util.Map;
/**
 * 27.2.4 MethodMessage
 * MethodMessage的主要作用是收集每一个接口的方法参数，并且提供execute方法供ActiveDaemonThread直接调用，
 * 该对象就是典型的WorkerThread模型中的Product(附有使用说明书的半成品，等待流水线工人的加工)，
 * execute方法则是加工该产品的说明书。
 * 其中，params主要用来收集方法参数，orderService是具体的接口实现，每一个方法都会拆分成不同的Message。
 * 在OrderService中，我们定义了两个方法，因此需要实现两个MethodMessage。
 * @author 14378
 *
 */
public abstract class MethodMessage {
	//用于收集方法参数，如果有返回Future类型则一并收集
	protected final Map<String, Object> params;
	
	protected final OrderService orderService;
	
	public MethodMessage(Map<String, Object> params, OrderService orderService) {
		this.params = params;
		this.orderService = orderService;
	}
	//抽象方法，扮演work thread的说明书
	public abstract void execute();
}
