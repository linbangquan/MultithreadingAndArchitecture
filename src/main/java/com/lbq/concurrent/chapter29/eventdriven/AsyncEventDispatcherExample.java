package com.lbq.concurrent.chapter29.eventdriven;

import java.util.concurrent.TimeUnit;

import com.lbq.concurrent.chapter29.eventdriven.EventDispatcherExample.ResultEvent;
/**
 * 好了，下面我们写一个针对异步EDA的测试，用法和同步的非常类似，只不过几个关键的类需要使用异步的来代替。
 * 当dispatcher分配一个Event的时候，如果执行非常缓慢也不会影响下一个Event被dispatch，
 * 这主要得益于我们采用了异步的处理方式(ExecutorService本身存在的任务队列可以允许异步提交一个数量级的数据)。
 * @author 14378
 *
 */
public class AsyncEventDispatcherExample {
	/**
	 * 主要用于处理InputEvent，但是需要继承AsyncChannel
	 * @author 14378
	 *
	 */
	static class AsyncInputEventHandler extends AsyncChannel {
		private final AsyncEventDispatcher dispatcher;
		
		public AsyncInputEventHandler(AsyncEventDispatcher dispatcher) {
			this.dispatcher = dispatcher;
		}

		/**
		 * 不同于以同步的方式实现dispatcher，异步的方式需要实现handle
		 */
		@Override
		protected void handle(Event message) {
			EventDispatcherExample.InputEvent inputEvent = (EventDispatcherExample.InputEvent) message;
			System.out.printf("X:%d, Y:%d\n", inputEvent.getX(), inputEvent.getY());
			try {
				TimeUnit.SECONDS.sleep(5);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			int result = inputEvent.getX() + inputEvent.getY();
			dispatcher.dispatch(new EventDispatcherExample.ResultEvent(result));
		}
	}
	/**
	 * 主要用于处理InputEvent，但是需要继承AsyncChannel
	 * @author 14378
	 *
	 */
	static class AsyncResultEventHandler extends AsyncChannel {

		@Override
		protected void handle(Event message) {
			EventDispatcherExample.ResultEvent resultEvent = (ResultEvent) message;
			try {
				TimeUnit.SECONDS.sleep(5);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			System.out.println("The result is:" + resultEvent.getResult());
		}
	}
	
	public static void main(String[] args) {
		//定义AsyncEventDispatcher
		AsyncEventDispatcher dispatcher = new AsyncEventDispatcher();
		//注册Event和Channel之间的关系
		dispatcher.registerChannel(EventDispatcherExample.InputEvent.class, new AsyncInputEventHandler(dispatcher));
		dispatcher.registerChannel(EventDispatcherExample.ResultEvent.class, new AsyncResultEventHandler());
		//提交需要处理的Message
		dispatcher.dispatch(new EventDispatcherExample.InputEvent(1, 2));
		dispatcher.dispatch(new EventDispatcherExample.InputEvent(2, 3));
		dispatcher.dispatch(new EventDispatcherExample.InputEvent(3, 4));
		dispatcher.dispatch(new EventDispatcherExample.InputEvent(4, 5));
		dispatcher.dispatch(new EventDispatcherExample.InputEvent(5, 6));
	}
}
