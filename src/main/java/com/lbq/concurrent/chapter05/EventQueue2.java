package com.lbq.concurrent.chapter05;

import java.util.LinkedList;
/**
 * 生产者消费者
 * 1.notify方法
 * 多线程间通信需要用到Object的notifyAll方法，该方法与notify比较类似，都可以唤醒由于调用了wait方法而阻塞的线程，
 * 但是notify方法只能唤醒其中的一个线程，而notifyAll方法则可以同时唤醒全部的阻塞线程，同样被唤醒的线程仍需要继续争抢monitor的锁。
 * 2.生产者消费者
 * 在5.2.1节中增定义了一个EventQueue，该队列在多个线程同时并发的情况下会出现数据不一致的情况，大致可分为两类：
 * 其一是LinkedList中没有元素的时候仍旧调用了removeFirst方法，其二是当LinkedList中元素超过10个的时候仍旧执行了addLast方法。
 * (1)LinkedList为空时执行removeFirst方法
 * 也许有读者会有疑问，EventQueue中的方法都增加了synchronized数据同步，为何还会存在数据不一致的情况？
 * 假设EventQueue中的元素为空，两个线程在执行take方法时分别调用wait方法进入了阻塞之中，另外一个offer线程执行addLast方法之后唤醒了其中一个阻塞的take线程，
 * 该线程顺利消费了 一个元素之后恰巧再次唤醒了一个take线程，这时就会导致执行空LinkedList的removeFist方法。
 * (2)LinkedList元素为10时执行addLast方法
 * 假设某个时刻EventQueue中存在10个Event数据，其中两个线程在执行offer方法的时候分别因为调用了wait方法而进入阻塞中，
 * 另外的一个线程执行take方法消费了event元素并且唤醒了一个offer线程，而该offer线程执行了addLast方法之后，
 * queue中的元素为10，并且再次执行唤醒方法，恰巧另外一个offer线程也被唤醒，因此可以绕开阈值检查eventQueue.size()>=max,
 * 致使EventQueue中的元素超过10个。
 * (3)改进
 * 只需要将临界值的判断if更改为while，将notify更改为notifyAll即可。
 * 
 * 线程休息室wait set
 * 在虚拟机规范中存在一个wait set的概念，至于该wait set是怎样的数据结构，jdk官方并没有给出明确的定义，
 * 不同厂家的jdk有着不同的实现方式，甚至相同的jdk厂家不同的版本也存在着差异，但是不管怎样，
 * 线程调用了某个对象的wait方法之后都会被加入与该对象monitor关联的wait set中，并且释放monitor的所有权。
 * 1.若干个线程调用了wait方法之后被加入与monitor关联的wait set中，待另外一个线程调用该monitor的notify方法之后，
 * 其中一个线程会从wait set中弹出，至于是随机弹出还是以先进先出的方式弹出，虚拟机规范同样也没有给出强制的要求。
 * 2.而执行notifyAll则不需要考虑哪个线程会被弹出，因为wait set中的所有wait线程都将被弹出。
 * @author 14378
 *
 */
public class EventQueue2 {

	private final int max;
	
	static class Event{}
	
	private final LinkedList<Event> eventQueue = new LinkedList<>();
	
	private final static int DEFAULT_MAX_EVENT = 10;
	public EventQueue2() {
		this(DEFAULT_MAX_EVENT);
	}
	
	public EventQueue2(int max) {
		this.max = max;
	}
	
	public void offer(Event event) {
		synchronized(eventQueue) {
			while(eventQueue.size() >= max) {
				try {
					console(" the queue is full.");
					eventQueue.wait();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			console(" the new event is submitted");
			eventQueue.addLast(event);
			eventQueue.notifyAll();
		}
	}
	
	public Event take() {
		synchronized(eventQueue) {
			while(eventQueue.isEmpty()) {
				try {
					console(" the queue is empty.");
					eventQueue.wait();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			
			Event event = eventQueue.removeFirst();
			this.eventQueue.notifyAll();
			console(" the event " + event + " is handled.");
			return event;
		}
	}
	
	private void console(String message) {
		System.out.printf("%s:%s\n", Thread.currentThread().getName(), message);
	}
}
