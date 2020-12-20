package com.lbq.concurrent.chapter01;
/**
 * 通过对index进行static修饰，做到了多线程下共享资源的唯一性，看起来似乎满足了我们的需求，
 * 但是只有一个index共享资源，如果共享资源很多呢？共享资源要经过一些比较复杂的计算呢？
 * 不可能都使用static修饰，而且static修饰的变量生命周期很长，所以Java提供了一个接口Runnable专门用于解决该问题，
 * 将线程的控制和业务逻辑的运行彻底分离开来。
 * @author 14378
 *
 */
public class TicketWindow extends Thread {
	//柜台名称
	private final String name;
	
	private static final int MAX = 50;
	
	private static int index = 1;//通过对index进行static修饰，做到了多线程下共享资源的唯一性
	
	public TicketWindow(String name) {
		this.name = name;
	}
	
	@Override
	public void run() {
		while(index <= MAX) {
			System.out.println("柜台：" + name + " 当前的号码是：" + (index++));
		}
	}
	
	public static void main(String[] args) {
		
		TicketWindow ticketWindow1 = new TicketWindow("一号出号机");
		ticketWindow1.start();
		
		TicketWindow ticketWindow2 = new TicketWindow("二号出号机");
		ticketWindow2.start();
		
		TicketWindow ticketWindow3 = new TicketWindow("三号出号机");
		ticketWindow3.start();
		
		TicketWindow ticketWindow4 = new TicketWindow("四号出号机");
		ticketWindow4.start();
	}
}
