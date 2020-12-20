package com.lbq.concurrent.chapter14;
/**
 * 使用枚举的方式实现单例模式是《Effective Java》作者力推的方式，在很多优秀的开源代码中经常可以看到使用枚举方式实现单例模式的（身影），
 * 枚举类型不允许被继承，同样是线程安全的且只能被实例化一次，但是枚举类型不能够懒加载，对Singleton主动使用，比如调用其中的静态方法则INSTANCE会立即得到实例化。
 * @author 14378
 *
 */
//枚举类型本身是final的，不允许被继承
public enum EnumSingleton {

	INSTANCE;
	//实例变量
	private byte[] data = new byte[1024];
	
	EnumSingleton() {
		System.out.println("instance will be initialized immediately");
	}
	
	public static void method() {
		//调用该方法则会主动使用EnumSingleton，INSTANCE将会被实例化
	}
	
	public static EnumSingleton getInstance() {
		return INSTANCE;
	}
}
