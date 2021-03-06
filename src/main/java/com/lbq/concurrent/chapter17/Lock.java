package com.lbq.concurrent.chapter17;
/**
 * 读写锁分离设计模式
 * 
 * 17.1 场景描述
 * 在多线程的情况下访问共享资源，需要对资源进行同步操作以防止数据不一致的情况发生，通常我们可以使用synchronized关键字或者显式锁，
 * 比如我们在5.4节中定义的BooleanLock进行资源的同步操作，当然也可以使用jdk1.5以后的显示锁Lock。
 * 对资源的访问一般包括两种类型的动作————读和写(更新、删除、增加等资源会发生变化的动作)，如果多个线程在某个时刻都在进行资源的读操作，
 * 虽然有资源的竞争，但是这种竞争不足以引起数据不一致的情况发生，那么这个时候直接采用排他的方式加锁，就显示的有些简单粗暴了。
 * 表17-1将两个线程对资源的访问动作进行了枚举，除了多线程在同一时间都进行读操作时不会引起冲突之外，其余的情况都会导致访问的冲突，需要对资源进行同步处理。
 * 		线程 		读		写
 * 		读		不冲突	冲突
 * 		写		冲突		冲突
 * 如果对某个资源读的操作明显多过于写的操作，那么多线程读时并不加锁，很明显对程序性能的提升会有很大的帮助。
 * 在本章中，我们将使用之前掌握的知识点，实现一个读写分离的锁。
 * 
 * 17.2 读写分离程序设计
 * Lock接口定义了锁的基本操作，加锁和解锁，显式锁的操作强烈建议与try finally 语句块一起使用，加锁和解锁说明如下。
 * 1.lock():当前线程尝试获得锁的拥有权，在此期间有可能进入阻塞。
 * 2.unlock():释放锁，其主要目的就是为了减少reader或者writer的数量。
 * 
 * @author 14378
 *
 */
public interface Lock {

	//获取显示锁，没有获得锁的线程将被阻塞
	void lock() throws InterruptedException;
	//释放获取的锁
	void unlock();
}
