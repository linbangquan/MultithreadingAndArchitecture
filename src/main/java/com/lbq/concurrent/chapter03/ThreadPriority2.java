package com.lbq.concurrent.chapter03;
/**
 * 设置线程的优先级，只需要调用setPriority方法即可，下面我们打开Thread的源码，一起来分析一下：
 * public final void setPriority(int newPriority){
 * 		ThreadGroup g;
 * 		checkAccess();
 * 		if(newPriority > MAX_PRIORITY || newPriority < MIN_PRIORITY){
 * 			throw new IllegalArgumentException();
 * 		}
 * 		if((g = getThreadGroup()) != null){
 * 			if(newPriority > g.getMaxPriority()){
 * 				newPriority = g.getMaxPriority():
 * 			}
 * 			setPriority0(priority = newPriority);
 * 		}
 * }
 * 通过上面源码的分析，我们可以看出，线程的优先级不能小于1也不能大于10，
 * 如果指定的线程优先级大于线程所在group的优先级，那么指定的优先级将会失效，取而代之的是group的最大优先级。
 * @author 14378
 *
 */
public class ThreadPriority2 {

	public static void main(String[] args) {
		//定义一个线程组
		ThreadGroup group = new ThreadGroup("test");
		//将线程组的优先级指定为7
		group.setMaxPriority(7);
		Thread thread = new Thread(group, "test-thread");
		//企图将线程的优先级设定为10
		thread.setPriority(10);
		//企图未遂
		System.out.println(thread.getPriority());
		
	}
}
