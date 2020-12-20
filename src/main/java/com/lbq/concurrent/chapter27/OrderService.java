package com.lbq.concurrent.chapter27;

import com.lbq.concurrent.chapter19.Future;
/**
 * 27.2.1 OrderService接口设计
 * OrderService是一个比较简单的接口，包含两个方法，其中第一个为有返回值的方法，第二个方法则没有返回值。
 * 
 * 1.findOrderDetails(long orderId):通过订单编号获取订单详情，有返回值的方法必须是Future类型的，
 * 	   因为方法的执行是在其他线程中进行的，势必不会立即得到正确的最终结果，通过Future可以立即得到返回。
 * 2.Order(String account, long orderId):提交用户的订单信息，是一种无返回值的方法。
 * @author 14378
 *
 */
public interface OrderService {
	/**
	 * 根据订单编号查询订单明细，有入参也有返回值，但是返回类型必须是Future
	 * @param orderId
	 * @return
	 */
	Future<String> findOrderDetails(long orderId);
	/**
	 * 提交订单，没有返回值
	 * @param account
	 * @param orderId
	 */
	void order(String account, long orderId);
}
