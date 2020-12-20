package com.lbq.concurrent.juc.utils.section11;

import com.lbq.concurrent.juc.utils.section11.RateLimiterTokenBucket.NoProductionException;
import com.lbq.concurrent.juc.utils.section11.RateLimiterTokenBucket.OrderFailedException;
/**
 * 一个简单的令牌环桶已经实现，理论上应该将订单请求封装成订单对象匀速地插入一个桶容器中，然后由另外的线程将其写入某中间件中，之后订单服务监听中间件的topic或者event再进行订购关系处理。
 * 下面就来简单模拟一下多用户对该商品进行抢购的操作。
 * 
 * 运行上面的程序，我们会看到商品抢购成功、抢购失败、商品售完等情况。
 * 
 * 3.11.4 RateLimiter总结
 * RateLimiter是一个非常好用的工具，用来进行限流控制是一个不错的选择，笔者在日常的开发中就是使用RateLimiter进行数据的分发速率控制，当然了，将速率的设置做成可配置是一个比较好的方式。
 * @author 14378
 *
 */
public class RateLimiterExample4 {
	private static final RateLimiterTokenBucket tokenBucket= new RateLimiterTokenBucket();
	public static void main(String[] args) {
		for(int i = 0; i < 20; i++) {
			new Thread(() -> {
				while(true) {
					try {
						//抢购商品
						tokenBucket.bookOrder(prodID -> System.out.println("User: " + Thread.currentThread() + " book the prod order and prodID: " + prodID));
					} catch (NoProductionException e) {
						//当前商品已经售完，退出抢购
						System.out.println("all of production already sold out.");
						break;
					} catch (OrderFailedException e) {
						//抢购失败，然后尝试重新抢购
						System.out.println("User: " + Thread.currentThread() + "book order failed, will try again.");
					}
				}
			}).start();
		}	
	}
}
