package com.lbq.concurrent.chapter25;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
/**
 * LRU其实是数据冷热治理的一种思想，不经常使用的数据被称为冷数据，经常使用的则称为热数据，
 * 对冷数据分配很少的资源或者提前释放，可以帮助我们节省更多的内存资源，LRUCache的实现方式有很多种，
 * 很多人喜欢借助于LinkedHashMap去实现，我们在这里使用双向链表+hash表的方式来实现
 * (其实LinkedHashMap自身也是双向链表和hash表的方式实现的)。
 * 
 * 在下述代码中：
 * 1.在LRUCache中，keyList主要负责对key的顺序进行管理，而Cache则主要用于存储真正的数据(K-V)。
 * 2.CacheLoader主要用于进行数据的获取。
 * 3.put方法可将Value缓存至Cache中，如果当前Cache的容量超过了指定容量大小，则会将最先保存至Cache中的数据丢弃掉。
 * 4.get方法根据key从Cache中获取数据，如果数据存在则先从keyList中删除，然后再插入到队尾，
 * 否则调用CacheLoader的load方法进行加载。
 * @author 14378
 *
 * @param <K>
 * @param <V>
 */
public class LRUCache<K, V> {
	//用于记录key值的顺序
	private final LinkedList<K> keyList = new LinkedList<>();
	//用于存放数据
	private final Map<K, V> cache = new HashMap<>();
	//cache的最大容量
	private final int capacity;
	//cacheLoader接口提供了一种加载数据的方式
	private final CacheLoader<K, V> cacheLoader;
	
	public LRUCache(int capacity, CacheLoader<K, V> cacheLoader) {
		this.capacity = capacity;
		this.cacheLoader = cacheLoader;
	}
	
	public void put(K key, V value) {
		//当元素数量超过容量时，将最老的数据清除
		if(keyList.size() >= capacity) {
			K eldestKey = keyList.removeFirst();//eldest data
			cache.remove(eldestKey);
		}
		//如果数据已经不存在，则从key的队列中删除
		if(keyList.contains(key)) {
			keyList.remove(key);
		}
		//将key存放至队尾
		keyList.addLast(key);
		cache.put(key, value);
	}
	
	public V get(K key) {
		V value;
		//先将key从keyList中删除
		boolean success = keyList.remove(key);
		if(!success) {//如果删除失败则表明该数据不存在
			//通过cacheLoader对数据进行加载
			value = cacheLoader.load(key);
			//调用put方法cache数据
			this.put(key, value);
		}else {
			//如果删除成功，则从cache中返回数据，并且将key再次放到队尾
			value = cache.get(key);
			keyList.addLast(key);
		}
		return value;
	}
	
	@Override
	public String toString() {
		return this.keyList.toString();
	}
}
