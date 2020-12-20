package com.lbq.concurrent.chapter18;

import java.util.Arrays;
import java.util.List;
/**
 * 不可变对象设计模式
 * 18.1 线程安全性
 * 所谓共享的资源，是指在多个线程同时对其进行访问的情况下，各线程都会使其发生变化，而线程安全性的主要目的就在于在受控的并发访问中防止数据发生变化。
 * 除了使用synchronized关键字同步对资源的写操作之外，还可以在线程之间不共享资源的状态，甚至将资源的状态设置为不可变。在本章中，我们将讨论
 * 如何设计不可变对象，这样就可以不用依赖于synchronized关键字的约束。
 * 
 * 18.2 不可变对象的设计
 * 无论是synchronized关键字还是显式锁，都会牺牲系统的性能，不可变对象的设计理念在这几年变得越来越受宠，
 * 其中Actor模型以及函数式编程语言Clojure等都是依赖于不可变对象的设计达到lock free(无锁)的。
 * Java核心类库中提供了大量的不可变对象范例，其中java.lang.String的每个方法都没有同步修饰，可是其在多线程访问的情况下是安全的，
 * Java 8 中通过Stream修饰的ArrayList在函数式方法并行访问的情况下也是线程安全的，所谓不可变对象是没有机会去修改它，
 * 每一次的修改都会导致一个新的对象产生，比如String s1 = " Hello"; s1 = s1 + " world"两者相加会产生新的字符串。
 * 
 * 有些非线程安全可变对象被不可变机制加以处理之后，照样也具备不可变性，比如ArrayList生成的stream在多线程的情况下也是线程安全的，
 * 同样是因为其具备不可变性的结果。
 * list虽然是在并行的环境下运行的，但是在stream的每一个操作中都是一个全新的List，根本不会影响到最原始的list，
 * 这样也是符合不可变对象的最基本思想。
 * @author 14378
 *
 */
public class ArrayListStream {

	public static void main(String[] args) {
		//定义一个list并且使用Arrays的方式进行初始化
		List<String> list = Arrays.asList("Java", "Thread", "Concurrency", "Scala", "Clojure");
		//获取并行的stream，然后通过map函数对list中的数据进行加工，最后输出
		list.parallelStream().map(String::toUpperCase).forEach(System.out::println);
		list.forEach(System.out::println);
	}

}
