package com.lbq.concurrent.chapter24;

import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
/**
 * TaskHandler用于处理每一个提交的Request请求，由于TaskHandler将被Thread执行，因此需要实现Runnable接口
 * TaskHandler代表每一个工作人员接收到任务后的处理逻辑。
 * @author 14378
 *
 */
public class TaskHandler implements Runnable {
	//需要处理的Request请求
	private final Request request;
	
	public TaskHandler(Request request) {
		this.request = request;
	}
	@Override
	public void run() {
		System.out.println("Begin handle " + request);
		slowly();
		System.out.println("End handler " + request);
	}
	//模拟请求处理比较耗时，使线程进入短暂的休眠阶段
	private void slowly() {
		try {
			TimeUnit.SECONDS.sleep(ThreadLocalRandom.current().nextInt(10));
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
	}

}
