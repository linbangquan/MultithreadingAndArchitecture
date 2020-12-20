package com.lbq.concurrent.chapter16;
/**
 * 看起来每一个客户都是合法的，因为每一个客户的身份证和登机牌首字母都一样，运行上面的程序却出现了错误，而且错误的情况还不太一样，笔者运行了5次左右，发现了两种类型的错误信息。
 * ====Exception====The 510 passengers, boardingPass [CF123456], idCard [C123456]
 * ====Exception====The 511 passengers, boardingPass [CF123456], idCard [B123456]
 * 首字母相同检查不能通过和首字母不相同检查不能通过，为什么会出现这样的情况呢？首字母相同却不能通过？
 * 更加奇怪的是传入的参数明明全都是首字母相同的，为什么会出现首字母不相同的错误呢?
 * 
 * 16.1.2 问题分析
 * 在本节中我们将尝试分析16.1.1节出现的两种错误情况，在多线程的情况下调用pass方法，如果传入“A123456”“AF123456”，
 * 虽然参数的传递百分之百能保证就是这两个值，但是在pass方法中对boardingPass和idCard的赋值很有可能交叉的，不能保证原子性操作。
 * 
 * (1) 首字母相同却未通过检查
 * 图16-2所示的为首字母相同却无法通过安检的分析过程。
 * 这种情况的执行步骤顺序如下。
 * 1)线程A调用pass方法，传入“A123456”“AF123456”并且对idCard赋值成功，由于CPU调度器时间片的轮转，CPU的执行权归B线程所有。
 * 2)线程B调用pass方法，传入“B123456”“BF123456”并且对idCard赋值成功，覆盖A线程赋值的idCard。
 * 3)线程A重新获得CPU的执行权，将boardingPass赋予AF123456，因此check无法通过。
 * 4)在输出toString之前，B线程成功将boardingPass覆盖为BF123456。
 * (2) 为何出现首字母不相同的情况
 * 明明传入的身份证和登机牌首字母都相同，可为何在运行的过程中会出现首字母不相同的情况，下面我们也通过图示的方式进行分析。
 * 这种情况的执行步骤顺序如下。
 * 1)线程A调用pass方法，传入“A123456”“AF123456”并且对idCard赋值成功，由于CPU调度器时间片的轮转，CPU的执行权归B线程所有。
 * 2)线程B调用pass方法，传入“B123456”“BF123456”并且对idCard赋值成功，覆盖A线程赋值的idCard。
 * 3)线程A重新获得CPU执行权，将boardingPass赋予AF123456，因此check无法通过。
 * 4)线程A检查不通过，输出idCard="A123456"和boardingPass="BF123456"。
 * 
 * 16.1.3 线程安全
 * 16.1.1节中出现得问题说到底就是数据同步的问题，虽然线程传递给pass方法的两个参数能够百分之百地保证首字母相同，
 * 可是在为FlightSecurity中的属性赋值的时候会出现多个线程交错的情况，结合我们在第一部分第4章的所讲内容可知，
 * 需要对共享资源增加同步保护，改进代码如下：（pass方法加synchronized关键字）
 * 修改后的pass方法，无论运行多久都不会再出现检查出错的情况了，为什么只在pass方法增加synchronized关键字，
 * check以及toString方法都有对共享资源的访问，难道它们不加同步就不会引起错误么?由于check方法是在pass方法中执行的，
 * pass方法加同步已经保证了single thread execution，因此check方法不需要增加同步，toString方法原因与此相同。
 * 
 * 何时适合使用single thread execution模式呢？答案如下。
 * 1.多线程访问资源的时候，被synchronized同步的方法总是排他性的。
 * 2.多线程对某个类的状态发生改变的时候，比如FlightSecurity的登机牌以及身份证。
 * 
 * 在Java中经常会听到线程安全的类和线程非安全的类，所谓线程安全的类是指多个线程在对某个类的实例同时进行操作时，不会引起数据不一致的问题，
 * 反之则是线程非安全的类，在线程安全的类中经常会看到synchronized关键字的身影。
 * 
 * @author 14378
 *
 */
public class FlightSecurityTest {
	//旅客线程
	static class Passengers extends Thread{
		//机场安检类
		private final FlightSecurity flightSecurity;
		
		//旅客的身份证
		private final String idCard;
		
		//旅客的登机牌
		private final String boardingPass;
		//构造旅客时传入身份证、登机牌以及机场安检类
		public Passengers(FlightSecurity flightSecurity, String idCard, String boardingPass) {
			this.flightSecurity = flightSecurity;
			this.idCard = idCard;
			this.boardingPass = boardingPass;
		}
		
		@Override
		public void run() {
			while(true) {
				//旅客不断地过安检
				flightSecurity.pass(boardingPass, idCard);
			}
		}
		
	}
	public static void main(String[] args) {
		//定义三个旅客，身份证和登机牌首字母均相同
		final FlightSecurity flightSecurity = new FlightSecurity();
		new Passengers(flightSecurity, "A123456", "AF123456").start();
		new Passengers(flightSecurity, "B123456", "BF123456").start();
		new Passengers(flightSecurity, "C123456", "CF123456").start();
	}
}
