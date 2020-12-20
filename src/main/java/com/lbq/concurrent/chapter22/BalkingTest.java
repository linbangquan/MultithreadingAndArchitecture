package com.lbq.concurrent.chapter22;
/**
 * Balking设计模式
 * 22.1 什么是Balking设计
 * 多个线程监控某个共享变量，A线程监控到共享变量发生变化之后即将触发某个动作，但是此时发现有另外一个线程B已经针对该变量的变化开始了行动，
 * 因此A便放弃了准备开始的工作，我们把这样的线程间交互称为Balking(犹豫)设计模式。其实这样的场景在生活中很常见，比如你去饭店吃饭，
 * 吃到途中想要再点一个小菜，于是你举手示意服务员，其中一个服务员看到了你举手正准备走过来的时候，发现距离你比较近的服务员已经准备要受理你的请求于是中途放弃了。
 * 
 * 再比如，我们再用word编写文档的时候，每次的文字编辑都代表着文档的状态发生了改变，除了我们可以使用CTRL+s快捷键手动保存以外，
 * word软件本身也会定期触发自动保存，如果word自动保存文档的线程在准备执行保存动作的时候，恰巧我们进行了主动保存，
 * 那么自动保存文档的线程将会放弃此次的保存动作。
 * 看了以上两个例子的说明，想必大家已经清楚了Balking设计模式要解决的问题了吧，简短的说就是某个线程因为发现其他线程正在进行相同的工作而放弃即将开始的任务，
 * 本章中，我们将通过模拟word文档自动保存与手动保存的功能讲解Balking模式的设计与应用。
 * 
 * 22.3 本章总结
 * Balking模式在日常的开发中很常见，比如在系统资源的加载或者某些数据的初始化时，在整个系统中生命周期中资源可能只被加载一次，
 * 我们就可以采用balking模式加以解决，代码如下：
 * public synchronized Map<String, Resource> load() {
 * 		//balking
 * 		if(loaded) {
 * 			return resourceMap;
 * 		}else {
 * 			//do the resource load task.
 * 			//...
 * 			this.loaded = true;
 * 			return resourceMap;
 * 		}
 * }
 * @author 14378
 *
 */
public class BalkingTest {

	public static void main(String[] args) {
		new DocumentEditThread("D:\\", "balking.txt").start();
	}

}
