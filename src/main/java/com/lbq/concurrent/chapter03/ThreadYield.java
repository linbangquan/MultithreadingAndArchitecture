package com.lbq.concurrent.chapter03;

import java.util.stream.IntStream;
/**
 * yield方法属于一种启发式的方法，其会提醒调度器我愿意放弃当前的cpu资源，如果cpu的资源不紧张，则会忽略这种提醒。
 * 调用yield方法会使当前线程从running状态切换到runnable状态，一般这个方法不太常用
 * 
 * 下面的程序运行很多次，你会发现输出的结果不一致，有时候是0最先打印出来，有时候是1最先打印出来，但是当你打开代码的注释部分，你会发现，顺序始终是0，1.
 * 因为第一个线程如果最先获得了cpu资源，它会比较谦虚，主动告诉cpu调度器释放了原本属于自己的资源，但是yield只是一个提示（hint），cpu调度器并不会担保每次都能满足yield提示。
 * 
 * yield和sleep
 * 看过前面的内容之后，会发现yield和sleep有一些混淆的地方，在jdk1.5以前的版本中yield的方法事实上是调用了sleep(0),
 * 但是它们之间存在着本质的区别，具体如下：
 * 1.sleep会导致当前线程的暂停指定的时间，没有cpu时间片的消耗。
 * 2.yield只是对CPU调度器的一个提示，如果CPU调度器没有忽略这个提示，它会导致线程上下文的切换。
 * 3.sleep会使线程短暂block，会在给定的时间内释放CPU资源。
 * 4.yield会使running状态的Thread进入runnable状态（如果CPU调度器没有忽略这个提示的话）。
 * 5.sleep几乎百分之百地完成了给定的时间的休眠，而yield的提示并不能一定担保。
 * 6.一个线程sleep另一个线程调用interrupt会捕获到中断信号，而yield则不会。
 * @author 14378
 *
 */
public class ThreadYield {

	public static void main(String[] args) {
		IntStream.range(0, 2).mapToObj(ThreadYield::create).forEach(Thread :: start);
	}

	private static Thread create(int index) {
		return new Thread(() -> {
			//注释部分
//			if(index == 0) {
//				Thread.yield();
//			}
			System.out.println(index);
		}) ;
	}
}
