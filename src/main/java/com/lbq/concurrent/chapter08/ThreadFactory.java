package com.lbq.concurrent.chapter08;
/**
 * ThreadFactory提供了创建线程的接口，以便于个性化地制定Thread，比如Thread应该被加到哪个Group中，优先级、线程名字以及是否为守护线程等。
 * 创建线程的工厂，其中createThread(Runnable runnable)用于创建线程。
 * @author 14378
 *
 */
@FunctionalInterface
public interface ThreadFactory {
	Thread createThread(Runnable runnable);
}
