package com.lbq.concurrent.chapter25;
/**
 * CacheLoader比较简单，是一种标准的函数式接口。
 * @author 14378
 *
 * @param <K>
 * @param <V>
 */
@FunctionalInterface
public interface CacheLoader<K, V> {
	//定义加载数据的方法
	V load(K k);
}
