package com.lbq.concurrent.chapter14;
/**
 * 但是也可以对其进行改造，增加懒加载的特性，类似于Holder的方式。
 * @author 14378
 *
 */
public final class EnumHolderSingleton {
	//实例变量
	private byte[] data = new byte[1024];
	
	private EnumHolderSingleton() {
		System.out.println("new EnumHolderSingleton");		
	}
	public static void method() {
		System.out.println("method()...");
	}
	//使用枚举充当holder
	private enum EnumHolder {
		INSTANCE;
		private EnumHolderSingleton instance;
		
		EnumHolder(){
			System.out.println("new EnumHolder");
			this.instance = new EnumHolderSingleton();
		}
		
		private EnumHolderSingleton getSingleton() {
			return instance;
		}
	}
	
	public static EnumHolderSingleton getInstance() {
		return EnumHolder.INSTANCE.getSingleton();
	}
}
