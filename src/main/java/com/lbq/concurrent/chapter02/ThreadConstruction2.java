package com.lbq.concurrent.chapter02;
/**
 * 在Thread的构造函数中，可发现有一个特殊的参数stackSize，这个参数的作用是什么呢？它的值对线程有什么影响呢？
 * 
 * 一般情况下，创建线程的时候不会手动指定栈内存的地址空间字节数组，统一通过xss参数进行设置即可。
 * stackSize越大则代表着正在线程内方法调用递归的深度就越深，stackSize越小则代表着创建的线程数量越多。
 * 当然了这个参数对平台的依赖性比较高，比如不同的操作系统、不同的硬件。
 * java -Xmx512m -Xms64m ThreadConstruction 1
 * 
 * JVM内存结构
 * JVM在执行Java程序的时候会把对应的物理内存划分成不同的内存区域，每一个区域都存放着不同的数据，
 * 也有不同的创建于销毁时机，有些分区会在JVM启动的时候就创建，有些则在运行时才创建，比如虚拟机栈。
 * 
 * 1.程序计数器
 * 无论任何语言，其实最终都是需要有操作系统通过控制总线向cpu发送机器指令，Java也不例外，
 * 程序计数器在jvm中所起的作用就是用于存放当前线程接下来将要执行的字节码指令、分支、循环、跳转、异常处理等信息。
 * 在任何时候，一个处理器只执行其中一个线程中的指令，为了能够在cpu时间片轮转切换上下文之后顺利回到正确的执行位置，
 * 每条线程都需要具有一个独立的程序计数器，各个线程之间互相不影响，因此jvm将此块内存区域设计成了线程私有的。
 * 
 * 2.Java虚拟机栈
 * 这里需要重点介绍内存，因为其与线程紧密关联，与程序计数器内存相类似，Java虚拟机栈也是线程私有的，它的生命周期与线程相同，
 * 是在jvm运行时所创建的，在线程中，方法在执行的时候都会创建一个名为栈帧(stack frame)的数据结构，主要用于存放局部变量表、
 * 操作栈、动态链接、方法出口等信息，方法的调用对应着栈帧在虚拟机栈中的压栈和弹栈过程。
 * 
 * 每个线程在创建的时候，jvm都会为其创建对应的虚拟机栈，虚拟机栈的大小可以通过-xss来配置，方法的调用是栈帧被压入和弹出的过程。，
 * 同等的虚拟机栈如果局部变量表等占用内存越小则可被压入的栈帧就会越多，反之则可被压入的栈帧就会越小，一般将栈帧内存的大小称为宽度，
 * 而栈帧的数量则称为虚拟机栈的深度。
 * 
 * 3.本地方法栈
 * Java中提供了调用本地方法的接口（Java Native Interface），也就是C/C++程序，在线程的执行过程中，经常会碰到调用JNI方法的情况，
 * 比如网络通信、文件操作的底层，甚至是String的intern等都是JNI方法，jvm为本地方法所划分的内存区域便是本地方法栈，
 * 这块内存区域其自由度非常高，完全靠不同的jvm厂商来实现，Java虚拟机规范并未给出强制的规定，同样它也是线程私有的内存区域。
 * 
 * 4.堆内存
 * 堆内存是jvm中最大的一块内存区域，被所有的线程所共享，Java在运行期间创建的所有对象几乎都存放在该内存区域，该内存区域也是垃圾回收器重点照顾的区域，因此有些时候堆内存被称为“GC堆”。
 * 堆内存一般会被细分为新生代和老年代，更细致的划分为Eden区、From Survivor区和To Survivor区。
 * 
 * 5.方法区
 * 方法区也是被多个线程所共享的内存区域，它主要用于存储已经被虚拟机加载的类信息、常量、静态变量、即时编译器（JIT）编译后的代码等数据，
 * 虽然在Java虚拟机规范中，将方法区划分为堆内存的一个逻辑分区，但是它还是经常被称为“非堆”，有时候也称为“持久代”，主要是站在垃圾回收器的角度进行划分，
 * 但是这种叫法比较欠妥，在HosSpotJVM中，方法区还会被细划分为持久代和代码缓存区，代码缓存区主要用于存储编译后的本地代码（和硬件相关）以及JIT(just in time)编译器生成的代码，
 * 当然不同的jvm会有不同的实现。
 * 
 * 6.Java 8 元空间
 * 上述内容大致介绍了jvm内存划分，在jdk1.8版本以前的内存大概都是这样划分的，但是自jdk1.8版本起，jvm的内存区域发生了一些改变，
 * 实际上是持久代内存被彻底删除，取而代之的是元空间，图2-6与图2-7是使用分别使用不同版本的jstat命令对比jvm的GC内存分布。
 * 通过对比会发现在jdk1.7版本中存在持久代内存区域，而在jdk1.8版本中，该内存区域被Meta Space取而代之了，元空间同样是堆内存的一部分，
 * jvm为每个类加载器分配一块内存列表，进行线性分配，块的大小取决于类加载器的类型，sun/反射/代理对应的类加载器块会小一些，
 * 之前的版本会单独卸载回收某个类，而现在则是GC过程中发现某个类加载器已具备回收的条件，则会将整个类加载器相关的元空间全部回收，
 * 这样就可以减少内存碎片，节省GC扫描和压缩的时间。
 * @author 14378
 *
 */
public class ThreadConstruction2 {

	public static void main(String[] args) {
		if(args.length < 1) {
			System.out.println("Please enter the stack size.");
			System.exit(1);
		}

		ThreadGroup group = new ThreadGroup("TestGroup");
		
		Runnable runnable = new Runnable() {

			final int MAX = Integer.MAX_VALUE;
			@Override
			public void run() {
				int i = 0;
				recurse(i);
			}
			private void recurse(int i) {
				System.out.println(i);
				if(i < MAX) {
					recurse(i + 1);
				}
			}
			
		};
		Thread thread = new Thread(group, runnable, "Test", Integer.parseInt(args[0]));
		thread.start();
	}

}
