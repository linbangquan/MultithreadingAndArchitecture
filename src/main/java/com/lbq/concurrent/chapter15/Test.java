package com.lbq.concurrent.chapter15;

import java.util.concurrent.TimeUnit;
/**
 * 本章总结
 * 
 * 15.3.1 测试运行
 * 关于ObservableThread我们已经完成实现，在这里进行简单测试，其中需要读者重点关注的是，ObservableThread是否保持了与Thread相同的使用习惯，
 * 其次读者可以通过实现TaskLifecycle监听感兴趣的事件，比如获取最终的计算结果等，代码如下：
 * 这段程序与你平时使用Thread并没有太大的区别，只不过ObservableThread是一个泛型类，
 * 我们将其定义为void类型，表示不关心返回值，默认的EmptyLifecycle同样表示不关心生命周期的每一个阶段。
 * @author 14378
 *
 */
public class Test {

	public static void main(String[] args) {
		Observable observableThread = new ObservableThread<>(() -> {
			try {
				TimeUnit.SECONDS.sleep(10);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			System.out.println(" finished done.");
			return null;
		});
		observableThread.start();
	}

}
