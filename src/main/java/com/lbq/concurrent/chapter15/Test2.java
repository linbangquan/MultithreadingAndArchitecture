package com.lbq.concurrent.chapter15;

import java.util.concurrent.TimeUnit;
/**
 * 下面这段程序代码定义了一个需要返回值的ObservableThread，并且通过重写EmptyLifecycle的onFinsh方法输出最终的返回结果。
 * 
 * 15.3.2 关键点总结
 * 1.在接口Observable中定义与Thread同样的方法用于屏蔽Thread的其他API,在使用的过程中使用Observable声明ObservableThread的类型，
 * 如果使用者还想知道更多的关于Thread的API，只需要在Observable接口中增加即可。
 * 2.将ObservableThread中的run方法修饰为final，或者将ObservableThread类修饰为final，防止子类继承重写，导致整个声明周期的监控失效，
 * 我们都知道，任务的逻辑执行单元是存在于run方法之中的，而在ObservableThread中我们摒弃了这一点，让它专门监控业务执行单元的生命周期，
 * 而将真正的业务逻辑执行单元交给了一个可返回计算结果的接口Task。
 * 3.ObservableThread本身的run方法充当了事件源的发起者，而TaskLifecycle则扮演了事件回调的响应者。
 * 
 * @author 14378
 *
 */
public class Test2 {

	public static void main(String[] args) {
		final TaskLifecycle<String> lifecycle = new TaskLifecycle.EmptyLifecycle<String>() {
			public void onFinish(Thread thread, String result) {
				System.out.println("The result is " + result);
			}
		};
		
		Observable observableThread = new ObservableThread<>(lifecycle, () -> {
			try {
				TimeUnit.SECONDS.sleep(10);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			System.out.println(" finished done.");
			return "Hello Observer";
		});
		observableThread.start();
	}

}
