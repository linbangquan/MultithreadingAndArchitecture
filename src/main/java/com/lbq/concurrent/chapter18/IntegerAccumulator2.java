package com.lbq.concurrent.chapter18;

import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;
/**
 * 18.2.3 不可变的累加器对象设计
 * 18.2.2节中通过同步的方式解决了线程安全性的问题，正确的加锁方式固然能使得一个类变成线程安全的，
 * 比如java.utils.Vector，但是我们需要的是设计出类似于java.lang.String的不可变类。
 * 
 * 重构后的IntegerAccumulator，使用了final修饰其的目的是为了防止由于继承重写而导致失去线程安全性，
 * 另外init属性被final修饰不允许线程对其进行改变，在构造函数中赋值后将不会再改变。
 * add方法并未在原有init的基础之上进行累加，而是创建了一个全新的IntegerAccumulator，
 * 并没提供任何修改原始IntegerAccumulator的机会，运行上面的程序不会出现ERROR的情况。
 * 
 * @author 14378
 *
 */
//不可变对象不允许被继承
public class IntegerAccumulator2 {

	private final int init;
	//构造时传入初始值
	public IntegerAccumulator2(int init) {
		this.init = init;
	}
	//构造新的累加器，需要用到另外一个accumulator和初始值
	public IntegerAccumulator2(IntegerAccumulator2 accumulator, int init) {
		this.init = accumulator.getValue() + init;
	}
	//对初始值增加i，每次相加都会产生一个新的IntegerAccumulator
	public IntegerAccumulator2 add(int i) {
		return new IntegerAccumulator2(this, i);
	}
	//返回当前的初始值
	public int getValue() {
		return this.init;
	}
	public static void main(String[] args) {
		//定义累加器，并且将设置初始值为0
		IntegerAccumulator2 accumulator = new IntegerAccumulator2(0);
		//定义三个线程，并且分别启动
		IntStream.range(0, 3).forEach(i -> new Thread(() -> {
			int inc = 0;
			while(true) {
				//首先获得old value
				int oldValue = accumulator.getValue();
				//然后调用add方法计算
				int result = accumulator.add(inc).getValue();
				System.out.println(oldValue + "+" + inc + "=" + result);
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
