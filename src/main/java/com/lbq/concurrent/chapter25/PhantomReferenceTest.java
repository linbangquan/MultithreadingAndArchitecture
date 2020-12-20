package com.lbq.concurrent.chapter25;

import java.lang.ref.PhantomReference;
import java.lang.ref.ReferenceQueue;
/**
 * 25.3.4 Phantom Reference
 * 这是JDK官网对Phantom reference(幻影引用)的说明，与softReference和WeakReference相比较Phantom Reference有如下几点不同之处：
 * 1.Phantom Reference 必须和ReferenceQueue配合使用。
 * 2.Phantom Reference 的get方法返回的始终是null。
 * 3.当垃圾回收器决定回收Phantom Reference对象的时候会将其插入关联的ReferenceQueue中。
 * 4.使用Phantom Reference进行清理动作要比Object的finalize方法更加灵活。
 * @author 14378
 *
 */
public class PhantomReferenceTest {

	public static void main(String[] args) throws InterruptedException {
		ReferenceQueue<Reference> queue = new ReferenceQueue<>();
		PhantomReference<Reference> reference = new PhantomReference<>(new Reference(), queue);
		System.out.println(reference.get());//始终返回null
		System.gc();
		java.lang.ref.Reference<? extends Reference> gcedRef = queue.remove();
		System.out.println(gcedRef);
	}

}
