package com.lbq.concurrent.chapter27;

import java.util.concurrent.TimeUnit;

import com.lbq.concurrent.chapter19.Future;
import com.lbq.concurrent.chapter19.FutureService;
/**
 * OrderServiceImpl类是OrderService的一个具体实现，该类是在执行线程中将要被使用的类，
 * 其中findOrderDetails方法通过第19章中我们开发的Future立即返回一个结果，
 * Order方法则通过休眠来模拟该方法的执行比较耗时。
 * @author 14378
 *
 */
public class OrderServiceImpl implements OrderService {

	@Override
	public Future<String> findOrderDetails(long orderId) {
		//使用在第19章中实现的Future返回结果
		FutureService<Long, String> futureService = FutureService.<Long, String>newService();
		return futureService.submit(input -> {
			try {
				//通过休眠来模拟该方法的执行比较耗时
				TimeUnit.SECONDS.sleep(10);
				System.out.println("process the orderID -> " + orderId);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			return "The order Details Information";
		}, orderId, null);
	}

	@Override
	public void order(String account, long orderId) {
		try {
			TimeUnit.SECONDS.sleep(10);
			System.out.println("process the order for account " + account + ", orderId " + orderId);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
	}

}
