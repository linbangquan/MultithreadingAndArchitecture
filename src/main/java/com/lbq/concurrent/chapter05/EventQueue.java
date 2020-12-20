package com.lbq.concurrent.chapter05;

import java.util.LinkedList;
/**
 * 如果队列中有Event，则通知工作的线程开始工作；没有Event，则工作线程休息并等待通知。
 * 首先实现一个EventQueue，该Queue有如下三种状态：
 * 1.队列满————最多可容纳多少个Event，好比一个系统最多同时能够受理多少业务一样；
 * 2.队列空————当所有的Event都被处理并且没有新的Event被提交的时候，此时队列将是空的状态；
 * 3.有Event但是没有满————有新的Event被提交，但是此时没有达到队列的上限。
 * 
 * wait和notify方法详解
 * wait方法的三个重载方法：
 * public final void wait() throws InterruptedException;
 * public final void wait(long timeout) throwsInterruptedException;
 * public final void wait(long timeout, int nanos) throws InterruptedException;
 * 
 * 1.wait方法的这三个重载方法都将调用wait(long timeout)这个方法，前文使用的wait()方法等价于wait(0)，0代表永不超时。
 * 2.Object的wait(long timeout)方法会导致当前线程进入阻塞，直到有其他线程调用了Object的notify或者notifyAll方法才能将其唤醒，或者阻塞时间到达了timeout时间而自动唤醒。
 * 3.wait方法必须拥有该对象的monitor，也就是wait方法必须在同步方法中使用。
 * 4.当前线程执行了该对象的wait方法之后，将会放弃对该monitor的所有权并且进入与该对象关联的wait set中，
 * 也就是说一旦线程执行了某个object的wait方法之后，它就会释放对该对象monitor的所有权，其他线程也会有机会继续争抢该monitor的所有权。
 * 
 * notify方法的作用
 * public final native void notify();
 * 1.唤醒单个正在执行该对象wait方法的线程。
 * 2.如果有某个线程由于执行该对象的wait方法而进入阻塞则会被唤醒，如果没有则会忽略。
 * 3.被唤醒的线程需要重新获取对该对象所关联monitor的lock才能继续执行。
 * 
 * 关于wait和notify的注意事项
 * 1.wait方法是可中断方法，这也就意味着，当前线程一旦调用了wait方法进入阻塞状态，其他线程是可以使用interrupt方法将其打断的；
 * 根据3.7节的介绍，可中断方法被打断后会收到中断异常InterruptedException，同时interrupt标识也会被擦除。
 * 2.线程执行了某个对象的wait方法以后，会加入与之对应的wait set中，每一个对象的monitor都有一个与之关联的wait set。
 * 3.当线程进入wait set之后，notify方法可以将其唤醒，也就是从wait set中弹出，同时中断wait中的线程也会将其唤醒。
 * 4.必须在同步方法中使用wait和notify方法，因为执行wait和notify的前提条件是必须持有同步方法的monitor的所有权。
 * 5.同步代码的monitor必须与执行wait、notify方法的对象一致，简单地说就是用哪个对象的monitor进行同步，就只能用哪个对象进行wait和notify操作。
 * 
 * wait和sleep
 * 从表面上看，wait和sleep方法都可以使当前线程进入阻塞状态，但是两者之间存在着本质的区别，下面我们将总结两者的区别和相似之处。
 * 1.wait和sleep方法都可以使线程进入阻塞状态。
 * 2.wait和sleep方法均是可中断方法，被中断后都会收到中断异常。
 * 3.wait是Object的方法，而sleep是Thread特有的方法。
 * 4.wait方法的执行必须在同步方法中进行，而sleep则不需要。
 * 5.线程在同步方法中执行sleep方法时，并不会释放monitor的锁，而wait方法则会释放monitor的锁。
 * 6.sleep方法短暂休眠之后会主动退出阻塞，而wait方法(如果没有指定wait时间)则需要被其他线程中断后才能退出阻塞。
 * 
 * @author 14378
 *
 */
public class EventQueue {

	private final int max;
	
	static class Event{}
	
	private final LinkedList<Event> eventQueue = new LinkedList<>();
	
	private final static int DEFAULT_MAX_EVENT = 10;
	public EventQueue() {
		this(DEFAULT_MAX_EVENT);
	}
	
	public EventQueue(int max) {
		this.max = max;
	}
	
	public void offer(Event event) {
		synchronized(eventQueue) {
			if(eventQueue.size() >= max) {
				try {
					console(" the queue is full.");
					eventQueue.wait();
					System.out.println("offer eventQueue.wait()");
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			console(" the new event is submitted");
			eventQueue.addLast(event);
			eventQueue.notify();
		}
	}
	
	public Event take() {
		synchronized(eventQueue) {
			if(eventQueue.isEmpty()) {
				try {
					console(" the queue is empty.");
					eventQueue.wait();
					System.out.println("take eventQueue.wait()");
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			
			Event event = eventQueue.removeFirst();
			this.eventQueue.notify();
			console(" the event " + event + " is handled.");
			return event;
		}
	}
	
	private void console(String message) {
		System.out.printf("%s:%s\n", Thread.currentThread().getName(), message);
	}
}
