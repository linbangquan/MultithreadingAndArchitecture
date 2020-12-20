package com.lbq.concurrent.chapter27;

import java.util.Map;
/**
 * OrderMessage主要处理order方法，从param中获取接口参数，然后执行真正的OrderService的方法。
 * @author 14378
 *
 */
public class OrderMessage extends MethodMessage {

	public OrderMessage(Map<String, Object> params, OrderService orderService) {
		super(params, orderService);
	}

	@Override
	public void execute() {
		//获取参数
		String account = (String) params.get("account");
		long orderId = (long) params.get("orderId");
		//执行真正的order方法
		orderService.order(account, orderId);
	}

}
