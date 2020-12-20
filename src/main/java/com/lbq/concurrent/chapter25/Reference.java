package com.lbq.concurrent.chapter25;
/**
 * Two Phase Termination 设计模式（两相终端设计模式）
 * 25.1 什么是Tow Phase Termination 模式
 * 当一个线程正常结束，或者因被打断而结束，或者因出现异常而结束时，我们需要考虑如何同时释放线程中资源，
 * 比如文件句柄、socket套接字句柄、数据库连接等比较稀缺的资源。Two Phase Termination 设计模式如图25-1所示。
 * 
 * 如图25-1所示，我们使用“作业中”表示线程的执行状态，当希望结束这个线程时，发出线程结束请求，接下来线程不会立即结束，
 * 而是会执行相应的资源释放动作直到真正的结束，在终止处理状态时，线程虽然还运行，但是进行的是终止处理工作，
 * 因此终止处理又称为线程结束的第二个阶段，而受理终止要求则被称为线程结束的第一阶段。
 * 
 * 在进行线程两个阶段终结的时候需要考虑如下几个问题。
 * 1.第二阶段的终止保证安全性，比如涉及对共享资源的操作。
 * 2.要百分之百地确保线程结束，假设在第二个阶段出现了死循环、阻塞等异常导致无法结束。
 * 3.对资源的释放时间要控制在一个可控的范围之内。
 * 
 * 25.2 Two Phase Termination的示例
 * 25.2.1 线程停止的Two Phase Termination
 * Two Phase Termination 与其说是一个模式，还不如说是线程使用的一个技巧(best practice)。
 * 其主要针对的是当线程结束生命周期时，能有机会做一些资源释放的动作，我们曾在第24章中定义了用于处理客户端连接的Handler(ClientHandler)，
 * 在本节中我们继续使用他作为我们的示例：
 * @Override
 * public void run() {
 * 		try{
 * 			this.chat();
 * 		}catch(IOException e) {
 * 			e.printStackTrace();
 * 		}
 * }
 * private void chat() throws IOException {
 * 		BufferedReader bufferedReader = wrap2Reader(this.socket.getInputStream());
 * 		PrintStream printStream = wrap2Print(this.socket.getOutputStream());
 * 		String received;
 * 		while((received = bufferedReader.readLine()) != null){
 * 			System.out.println("client: %s-message: %s\n", clientIdentify, received);
 * 			if (received.equals("quit")) {
 * 				write2Client(printStream, "client will close");
 * 				socket.close();
 * 				break;
 * 			}
 * 			write2Client(printStream, "server:" + received);
 * 		}
 * }
 * 当客户端发送quit指令时，服务端会断开与客户端的连接“socket.close();”，如果客户端发送正常信息后发生异常，chat方法会抛出错误，
 * 那么此时该线程的任务执行也将结束，socket和thread一样都属于严重依赖操作系统资源的对象，各个操作系统中可供创建的线程数量有限，
 * 为了能够确保客户端即使异常关闭，我们也能够尽快地释放socket资源，two phase Termination 设计模式将是一种比较好的解决方案，
 * 示例代码如下：
 * @Override
 * public void run() {
 * 		try{
 * 			this.chat();
 * 		} catch (IOException e) {
 * 			e.printStackTrace();
 * 		} finally {
 *			//任务执行结束时，执行释放资源的工作
 * 			this.release();
 * 		}
 * }
 * private void release() {
 * 		try {
 * 			if (socket != null) {
 * 				socket.close();
 * 			}
 * 		} catch (Throwable e) {
 * 			//ignore
 * 		}
 * }
 * 当chat方法出现异常会被run方法捕获，在run方法中加入finally子句，用于执行客户端socket的主动关闭，
 * 为了使客户端的关闭不影响线程任务的结束，我们捕获了Throwable异常(在关闭客户端连接时出现的异常，
 * 视为不可恢复的异常，基本上没有针对该异常进行处理的必要)。
 * 上面的这个例子是针对线程(任务)关闭时进行资源释放的举例，那么当一个进程主动关闭或者异常关闭的时候，可不可以也进行两阶段的termination呢?
 * 
 * 25.2.2 进程关闭的Two Phase Termination
 * 如同线程关闭需要进行资源释放一样，不论是进程的主动关闭还是被动关闭(出现异常)都需要对持有的资源进行释放，我们在本书的第7章的7.2节中做了比较详细的讲解，
 * 通过为进程注入一个或者多个hook线程的方式进行两阶段的结束。
 * 
 * 25.3 知识扩展
 * 无论是File还是Socket等重量级的资源(严重依赖操作系统资源)，在进行释放时并不能百分之百的保证成功(可能是操作系统的原因)，
 * 如25.2.1节中对socket在第二阶段的关闭有可能会失败，然后socket的实例会被垃圾回收器回收，但是socket实例对应的底层系统资源或许并未释放。
 * 那么我们有什么办法可以再次尝试对socket进行资源释放操作呢？
 * 这看起来似乎是不可能的事情，但是JDK为我们借助于PhantomReference就可以很好地获取到是哪个对象即将被垃圾回收器清除，
 * 在被清除之前还可以尝试一次资源回收，尽最大的努力回收重量级的资源是一种非常好的编程体验。
 * 我们在知识扩展中不会单纯地介绍PhantomReference，会连同SoftReference、WeakReference等一起介绍，
 * 如图25-2所示，Reference有3个子类，分别是我们即将要介绍到的三种Reference类型，除了这三种引用类型以外，
 * 还有一种称为Strong Reference，我们平时使用最多的就是Strong Reference(强引用)，当一个对象被关键字new实例化出来的时候，它就是强引用。
 * 
 * 25.3.1 Strong Reference 及 LRUCache
 * 强引用(Strong Reference)是我们平时使用最多的一种对象引用，当一个对象被关键字new实例化出来的时候，JVM会在堆(heap)内存中开辟一片内存区域，
 * 用于存放与该实例对应的数据结构。JVM垃圾回收器线程会在达到GC条件的时候尝试回收(Full GC，Young GC)堆栈内存中的数据，
 * 强引用的特点是只要引用到ROOT根的路径可达，无论怎样的GC都不会将其释放，而是宁可出现JVM内存溢出。
 * 
 * Cache是一种用于提高系统性能，提高数据检索效率的机制，而LRU(Least recently used，最近最少使用)算法和Cache的结合是最常见的一种Cache的实现，
 * 在本节以及接下来的几节中，我们将通过LRUCache的方式来对比(Strong, Soft)这三种引用类型的不同并且总结他们各自的特点。
 * 
 * 首先我们定义一个占用内存1MB+的类Reference。
 * 当Reference对象被实例化之后，会在堆内存中创建1M+(+代表的是除了byte[] data之外Reference对象自身占有的少许内存，如果要求不那么精确，+可以被忽略)的内存空间，
 * finalize方法会在垃圾回收的标记阶段被调用(垃圾回收器在回收一个对象之前，首先会进行标记，标记过程则会调用该对象的finalize方法，所以千万不要认为该方法被调用之后，
 * 就代表对象已经被垃圾回收器回收，对象在finalize方法中是可以“自我救赎”的)。
 * 
 * @author 14378
 *
 */
public class Reference {

	private final byte[] data = new byte[2 << 19];
	
	@Override
	protected void finalize() throws Throwable {
		System.out.println("the reference will be GC.");
	}
}
