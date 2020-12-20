package com.lbq.concurrent.chapter01;

import java.sql.ResultSet;
/**
 * Runnable接口非常简单，只定义了一个无参数无返回值的run方法，具体代码如下：
 * package java.lang;
 * public interface Runnable{
 * 		void run();
 * }
 * 在很多软文以及一些书籍中，经常会提到，创建线程有两种方式，第一种是构造一个Thread，第二种是实现Runnable接口，这种说法是错误的，最起码是不严谨的，
 * 在jdk中代表线程的就只有Thread这个类，我们在前面分析过，线程的执行单元就是run方法，你可以通过继承Thread然后重写run方法实现自己的业务逻辑，
 * 也可以实现Runnable接口实现自己的业务逻辑，所以说创建线程有两种方式，一种是创建一个Thread，一种是实现Runnable接口，这种说法是不严谨的。
 * 准确地讲，创建线程只有一种方式，那就是构造Thread类，
 * 而实现线程的执行单元则有两种方式，第一种是重写Thread的run方法，第二种是实现Runnable接口的run方法，并且将Runnable实例用作构造器Thread的参数。
 * 
 * 无论是Runnable的run方法，还是Thread类本身的run方法(事实上Thread类也实现了Runnable接口)都是想将线程的控制本身和业务逻辑的运行分离开来，达到职责分明、功能单一的原则。
 * 这一点与策略设计模式很相似。
 * 
 * RowHandler接口只负责对从数据库中查询出来的结果进行操作，至于最终返回成什么样的数据结构，那就需要你自己去实现，类似于Runnable接口。
 * @author 14378
 *
 * @param <T>
 */
public interface RowHandler<T> {

	T handle(ResultSet rs);
}
