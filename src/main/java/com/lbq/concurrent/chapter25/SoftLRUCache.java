package com.lbq.concurrent.chapter25;

import java.lang.ref.SoftReference;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
/**
 * 25.3.2 Soft Reference 及 SoftLRUCache
 * 当JVM Detect(探测)到内存即将溢出，它会尝试GC soft类型的reference，
 * 我们参考LRUCache编写了一个SoftLRUCache。
 * 
 * 在SoftLRUCache中，cache的Value被声明成SoftReference<V>，在put一个键值对的时候，需要将V实例封装成new SoftReference<>(V)，这也是SoftReference引用的构造方式；
 * 在get数据的时候首先从cache中获取SoftReference，然后再通过get方法得到put进去的Value值。
 * @author 14378
 *
 * @param <K>
 * @param <V>
 */
public class SoftLRUCache<K, V> {

	private final LinkedList<K> keyList = new LinkedList<>();
	//Value采用SoftReference进行修饰
	private final Map<K, SoftReference<V>> cache = new HashMap<>();
	
	private final int capacity;
	
	private final CacheLoader<K, V> cacheLoader;
	
	public SoftLRUCache(int capacity, CacheLoader<K, V> cacheLoader) {
		this.capacity = capacity;
		this.cacheLoader = cacheLoader;
	}
	
	public void put(K key, V value) {
		if(keyList.size() >= capacity) {
			K eldestKey = keyList.removeFirst();//eldest data
			cache.remove(eldestKey);
		}
		if(keyList.contains(key)) {
			keyList.remove(key);
		}
		keyList.addLast(key);
		//保存SoftReference
		cache.put(key, new SoftReference<>(value));
	}
	
	public V get(K key) {
		V value;
		boolean success = keyList.remove(key);
		if(!success) {
			value = cacheLoader.load(key);
			this.put(key, value);
		}else {
			value = cache.get(key).get();
			keyList.addLast(key);
		}
		return value;
	}
	@Override
	public String toString() {
		return keyList.toString();
	}
}
