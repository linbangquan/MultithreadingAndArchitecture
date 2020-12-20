package com.lbq.concurrent.chapter19;

import java.util.concurrent.TimeUnit;
/**
 * Future设计模式
 * 19.1 先给你一张凭据
 * 假设有一个任务需要执行比较长的时间，通常需要等待任务执行结束或者出错才能返回结果，在此期间调用者只能陷入阻塞苦苦等待，
 * 对此，Future设计模式提供了一种凭据式的解决方案。在我们日常生活中，关于凭据的使用非常多见，
 * 比如你去某西服手工作坊想订做一身合体修身的西服，西服的制作过程比较漫长，少则一个礼拜，多则一个月，你不可能一直待在原地等待，
 * 一般来说作坊会为你开一个凭据，此凭据就是Future，在接下来的任意日子里你可以凭借此凭据到作坊获取西服。
 * 在本章中，我们将通过程序的方式实现Future设计模式，让读者体会这种设计的好处。
 * 自jdk1.5起，Java提供了比较强大的Future接口，在jdk1.8时更是引入了CompletableFuture，其结合函数式接口可实现更强大的功能，
 * 由于本书不涉及讨论并发包的知识点，读者可自行查阅。
 * 
 * 19.3 Future的使用以及技巧总结
 * Future直译是“未来”的意思，主要是将一些耗时的操作交给一个线程去执行，从而达到异步的目的，
 * 提交线程在提交任务和获得计算结果的过程中可以进行其他的任务执行，而不至于傻傻等待结果的返回。
 * 
 * 我们提供了两种任务的提交(无返回值和有返回值)方式，在这里分别对其进行测试。
 * 
 * 至此，Future模式设计已讲解完毕，虽然我们提交任务时不会进入任何阻塞，但是当调用者需要获取结果的时候，还是有可能陷入阻塞直到任务完成，
 * 其实这个问题不仅在我们设计的Future中有，在jdk1.5时期，也存在，直到jdk1.8引入了CompletabelFuture才得到了完美的增强，
 * 那么在此期间各种开源项目中都出了各自的解决方案，比如Google的Guava Toolkit就提供了ListenableFuture用于支持任务完成时回调的方式。
 * 
 * 19.4 增强FutureService使其支持回调
 * 使用任务完成时回调的机制可以让调用者不再进行显式地通过get的方式获得数据而导致进入阻塞，可在提交任务的时候将回调接口一并注入，
 * 在这里对FutureService接口稍作修改，修改后的submit方法，增加了一个Callback参数，主要用来接受并处理任务的计算结果，
 * 当提交的任务执行完成之后，会将结果传递给Callback接口进行进一步的执行，这样在提交任务之后不再会因为通过get方法获得结果而陷入阻塞。
 * 
 * System.out::println是一个Lambda表达式的静态推导，其作用就是实现call方法，通过升级后的程序你会发现，
 * 我们再也不需要通过get的方法来获得结果了，当然你也可以继续使用get方法获得最终的计算结果。
 * 
 * 19.5 本章总结
 * 
 * 当某个任务运行需要较长的时间时，调用线程在提交任务之后的徒劳等待对CPU资源来说是一种浪费，在等待的这段时间里，完全可以进行其他任务的执行，
 * 这种场景完全符合Future设计模式的应用，虽然我们实现了一个简单的Future设计，但是仍旧存在诸多缺陷，读者在阅读完本章内容之后可以对其进行再次增强。
 * 1.将提交的任务交给线程池运行，比如我们在第八章自定义的线程池。
 * 2.Get方法没有超时功能，如果获取一个计算结果在规定的时间内没有返回，可以抛出异常通知调用线程。
 * 3.Future未提供Cancel功能，当任务提交之后还可以对其进行取消。
 * 4.任务运行时出错未提供回调方式。
 * 
 * @author 14378
 *
 */
public class Test {

	public static void main(String[] args) throws InterruptedException {
		//定义不需要返回值的FutureService
		FutureService<Void, Void> service = FutureService.newService();
		//submit方法为立即返回的方法
		Future<?> future = service.submit(() -> {
			try {
				TimeUnit.SECONDS.sleep(10);
			}catch(InterruptedException e) {
				e.printStackTrace();
			}
			System.out.println("I am finish done.");
		});
		//get方法会使当前线程进入阻塞
		future.get();
		/*************************************************************************/
		//定义有返回值的FutureService
		FutureService<String, Integer> service2 = FutureService.newService();
		//submit方法为立即返回的方法
		Future<Integer> future2 = service2.submit((input) -> {
			try {
				TimeUnit.SECONDS.sleep(10);
			}catch(InterruptedException e) {
				e.printStackTrace();
			}
			return input.length();
		}, "Hello");
		//get方法会使当前线程进入阻塞，最终会返回计算的结果
		System.out.println(future2.get());
		/*************************************************************************/
		FutureService<String, Integer> service3 = FutureService.newService();
		service3.submit((input) -> {
			try {
				TimeUnit.SECONDS.sleep(10);
			}catch(InterruptedException e) {
				e.printStackTrace();
			}
			return input.length();
		}, "Hello", System.out::println);
	}

}
