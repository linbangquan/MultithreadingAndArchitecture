package com.lbq.concurrent.chapter27;

import java.util.Map;

import com.lbq.concurrent.chapter19.Future;

public class FindOrderDetailsMessage extends MethodMessage {

	public FindOrderDetailsMessage(Map<String, Object> params, OrderService orderService) {
		super(params, orderService);
	}

	@Override
	public void execute() {
		//①执行orderService的findOrderDetails方法
		Future<String> realFuture = orderService.findOrderDetails((Long) params.get("orderId"));
		ActiveFuture<String> activeFuture = (ActiveFuture<String>) params.get("activeFuture");
		
		try {
			//②调用orderServiceImpl返回的Future.get()，此方法会导致阻塞直到findOrderDetails方法完全执行结束。
			String result = realFuture.get();
			//③当findOrderDetails执行结束时，将结果通过finish的方法传递给activeFuture。
			activeFuture.finish(result);
		} catch (InterruptedException e) {
			activeFuture.finish(null);
		}
	}
}
