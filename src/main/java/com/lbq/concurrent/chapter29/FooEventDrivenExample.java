package com.lbq.concurrent.chapter29;

import java.util.LinkedList;
import java.util.Queue;
/**
 * 29.1.2 Event Handlers
 * Event Handlers主要用于处理Event，比如一些filtering或者transforming数据的操作等，
 * 下面我们写两个比较简单的方法，代码如下：
 * handlerEventA 方法只是简单地将Event中的data进行了lowerCase之后的输出。
 * 同样，handlerEventB 方法也是足够的简单，直接将Event中字符串数据变成大写进行了控制台输出。
 * 
 * 29.1.3 Event Loop
 * Event Loop处理接收到的所有Event，并且将它们分配给合适的Handler去处理，代码如下：
 * 
 * 在EventLoop中，每一个Event都将从Queue中移除出去，通过类型匹配交给合适的Handler去处理。
 * 虽然这个EDA的设计足够简单，但是通过它我们可以感受到EDA中三个重要组件之间的交互关系，
 * 其对接下来的内容学习也会有一定的帮助。
 * @author 14378
 *
 */
public class FooEventDrivenExample {
	//用于处理A类型的Event
	public static void handleEventA(Event e) {
		System.out.println(e.getData().toLowerCase());
	}
	//用于处理B类型的Event
	public static void handleEventB(Event e) {
		System.out.println(e.getData().toUpperCase());
	}
	
	public static void main(String[] args) {
		Queue<Event> events = new LinkedList<>();
		events.add(new Event("A", "Hello"));
		events.add(new Event("A", "I am Event A"));
		events.add(new Event("B", "I am Event B"));
		events.add(new Event("B", "World"));
		Event e;
		while((e = events.poll()) != null) {
			//从消息队列中不断移除，根据不同的类型进行处理
			//e = events.remove();events.poll();
			switch(e.getType()) {
			case "A":
				handleEventA(e);
				break;
			case "B":
				handleEventB(e);
				break;
			}
		}
	}
}
