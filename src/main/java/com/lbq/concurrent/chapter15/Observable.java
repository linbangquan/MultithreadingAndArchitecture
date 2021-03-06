package com.lbq.concurrent.chapter15;
/**
 * 监控任务的生命周期
 * 15.1 场景描述
 * 虽然Thread为我们提供了可获取状态，以及判断是否alive的方法，但是这些方法均是针对线程本身的，
 * 而我们提交的任务Runnable在运行过程中所处的状态如何是无法直接获得的，比如它什么时候开始，什么时候结束，
 * 最不好的一种体验是无法获得Runnable任务执行后的结果。一般情况下想要获得最终结果，我们不得不为Thread或者Runnable传入共享变量，
 * 但是在多线程的情况下，共享变量将导致资源的竞争从而增加了数据不一致性的安全隐患。
 * 
 * 15.2 当观察者模式遇到Thread
 * 当某个对象发生状态改变需要通知第三方的时候，观察者模式就特别适合胜任这样的工作。观察者模式需要有事件源，也就是引发状态改变的源头，
 * 很明显Thread负责执行任务的逻辑单元，它最清楚整个过程的始末周期，而事件的接收者则是通知接收者一方，严格意义上的观察者模式是需要Observer的集合的，
 * 我们在这里不需要完全遵守这样的规则，只需将执行任务的每个阶段都通知给观察者即可。
 * 
 * 15.2.1 接口定义
 * 该接口主要是暴露给调用者使用的，其中四个枚举类型分别代表了当前任务执行生命周期的各个阶段，具体如下。
 * 1.getCycle()方法用于获取当前任务处于哪个执行阶段。
 * 2.start()方法的目的主要是为了屏蔽Thread类其他的API,可通过Observable的start对线程进行启动。
 * 3.interrupt()方法的作用与start一样，可通过Observable的interrupt对当前线程进行中断。
 * @author 14378
 *
 */
public interface Observable {
	//任务生命周期的枚举类型
	enum Cycle {
		STARTED, RUNNING, DONE, ERROR
	}
	
	//获取当前任务的生命周期状态
	Cycle getCycle();
	
	//定义启动线程的方法，主要作用是为了屏蔽Thread的其他方法
	void start();
	
	//定义线程的打断方法，作用与start方法一样，也是为了屏蔽Thread的其他方法
	void interrupt();
}
