package com.lbq.concurrent.chapter16;

import java.util.concurrent.TimeUnit;
/**
 * Tableware代表餐具的类，比较简单，其中toolName表示餐具的名称，我们再来创建吃面的线程，代码如下。
 * 在创建吃面线程时需要指定名称和吃面用的刀叉，在eat方法中先拿起左手的餐具，然后尝试获取右手的餐具，代码如下。
 * 运行下面的程序，吃不了几个回合便会陷入死锁，在程序最后卡住的地方不难发现B获取了刀企图获取叉，
 * 而A则相反手持叉企图获取刀，因此进入了阻塞，输出如下：
 * A take up Tool: fork(left)
 * B take up Tool: knife(left)
 * @author 14378
 *
 */
public class EatNoodleThread extends Thread {

	private final String name;
	//左手边的餐具
	private final Tableware leftTool;
	//右手边的餐具
	private final Tableware rightTool;

	public EatNoodleThread(String name, Tableware leftTool, Tableware rightTool) {
		super();
		this.name = name;
		this.leftTool = leftTool;
		this.rightTool = rightTool;
	}
	
	@Override
	public void run() {
		while(true) {
			this.eat();
		}
	}
	//吃面条的过程
	private void eat() {
		synchronized(leftTool) {
			try {
				TimeUnit.SECONDS.sleep(1);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			System.out.println(name + " take up " + leftTool + "(left)");
			synchronized(rightTool) {
				try {
					TimeUnit.SECONDS.sleep(1);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				System.out.println(name + " take up " + rightTool + "(right)");
				System.out.println(name + " is eating now. ");
				System.out.println(name + " put down " + rightTool + "(right)");
			}
			System.out.println(name + " put down " + leftTool + "(left)");
		}		
	}
	
	public static void main(String[] args) {
		Tableware fork = new Tableware("fork");
		Tableware knife = new Tableware("knife");
		new EatNoodleThread("A", fork, knife).start();
		new EatNoodleThread("B", knife, fork).start();
	}
	
}
