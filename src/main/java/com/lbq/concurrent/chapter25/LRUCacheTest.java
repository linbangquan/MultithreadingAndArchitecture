package com.lbq.concurrent.chapter25;
/**
 * 下面的程序是对LRUCache的基本测试：
 * @author 14378
 *
 */
public class LRUCacheTest {

	public static void main(String[] args) {
		LRUCache<String, Reference> cache = new LRUCache<>(5, key -> new Reference());
		cache.get("Alex");
		cache.get("Jack");
		cache.get("Gavin");
		cache.get("Dillon");
		cache.get("Leo");
		System.out.println(cache.toString());
		//上面的数据在缓存中的新旧程度为 Leo > Dillon > Gavin > Jack > Alex
		cache.get("Jenny");//Alex将会被踢出
		System.out.println(cache.toString());
	}

}
