package com.lbq.concurrent.chapter02;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
/**
 * 虚拟机栈内存是线程私有的，也就是说每个线程都会占有指定的内存大小，我们粗略地认为一个Java进程的内存大小为：堆内存 + 线程数量 * 栈内存。
 * 
 * 在操作系统中一个进程的内存大小是有限制的，这个限制称为地址空间，操作系统则会将进程内存的大小控制在最大地址空间以内，
 * 下面的公式是一个相对比较精准的计算线程数量的公式，其中ReservedOsMemory是系统保留内存，一般在136MB左右：
 * 线程数 = (最大地址空间（MaxProcessMemory）- JVM堆内存 - ReservedOsMemory) / ThreadStackSize（XSS）
 * 
 * @author 14378
 *
 */
public class ThreadCounter extends Thread {

	final static AtomicInteger counter = new AtomicInteger(0);
	public static void main(String[] args) {
		try {
			while(true) {
				new ThreadCounter().start();
			}
		}catch(Throwable e) {
			System.out.println("failed At=>" + counter.get());//160376
		}
	}
	
	@Override
	public void run() {
		try {
			System.out.println("The " + counter.getAndIncrement() + " thread be created.");
			TimeUnit.MINUTES.sleep(10);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
