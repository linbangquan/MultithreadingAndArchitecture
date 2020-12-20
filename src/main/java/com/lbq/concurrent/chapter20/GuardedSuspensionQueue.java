package com.lbq.concurrent.chapter20;

import java.util.LinkedList;
/**
 * Guarded Suspension 设计模式
 * 
 * 20.1 什么是Guarded Suspension设计模式
 * Suspension是“挂起”、“暂停”的意思，而Guarded则是“担保”的意思，连在一起就是确保挂起。
 * 当线程在访问某个对象时，发现条件不满足，就暂时挂起等待条件满足时再次访问，
 * 这一点和Balking设计模式刚好相反（Balking在遇到条件不满足时会放弃）。
 * Guarded Suspension设计模式是很多设计模式的基础，比如生产者消费者模式，Worker Thread设计模式，等等，
 * 同样在Java并发包中的BlockingQueue中也大量使用到了Guarded Suspension设计模式。
 * 
 * 20.2 Guarded Suspension的示例，代码如下。
 * 在GuradedSuspensionQueue中，我们需要保证线程安全的是queue，分别在take和offer方法中对应的临界值是queue为空和queue的数量>=100,
 * 当queue中的数据已经满时，如果有线程调用offer方法则会被挂起(Suspension)，同样，当queue没有数据的时候，调用take方法也会被挂起。
 * Guarded Suspension模式是一个非常基础的设计模式，它主要关注的是当某个条件（临界值）不满足时操作的线程正确挂起，以防止出现数据不一致或者操作超过临界值的控制范围。
 * 
 * 20.3 本章总结
 * Guarded Suspension设计模式并不复杂，但是它是很多其他线程设计模式的基础，比如生产者消费者模式，后文中的Thread Worker设计模式、Balking设计模式等，
 * 都可以看到Guarded Suspension模式的影子，Guarded Suspension的关注点在于临界值的条件是否满足，当到达设置的临界值时相关线程则会被挂起。
 * @author 14378
 *
 */
public class GuardedSuspensionQueue {
	//定义存放Integer类型的queue
	private final LinkedList<Integer> queue = new LinkedList<>();
	//定义queue的最大容量为100
	private final int LIMIT = 100;
	//往queue中插入数据，如果queue中的元素超过了最大容量，则会陷入阻塞
	public void offer(Integer data) throws InterruptedException {
		synchronized(this) {
			while(queue.size() >= LIMIT) {
				this.wait();
			}
			queue.addLast(data);
			this.notifyAll();
		}
	}
	//从队列中获取元素，如果队列此时为空，则会使当前线程阻塞
	public Integer take() throws InterruptedException {
		synchronized(this) {
			//判断如果队列为空
			while(queue.isEmpty()) {
				//则挂起当前线程
				this.wait();
			}
			//通知offer线程可以继续插入数据了
			this.notifyAll();
			return queue.removeFirst();
		}
	}
}
