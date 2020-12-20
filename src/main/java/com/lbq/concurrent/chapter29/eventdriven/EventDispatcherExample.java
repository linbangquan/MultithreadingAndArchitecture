package com.lbq.concurrent.chapter29.eventdriven;
/**
 * 由于所有的类都存放于一个文件中，因此看起来测试代码比较多，其实结构还是非常清晰的，
 * InputEvent是一个Message，它包含了两个Int类型的属性，而InputEventHandler是对InputEvent消息的处理，
 * 接收到了InputEvent消息之后，分别对X和Y进行相加操作，然后将结果封装成ResultEvent提交给EventDispatcher，
 * ResultEvent相对比较简单，只包含了计算结果的属性，ResultEventHandler则将计算结果输出到控制台上。
 * 
 * 通过下面这个例子的运行你会发现，不同数据的处理过程之间根本无须知道彼此的存在，一切都由EventDispatcher这个Router来控制，
 * 它会给你想要的一切，这是稀疏耦合(松耦合)的设计。
 * 
 * EDA的设计除了松耦合特性之外，扩展性也是非常强的，比如Channel非常容易扩展和替换，另外由于Dispatcher统一负责Event的调配，
 * 因此在消息通过Channel之前可以进行很多过滤、数据验证、权限控制、数据增强(Enhance)等工作。
 * @author 14378
 *
 */
public class EventDispatcherExample {
	/**
	 * InputEvent中定义了两个属性x和y，主要用于在其他Channel中的运算
	 * @author 14378
	 *
	 */
	static class InputEvent extends Event {
		private final int x;
		private final int y;
		
		public InputEvent(int x, int y) {
			this.x = x;
			this.y = y;
		}

		public int getX() {
			return x;
		}

		public int getY() {
			return y;
		}
	}
	/**
	 * 用于存放结果的Event
	 * @author 14378
	 *
	 */
	static class ResultEvent extends Event {
		private final int result;
		
		public ResultEvent(int result) {
			this.result = result;
		}
		public int getResult() {
			return result;
		}
	}
	/**
	 * 处理ResultEvent的Handler(Channel)，只是简单地将计算结果输出到控制台
	 * @author 14378
	 *
	 */
	static class ResultEventHandler implements Channel<ResultEvent>{

		@Override
		public void dispatch(ResultEvent message) {
			System.out.println("The result is: " + message.getResult());
		}
		
	}
	/**
	 * InputEventHandler需要向Router发送Event，因此在构造的时候需要传入Dispatcher
	 * @author 14378
	 *
	 */
	static class InputEventHandler implements Channel<InputEvent>{

		private final EventDispatcher dispatcher;
		
		private InputEventHandler(EventDispatcher dispatcher) {
			this.dispatcher = dispatcher;
		}
		/**
		 * 将计算的结果构造成新的Event提交给Router
		 */
		@Override
		public void dispatch(InputEvent message) {
			System.out.printf("X:%d,Y:%d\n", message.getX(), message.getY());
			int result = message.getX() + message.getY();
			dispatcher.dispatch(new ResultEvent(result));
		}
		
	}
	
	public static void main(String[] args) {
		//构造Router
		EventDispatcher dispatcher = new EventDispatcher();
		//将Event和Handler(Channel)的绑定关系注册到Dispatcher
		dispatcher.registerChannel(InputEvent.class, new InputEventHandler(dispatcher));
		dispatcher.registerChannel(ResultEvent.class, new ResultEventHandler());
		dispatcher.dispatch(new InputEvent(1, 2));
	}

}
