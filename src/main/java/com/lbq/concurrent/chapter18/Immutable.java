package com.lbq.concurrent.chapter18;

import java.util.List;
/**
 * 18.3 本章总结
 * 设计一个不可变的类共享资源需要具备不可破坏性，比如使用final修饰，另外针对共享资源操作的方法是不允许被重写的，
 * 以防止由于继承而带来的安全性问题，但是单凭这两点与不足以保证一个类是不可变的，比如下面的类用final修饰，
 * 并且其中的list也是final修饰的，只允许在构造时创建：
 * Immutable类被final修饰因此不允许更改，同样list只能在构造时被指定，但是该类同样是可变的(mutable)，
 * 因为getList方法返回的list是可被其他线程修改的，如果想要使其真正的不可变，则需要在返回list的时候增加
 * 不可修改的约束Collections.unmodifiableList(this.list)或者克隆一个全新的list返回。
 * @author 14378
 *
 */
public final class Immutable {
	
	private final List<String> list;
	
	public Immutable(List<String> list) {
		this.list = list;
	}
	
	public List<String> getList(){
		return this.list;
	}
}
