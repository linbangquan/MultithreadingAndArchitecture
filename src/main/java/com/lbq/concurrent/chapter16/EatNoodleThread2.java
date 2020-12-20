package com.lbq.concurrent.chapter16;

import java.util.concurrent.TimeUnit;
/**
 * 在EatNoodleThread中使用TablewarePair代替leftTool和rightTool，这样就可以避免交叉锁的情况，代码如下。
 * 更改后的程序无论运行多久都不会出现死锁的情况，因为在同一时间只能有一个线程获得刀和叉。
 * 
 * 16.2.3 哲学家吃面
 * 哲学家吃面是解释操作系统中多个进程竞争资源的经典问题，每个哲学家的左右手都有吃面用的刀叉，但是不足以同时去使用，
 * 比如A哲学家想吃面，必须拿起左手边的叉和右手边的刀，但是有可能叉和刀都被等待别人放下叉等容易引起死锁的问题。
 * 
 * 16.3 本章总结
 * 将某个类设计成线程安全的类，用Single Thread Execution控制是其中的方法之一，
 * 但是子类如果继承了线程安全的类并且打破了Single Thread Execution的方式，
 * 就会打破方法的安全性，这种情况一般称为继承异常。
 * 在Single Thread Execution 中，synchronized关键字起到了决定性的作用，但是synchronized的排他性是以性能的牺牲为代价的，
 * 因此在保证线程安全的前提下应尽量缩小synchronized的作用域。
 * 
 * @author 14378
 *
 */
public class EatNoodleThread2 extends Thread {

	private final String name;
	
	private final TablewarePair tablewarePair;

	public EatNoodleThread2(String name, TablewarePair tablewarePair) {
		this.name = name;
		this.tablewarePair = tablewarePair;
	}
	
	@Override
	public void run() {
		while(true) {
			try {
				TimeUnit.SECONDS.sleep(1);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			this.eat();
		}
	}
	//吃面条的过程
	private void eat() {
		synchronized(tablewarePair) {
			System.out.println(name + " take up " + tablewarePair.getLeftTool() + "(left)");
			System.out.println(name + " take up " + tablewarePair.getRightTool() + "(right)");
			System.out.println(name + " is eating now. ");
			System.out.println(name + " put down " + tablewarePair.getRightTool() + "(right)");

			System.out.println(name + " put down " + tablewarePair.getLeftTool() + "(left)");
		}		
	}
	
	public static void main(String[] args) {
		Tableware fork = new Tableware("fork");
		Tableware knife = new Tableware("knife");
		TablewarePair tablewarePair = new TablewarePair(fork, knife);
		new EatNoodleThread2("A", tablewarePair).start();
		new EatNoodleThread2("B", tablewarePair).start();
	}
	
}
