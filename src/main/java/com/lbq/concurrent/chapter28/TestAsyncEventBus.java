package com.lbq.concurrent.chapter28;

import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
/**
 * (3)异步Event Bus
 * 同步的Event Bus有个缺点，若其中的一个subscribe方法运行时间比较长，则会影响下一个subscribe方法的执行，因此采用AsyncEventBus是另外一个比较好的选择。
 * @author 14378
 *
 */
public class TestAsyncEventBus {

	public static void main(String[] args) {
		Bus bus = new AsyncEventBus("TestBus", (ThreadPoolExecutor) Executors.newFixedThreadPool(10));
		bus.register(new SimpleSubscriber1());
		bus.register(new SimpleSubscriber2());
		bus.post("Hello");
		System.out.println("---------------------------");
		bus.post("Hello", "test");
	}

}
