package com.lbq.concurrent.chapter18;

import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;
/**
 * 18.2.1 非线程安全的累加器
 * 不可变对象最核心的地方在于不给外部修改共享资源的机会，这样就会避免多线程情况下的数据冲突而导致的数据不一致的情况，
 * 又能避免因为对锁的依赖而带来的性能降低，好了，在本节中我们将模仿java.lang.String的方式实现一个不可变的int类型累加器，
 * 先来看看不加同步的累加器。
 * 这段程序既没有对共享资源进行共享锁的保护，也没有进行不可变的设计，在程序的运行过程中偶尔会出现错误的情况。
 * 
 * 18.2.2 方法同步增加线程安全性
 * 18.2.1中的程序出现错误的原因是显而易见的，共享资源被多个线程操作未加任何同步控制，出现数据不一致的问题是情理之中的事情，
 * 下面我们对init变量的操作加以同步，情况就会变得不一样了。
 * 
 * 这里将数据同步的控制放在了线程的逻辑执行单元中，而在IntegerAccumulator中未增加任何同步的控制，
 * 如果单纯对getValue方法和add方法增加同步控制，虽然保证了单个方法的原子性，但是两个原子类型的操作在一起未必就是原子性的，
 * 因此在线程的逻辑执行单元中增加同步控制是最为合理的。
 * @author 14378
 *
 */
public class IntegerAccumulator {

	private int init;
	//构造时传入初始值
	public IntegerAccumulator(int init) {
		this.init = init;
	}
	//对初始值增加i
	public int add(int i) {
		this.init += i;
		return this.init;
	}
	//返回当前的初始值
	public int getValue() {
		return this.init;
	}
	public static void main(String[] args) {
		//定义累加器，并且将设置初始值为0
		IntegerAccumulator accumulator = new IntegerAccumulator(0);
		//定义三个线程，并且分别启动
		IntStream.range(0, 3).forEach(i -> new Thread(() -> {
			int inc = 0;
			while(true) {
				int oldValue;
				int result;
				synchronized(IntegerAccumulator.class) {
					oldValue = accumulator.getValue();
					result = accumulator.add(inc);
				}
				//首先获得old value
				//int oldValue = accumulator.getValue();
				//然后调用add方法计算
				//int result = accumulator.add(inc);
				//System.out.println(oldValue + "+" + inc + "=" + result);
				//经过验证，如果不合理，则输出错误信息
				if(inc + oldValue != result) {
					System.out.println("ERROR:" + oldValue + "+" + inc + "=" + result);
				}
				inc++;
				//模拟延迟
				slowly();
			}
		}).start());

	}

	private static void slowly() {
		try {
			TimeUnit.MILLISECONDS.sleep(1);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
