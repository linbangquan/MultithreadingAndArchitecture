package com.lbq.concurrent.chapter14;
/**
 * 单例设计模式是GoF23种设计模式中最常用的设计模式之一，无论是第三方类库，还是我们在日常的开发中，几乎都可以看到单例的影子，
 * 单例设计模式提供了一种在多线程情况下保证实例唯一性的解决方案，单例设计模式的实现虽然非常简单，但是实现方式却多种多样，
 * 在本章中笔者列举了7种单例设计模式的实现方法，为了比较这7种实现方式的优劣，我们从三个维度对其进行评估：
 * 线程安全、高性能、懒加载。
 * @author 14378
 *
 */
public class Main {

	public static void main(String[] args) {
		System.out.println("Main main()...");
//		System.out.println(HungerSingleton.x);
//		HungerSingleton singleton1 = HungerSingleton.getInstance();
//		HungerSingleton singleton2 = HungerSingleton.getInstance();
//		System.out.println(singleton1 == singleton2);


//		System.out.println(LazySingleton.x);
//		LazySingleton singleton3 = LazySingleton.getInstance();
//		LazySingleton singleton4 = LazySingleton.getInstance();
//		System.out.println(singleton3==singleton4);
		

//		SynchronizedLazySingleton singleton5 = SynchronizedLazySingleton.getInstance();
//		SynchronizedLazySingleton singleton6 = SynchronizedLazySingleton.getInstance();
//		System.out.println(singleton5==singleton6);
//		
//
//		DoubleCheckSingleton singleton7 = DoubleCheckSingleton.getInstance();
//		DoubleCheckSingleton singleton8 = DoubleCheckSingleton.getInstance();
//		System.out.println(singleton7 == singleton8);
//		
//		VolatileDoubleCheckSingleton singleton9 = VolatileDoubleCheckSingleton.getInstance();
//		VolatileDoubleCheckSingleton singleton10 = VolatileDoubleCheckSingleton.getInstance();
//		System.out.println(singleton9 == singleton10);
		
//		System.out.println(HolderSingleton.x);
//		HolderSingleton singleton11 = HolderSingleton.getInstance();
//		HolderSingleton singleton12 = HolderSingleton.getInstance();
//		System.out.println(singleton11 == singleton12);
		
//		EnumSingleton.method();
//		EnumSingleton singleton13 = EnumSingleton.INSTANCE;
//		EnumSingleton singleton14 = EnumSingleton.INSTANCE;
//		System.out.println(singleton13 == singleton14);
//
		EnumHolderSingleton.method();
//		EnumHolderSingleton singleton15 = EnumHolderSingleton.getInstance();
//		EnumHolderSingleton singleton16 = EnumHolderSingleton.getInstance();
//		System.out.println(singleton15 == singleton16);
	}

}
