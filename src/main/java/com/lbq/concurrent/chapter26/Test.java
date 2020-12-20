package com.lbq.concurrent.chapter26;

import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;
/**
 * Worker-Thread设计模式
 * 26.1 什么是Worker-Thread模式
 * Worker-Thread模式有时也称为流水线设计模式，这种设计模式类似于工厂流水线，上游工作人员完成了某个电子产品的组装之后，将半成品放到流水线传送带上，接下来的加工工作则会交给下游的工人。
 * 线程池在某种意义上也算是Worker-Thread模式的一种实现，线程池初始化时所创建的线程类似于在流水线等待的工作的工人，提交给线程池的Runnable接口类似于需要加工的产品，
 * 而Runnable的run方法则相当于组装该产品的说明书。
 * 
 * 26.2 worker-Thread模式实现
 * 根据我们前面的描述可以看出Worker-Thread模式需要有如下几个角色。
 * 1.流水线工人：流水线工人主要用来对传送带上的产品进行加工。
 * 2.流水线传送带：用于传送来自上游的产品。
 * 3.产品组装说明书：用来说明该产品如何组装。
 * 
 * 26.3 本章总结
 * 26.3.1 产品流水线测试
 * 26.3.2 Worker-Thread和Producer-Consumer
 * 笔者在进行互联网授课的过程中，很多人觉得无法区分Worker-Thread和Producer-Consumer模式，其实这也可以理解，
 * 毕竟多线程的架构设计模式是很多程序员在日常的开发过程中累积沉淀下来的优秀编程模式，并不像GoF23中设计模式那样具有官方的认可，
 * 但是两者之间的区别还是很明显的。
 * (1)Producer-Consumer模式
 * 首先Producer、Consumer对Queue都是依赖关系，其次Producer要做的就是不断地往Queue中生产数据，
 * 而是Consumer则是不断地从Queue中获取数据，Queue即不知道Producer的存在也不知道Consumer的存在，
 * 最后Consumer对Queue中数据的消费并不依赖于数据本身的方法（使用说明书）。
 * (2)Worker-Thread模式
 * 左侧的线程，也就是传送带上游的线程，同样在不断地往传送带(Queue)中生产数据，而当Channel被启动的时候，就会同时创建并启动若干数量的Worker线程，
 * 因此我们可以看出Worker于Channel来说并不是单纯的依赖关系，而是聚合关系，Channel必须知道Worker的存在。
 * @author 14378
 *
 */
public class Test {

	public static void main(String[] args) {
		//流水线上有5个工人
		final ProductionChannel channel = new ProductionChannel(5);
		AtomicInteger productionNo = new AtomicInteger();
		//流水线上有8个工作人员往传送带上不断地放置等待加工的半成品
		IntStream.range(1, 8).forEach(i -> new Thread(() -> {
			while(true) {
				channel.offerProduction(new Production(productionNo.getAndIncrement()));
				try {
					TimeUnit.SECONDS.sleep(ThreadLocalRandom.current().nextInt());
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}).start());
	}

}
