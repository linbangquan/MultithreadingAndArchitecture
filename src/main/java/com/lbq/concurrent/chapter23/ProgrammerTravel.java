package com.lbq.concurrent.chapter23;

import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
/**
 * 2.程序测试齐心协力打开门阀
 * 连续得加班让这几位程序员游玩的心愿非常迫切，他们力争能够在礼拜六的早上十点到达城市广场，
 * 因此每个人都非常积极，下面的程序模拟了程序员乘坐不同的交通工具到达城市广场的场景。
 * 
 * ProgrammerTravel继承自Thread代表程序员，需要三个构造参数，第一个是前文中设计的latch(门阀)，
 * 第二个是程序员的名称，比如前文的Jack、Gavin等，第三个参数则表示他们搭乘的交通工具。
 * @author 14378
 *
 */
/** 程序员旅游线程 **/
public class ProgrammerTravel extends Thread {

	/** 门阀 **/
	private final Latch latch;
	/** 程序员 **/
	private final String programmer;
	/** 交通工具 **/
	private final String transportation;
	
	/** 通过构造函数传入latch，programmer，transportation **/
	public ProgrammerTravel(Latch latch, String programmer, String transportation) {
		this.latch = latch;
		this.programmer = programmer;
		this.transportation = transportation;
	}
	
	@Override
	public void run() {
		System.out.println(programmer + " start take the transportation [" + transportation + "]");
		try {
			//程序员乘坐交通工具花费在路上的时间（使用随机数字模拟）
			TimeUnit.SECONDS.sleep(ThreadLocalRandom.current().nextInt(10));
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		System.out.println(programmer + " arrived by " + transportation);
		//完成任务时使计数器减一
		latch.countDown();
	}
}
