package com.lbq.concurrent.chapter27.activeobject;

import com.lbq.concurrent.chapter19.Future;
import com.lbq.concurrent.chapter27.OrderService;
import com.lbq.concurrent.chapter27.OrderServiceFactory;
import com.lbq.concurrent.chapter27.OrderServiceImpl;
/**
 * 27.3 通用Active Objects框架设计
 * 标准的Active Objects要将每一个方法都封装成Message(比如27.2节中定义的FindOrderDetailsMessage，OrderMessage)，
 * 然后提交至Message队列中，这样的做法有点类似于远程方法调用(RPC:Remote Process Call)。
 * 如果某个接口的方法很多，那么需要封装很多的Message类；同样如果很多接口需要成为Active Object，则需要封装成非常多的Message类，
 * 这样显然不是很友好。在本节中，我们将设计一个更加通用的ActiveObject框架，可以将任意的接口转换成Active Object。
 * 
 * 在本节中，我们将使用JDK动态代理的方式实现一个更为通用的Active Objects，可以将任意接口方法转换为Active Objects，
 * 当然如果接口方法有返回值，则必须要求返回Future类型才可以，否则将会抛出IllegalActiveMethod异常。
 * 
 * 关于这个版本的Active Objects，其用法与之前的版本差别不大，但是它能够将满足规范的任意接口方法都转换成Active Objects。
 * 运行下面的测试代码，future将会立即返回，但是get方法会进入阻塞，10秒钟以后订单的详细信息将会返回，同样，OrderService接口
 * 的调用线程和具体的执行线程不是同一个，OrderServiceImpl通过active方法具备了可接受异步消息的能力。
 * 
 * 27.4 本章总结
 * 在本章中，我们通过System.gc()方法的原理分析，分别设计了两种不同的Active Objects模式实现，第二种方式更加通用一些，
 * 因为它摒弃了第一种方式需要手动定义方法的Message以及Proxy等缺陷，通过动态代理的方式动态生成代理类，当然读者可以通过开源的
 * 第三方Proxy来实现动态代理的功能，比如cglib以及asm等。
 * Active Objects模式既能够完整地保留接口方法的调用形式，又能让方法的执行异步化，这也是其他接口异步调用模式(Future模式：
 * 只提供了任务的异步执行方案，但是无法保留接口原有的调用形式)无法同时做到的。
 * Active Objects模式中使用了很多其他设计模式，代理类的生成(代理设计模式)、ActiveMessageQueue(Guarded Suspension Pattern
 * 以及Worker Thread Pattern)、findOrderDetails方法(Future设计模式)，希望读者能够熟练掌握在Active Objects
 * 设计模式中用到的其他设计模式。
 * @author 14378
 *
 */
public class Test {

	public static void main(String[] args) throws InterruptedException {
		OrderService orderService = OrderServiceFactory.toActiveObject(new OrderServiceImpl());
		
		Future<String> future = orderService.findOrderDetails(23423);
		System.out.println("i will be returned immediately");
		System.out.println(future.get());
	}

}
