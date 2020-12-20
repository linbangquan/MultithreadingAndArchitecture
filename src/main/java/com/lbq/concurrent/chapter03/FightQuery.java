package com.lbq.concurrent.chapter03;

import java.util.List;
/**
 * FightQuery提供了一个返回方法，学到这里大家应该注意到了，不管是Thread的run方法，还是Runnable接口，都是void返回类型，
 * 如果你想通过某个线程的运行得到结果，就需要自己定义一个返回的接口。
 * @author 14378
 *
 */
public interface FightQuery {

	List<String> get();
}
