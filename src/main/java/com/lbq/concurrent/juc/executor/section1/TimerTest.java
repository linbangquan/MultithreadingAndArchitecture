package com.lbq.concurrent.juc.executor.section1;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

public class TimerTest {

	public static void main(String[] args) {
		Timer timer = new Timer();
		timer.scheduleAtFixedRate(new TimerTask() {

			@Override
			public void run() {
				System.out.println(new Date());
				
			}}, 1000, 60 * 1000);//根据固定周期来执行TimerTask
	}

}
