package com.lbq.concurrent.chapter16;
/**
 * Single Thread Execution模式是指在同一时刻只能有一个线程去访问共享资源，就像独木桥一样每次只允许一人通行，
 * 简单来说，Single Thread Execution就是采用排他式的操作保证在同一时刻只能有一个线程访问共享资源。
 * 
 * 16.1 机场过安检
 * 相信大大家都有乘坐飞机的经历，在进入登机口之前必须经过安全检查，安检口类似于独木桥，每次只能通过一个人，
 * 工作人员除了检查你的登机牌以外，还要联网检查身份证信息以及是否携带危险物品。
 * 
 * 16.1.1 非线程安全
 * 先模拟一个非线程安全的安检口类，旅客（线程）分别手持登机牌和身份证接受工作人员的检查。
 * FlightSecurity比较简单，提供了一个pass方法，将旅客的登机牌和身份证传递给pass方法，在pass方法中调用check方法对旅客进行检查，
 * 检查的逻辑也足够的简单，只需要检测登机牌和身份证首字母是否相等。
 * @author 14378
 *
 */
public class FlightSecurity {

	private int count = 0;
	//登机牌
	private String boardingPass = "null";
	//身份证
	private String idCard = "null";
	
	public void pass(String boardingPass, String idCard) {//synchronized
		this.boardingPass = boardingPass;
		this.idCard = idCard;
		this.count++;
		check();
	}

	private void check() {
		// 简单的测试，当登机牌和身份证首字母不相同时则表示检查不通过
		if(boardingPass.charAt(0) != idCard.charAt(0)) {
			throw new RuntimeException("====Exception====" + toString());
		}		
	}
	
	public String toString() {
		return "The " + count + " passengers, boardingPass [" + boardingPass + "], idCard [" + idCard + "]";	
	}
}
