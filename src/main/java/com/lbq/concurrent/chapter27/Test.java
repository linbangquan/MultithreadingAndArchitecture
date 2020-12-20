package com.lbq.concurrent.chapter27;
/**
 * Active Objects设计模式
 * 27.1 接受异步消息的主动对象
 * Active是“主动”的意思，Active Object是“主动对象”的意思，所谓主动对象就是指其拥有自己的独立线程，
 * 比如java.lang.Thread实例就是主动对象，不过Active Object Pattern不仅仅是拥有独立的线程，
 * 它还可以接受异步消息，并且能够返回处理的结果。
 * 我们在本书中频繁使用的System.gc()方法就是一个“接受异步消息的主动对象”，调用gc方法的线程和gc自身的执行线程并不是同一个线程，
 * 在本章中，我们将实现一个类似于System.gc的可接受异步消息的主动对象。
 * 
 * 27.2 标准Active Objects模式设计
 * 在本书中，我们首先从标准的Active Objects设计入手，将一个接口的方法调用转换成可接受异步消息的主动对象，
 * 也就是说方法的执行和方法的调用是在不同的线程中进行的，那么如何使得执行线程知道应该如何正确执行接口方法呢？
 * 我们需要将接口方法的参数以及具体实现封装成特定的Message告知执行线程。如果该接口方法需要返回值，则必须得设计成Future的返回形式。
 * 
 * 当某个线程调用OrderService接口的findOrderDetails方法时，事实上是发送了一个包含findOrderDetails方法参数以及
 * OrderService具体实现的Message至Message队列，执行线程通过从队列中获取Message来调用具体的实现，接口方法的调用和接口方法
 * 的执行分别处于不同的线程中，因此我们称该接口为Active Objects(可接受异步消息的主动对象)。
 * 
 * 运行下面的测试代码会立即得到返回，10秒之后，order方法执行结束，调用order方法的线程是主线程，
 * 但是执行该方法的线程却是其他线程，这也正是ActiveObjects可接受异步消息的意思。
 * @author 14378
 *
 */
public class Test {
	
	public static void main(String[] args) throws InterruptedException {
		//在创建OrderService时需要传递OrderSerive接口的具体实现
		OrderService orderService = OrderServiceFactory.toActiveObject(new OrderServiceImpl());
		orderService.order("hello", 453453);
		//立即返回
		System.out.println("Return immediately");
		Thread.currentThread().join();
	}
}
