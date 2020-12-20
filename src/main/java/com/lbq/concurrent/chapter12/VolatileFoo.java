package com.lbq.concurrent.chapter12;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
/**
 * volatile关键字的介绍
 * 正如我们前面所说的，volatile是一个非常重要的关键字，虽然看起来很简单，但是想要彻底弄清楚，volatile的来龙去脉还是需要具备java内存模型、cpu缓存模型等知识的，
 * 在本章中，我们将首先介绍一个最能说明volatile特征的例子，然后对Java内存模型、cpu缓存模型等知识进行展开讲解，这样对理解volatile关键字是非常有帮助的。
 * 
 * 12.1 初识volatile关键字
 * VolatileFoo的这段程序分别启动了两个线程，一个线程负责对变量进行修改，一个线程负责对变量进行输出，根据本书第一部分的知识讲解，该变量就是共享资源(数据)，
 * 那么在多线程操作的情况下，很有可能会引起数据不一致等线程安全的问题。
 * 
 * 1.通过控制台的输出信息我们不难发现，Reader线程压根就没有感知到init_value的变化而进入了死循环，这是为什么呢？
 * 
 * 2.我们将init_value的定义做一次小小的调整，代码如下：static volatile int init_value = 0;
 * 这里为init_value变量增加volatile关键字的修饰。再次运行修改后的程序，你会发现Reader线程会感知到init_value变量的变化，并且在条件不满足时退出运行。
 * 
 * 为什么会出现这样的情况呢，其实这一切都是volatile关键字所起的作用，在本章接下来的内容以及第13章“深入volatile关键字”中都会介绍和volatile有关的内容。
 * 注意：volatile关键字只能修饰类变量和实例变量，对于方法参数、局部变量以及实例常量，类常量都不能进行修饰。
 * 
 * 12.2 机器硬件CPU
 * 	在计算机中，所有的运算操作都是由cpu的寄存器来完成的，cpu指令的执行过程需要涉及数据的读取和写入操作，cpu所能访问的所有数据只能是计算机的主存(通常是RAM)，
 * 虽然cpu的发展频率不断地得到提升，但受制于制造工艺以及成本等的限制，计算机的内存反倒在访问速度上并没有多大的突破，因此cpu的处理速度和内存的访问速度之间的差距越来越大，
 * 通常这种差距可以达到上千倍，极端情况下甚至会在上万倍以上。
 * 
 * 12.2.1 cpu cache模型
 * 由于两边速度严重的不对等，通过传统FSB直连内存的访问方式很明显会导致cpu资源受到大量的限制，降低cpu整体的吞吐量，于是就有了在cpu和主内存之间增加缓存的设计，
 * 现在缓存的数量都可以增加到3级了，最靠近cpu的缓存称为L1，然后依次是L2，L3和主内存，cpu缓存模型如图12-1所示。
 * 由于程序指令和程序数据的行为和热点分布差异很大，因此L1 Cache又被划分成了L1i(i是instruction的首字母)和L1d(d是data的首字母)这两种有各自专门用途的缓存，
 * cpu cache又是由很多个cache Line构成的，cache Line可以认为是cpu cache中的最小缓存单位，目前主流cpu cache的cache Line大小都是64字节。
 * 
 * cache的出现是为了解决cpu直接访问内存效率低下问题的，程序在运行的过程中，会将运算所需要的数据从主存复制一份到cpu cache中，
 * 这样cpu进行计算时就可以直接对cpu Cache中的数据进行读取和写入，当运算结束之后，再将cpu cache中的最新数据刷新到主内存当中，
 * cpu通过直接访问cache的方式替代直接访问主存的方式极大地提高了cpu的吞吐能力，有了cpu Cache之后，整体的cpu和主内存之间交互的架构大致如图12-3所示。
 * 
 * 12.2.2 cpu缓存一致性问题
 * 由于缓存的出现，极大地提高了cpu的吞吐能力，但是同时也引入了缓存不一致的问题，比如i++这个操作，在程序的运行过程中，首先需要将主内存中的数据复制一份存放到cpu cache中，
 * 那么cpu寄存器在进行数值计算的时候就直接到cache中读取和写入，当整个过程运算结束之后再将cache中的数据刷新到主存当中，具体过程如下。
 * 	1.读取主内存的i到cpu cache中。
 * 	2.对i进行加一操作。
 * 	3.将结果写回到CPU Cache中。
 * 	4.将数据刷新到主内存中。
 * i++在单线程的情况下不会出现任何问题，但是在多线程的情况下就会有问题，每个线程都有自己的工作内存(本地内存，对应于CPU中的Cache)，
 * 变量i会在多个线程的本地内存中都存在一个副本。如果同时有两个线程执行i++操作，假设i的初始值为0，每一个线程都从主内存中获取i的值存入CPU Cache中，
 * 然后经过计算再写入主内存中，很有可能i在经过了两次自增之后结果还是1，这就是典型的缓存不一致性问题。
 * 
 * 为了解决缓存不一致性问题，通常主流的解决方法有如下两种。
 * 	1.通过总线加锁的方式。
 * 	2.通过缓存一致性协议。
 * 第一种方式常见于早期的CPU当中，而且是一种悲观的实现方式，CPU和其他组件的通信都是通过总线(数据总线、控制总线、地址总线)来进行的，
 * 如果采用总线加锁的方式，则会阻止其他CPU对其他组件的访问，从而使得只有一个CPU(抢到总线锁)能够访问这个变量的内存。
 * 这种方式效率低下，所以就有了第二种通过缓存一致性协议的方式来解决不一致的问题。
 * 
 * 在缓存一致性协议中最为出名的是Intel的MESI协议，MESI协议保证了每一个缓存中使用的共享变量副本都是一致的，它的大致思想是，
 * 当CPU在操作Cache中的数据时，如果发现该变量是一个共享变量，也就是说在其他CPU Cache中也存在一个副本，那么进行如下操作：
 * 	1.读取操作，不做任何处理，只是将Cache中的数据读取到寄存器。
 * 	2.写入操作，发出信号通知其他CPU将该变量的Cache line置为无效状态，其他CPU在进行该变量读取的时候不得不到主内存中再次获取。
 * 
 * 12.3 Java内存模型
 * Java的内存模型（Java Memory Mode，JMM）指定了Java虚拟机如何与计算机的主存（RAM）进行工作，如图12-5所示，
 * 理解Java内存模型对于编写行为正确的并发程序是非常重要的。在JDK1.5以前的版本中，Java内存模型存在着一定的缺陷，
 * 在JDK1.5的时候，JDK官方对Java内存模型重新进行了修订，JDK1.8及最新的JDK版本都沿用了jdk1.5修订的内存模型。
 * Java的内存模型决定了一个线程对共享变量的写入何时对其他线程可见，Java内存模型定义了线程和主内存之间的抽象关系，具体如下。
 * 	1.共享变量存储于主内存之中，每个线程都可以访问。
 * 	2.每个线程都有私有的工作内存或者称为本地内存。
 * 	3.工作内存只存储该线程对共享变量的副本。
 * 	4.线程不能直接操作主内存，只有先操作了工作内存之后才能写入主内存。
 * 	5.工作内存和Java内存模型一样也是一个抽象的概念，它其实并不存在，它涵盖了缓存、寄存器、编译器优化以及硬件等。
 * 
 * 假设主内存的共享变量X为0，线程1和线程2分别拥有共享变量X的副本，假设线程1此时将工作内存中的X修饰为1，同时刷新到主内存中，
 * 当线程2想要去使用副本X的时候，就会发现该变量已经失效了，必须到主内存中再次获取然后存入自己的工作内存中，
 * 这一点和CPU与CPU Cache之间的关系非常类似。
 * Java的内存模型是一个抽象的概念，其与计算机硬件的结构并不完全一样，比如计算机物理内存不会存在栈内存和堆内存的划分，
 * 无论是堆内存还是虚拟机的栈内存都会对应到物理的主内存，当然也有一部分堆栈内存的数据有可能会存入CPU cache寄存器中。
 * 
 * 当同一个数据被分别存储到了计算机的各个内存区域时，势必会导致多个线程在各自的工作区域中看到的数据有可能是不一样的，
 * 在Java语言中如何保证不同线程对某个共享变量的可见性？以及又该如何解释12.1节中增加了volatile修饰之后不一样的运行效果？
 * 第13章将会为读者详细讲解。
 * 
 * 12.4 本章总结
 * 本章通过一个非常具有代表性的例子引入了volatile关键字，通过该示例不难发现，volatile关键字具备了synchronized关键字的部分语义，
 * 但是相比于synchronized关键字，要理解volatile关键字会困难很多，需要了解机器硬件CPU的架构以及Java的内存模型等其他知识。
 * volatile关键字在jdk1.5版本以后的并发包(JUC)中使用得非常广泛，因此彻底理解和掌握volatile关键字的来龙去脉对进一步的提升大有裨益。
 * 
 * @author 14378
 *
 */
public class VolatileFoo {
	//init_value的最大值
	final static int MAX = 5;
	//init_value的初始值
	static int init_value = 0;
//	static volatile int init_value = 0;
	
	static Map<String, Integer> map = new HashMap<>();
	
	public static void main(String[] args) {
		//启动一个Reader线程，当发现local_value和init_value不同时，则输出init_value被修改的信息
		new Thread(() -> {
			int localValue = init_value;
			while(localValue < MAX) {
				//System.out.println(map);
				System.out.println("init_value="+init_value);
				if(init_value != localValue) {
					System.out.printf("The init_value is updated to [%d]\n", init_value);
					System.out.println(map);
					//对localValue进行重新赋值
					localValue = init_value;
				}
			}
		}, "Reader") .start();
		//启动Updater线程，主要用于对init_value的修改，当local_value >= 5的时候则退出生命周期
		new Thread(() -> {
			int localValue = init_value;
			while(localValue < MAX) {
				//修改init_value
				System.out.printf("The init_value will be changed to [%d]\n", ++localValue);
				init_value = localValue;
				Map<String, Integer> temp = new HashMap<>(map);
				temp.put(String.valueOf(init_value), init_value);
				map = temp;
				try {
					//短暂休眠，目的是为了使Reader线程能够来得及输出变化内容
					TimeUnit.SECONDS.sleep(2);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}				
			}
		}, "Updater").start();
	}
}
