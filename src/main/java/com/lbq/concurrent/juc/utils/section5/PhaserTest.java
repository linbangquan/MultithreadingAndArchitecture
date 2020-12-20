package com.lbq.concurrent.juc.utils.section5;

import java.util.concurrent.Phaser;
import java.util.concurrent.TimeUnit;

import org.junit.Test;

/**
 * 3.5.2 Phase(阶段)以及Phaser方法详解
 * 在Phaser中可以有多个Phase(阶段)，为了更好地每一个Phase进行管理和监控，Phaser为每一个Phase都提供了对应的编号，这一点与CyclicBarrier是不一样的，后者更加注重的是循环。
 * CyclicBarrier在所有的线程都到达barrier point之后，它才会重新开始，而Phaser则不然，只要某一个Phase的所有关联parties都arrive(到达)了，它就会从下一个Phase继续开始，
 * 除非Phaser本身被终止或者销毁，下面来看一下具体的图示(如图3-6所示)。
 * 
 * 在图3-6中，我们通过图示的方式为大家展示了随着Phaser的创建，每一个Phase(阶段)中所有关联的parties个任务到达之后，Phase编号的变化。
 * 
 * 1.register方法
 * register方法的主要作用是为Phaser新增一个未到达的分片，并且返回Phase(阶段)的编号，该编号与Phaser当前的Phase(阶段)编号数字是一样的，
 * 但是调用该方法时，有些时候会陷入阻塞之中。比如前一个Phase(阶段)在执行onAdvance方法时耗时较长，那么此时若有一个新的分片想要通过register方法
 * 加入到Phaser中就会陷入阻塞，如以下代码片段所示。
 * 
 * 2.bulkRegister方法
 * 该方法返回的Phase(阶段)编号同register方法，但是该方法允许注册零个或者一个以上的分片(Parties)到Phaser，其实无论是register方法还是bulkRegister方法，
 * 背后调用的都是doRegister方法，因此register方法的特点bulkRegister方法都会具备。
 * 
 * 3.arrive和arriveAndAwaitAdvance方法
 * arrive和arriveAndAwaitAdvance方法都是到达Phaser的下一个Phase(阶段)，前者不会等待其他分片(part)，后者则会等待所有未到达的分片(part)到达，
 * 除了这个区别以外，更重要的一个区别在于，arrive方法返回的Phase(阶段)编号为当前的Phase(阶段)编号，原理很好理解，因为它自身不清楚其他分片(part)是否到达
 * 也无须等待其他分片part到达下一个Phase(阶段)，因此返回Phaser当前的Phase阶段编号即可。但是在使用这两个方法的过程中会有一些让人疑惑的地方，
 * 我们看一下下面的例子。
 * 
 * 无论有没有其他的任务分片到达，调用arriveAndAwaitAdvance方法都会返回下一个Phase(阶段)的编号，这一点很好理解，不管怎样，当任务分片到达的肯定是下一个Phase(阶段)。
 * 
 * 4.arriveAndDeregister方法
 * 该方法的作用除了到达下一个Phase(阶段)之外，它还会将当前Phaser的分区(parties)数量减少一个。该方法也是Phaser灵活性的一个体现，
 * 即动态减少分区(parties)数量，同时该方法的返回值也是整数类型的数字，代表着当前Phase(阶段)的编号，如果Phase(阶段)的编号数字为负数，
 * 则表明当前的Phaser已经被销毁。
 * 
 * 5.awaitAdvance与awaitAdvanceInterruptibly方法
 * 在使用arrive以及arriveAndAwaitAdvance、arriveAndDeregister方法的时候，分片任务会到达某个Phase(阶段)，
 * 这几个方法在使用的过程中更多关注的是arrive(到达)，而不是关注Phase(阶段)编号，本节将要介绍的三个方法则不会关注arrive(到达)而是在于等待，
 * 它们等待某个Phaser关联的所有分片(part)是否已经到达某个指定的Phase(阶段)，同时需要注意的是，在使用本节的几个方法时，
 * 是不会影响Phaser内部分片(part)arrive以及unarrive的变化的，本节在介绍这些方法的同时会使用比较简单的代码片段诠释它们的使用方法和原理。
 * 1).awaitAdvance(int phase):该方法的主要作用是等待与Phaser关联的分片part都到达某个指定的Phase阶段编号，
 * 	如果有某个分片任务未到达，那么该方法会进入阻塞状态，这有点类似于CountDownLatch的await方法，虽然该方法是Phaser提供的方法，
 * 	但是它并不会参与对arrive与unarrive分片part的运算和维护，如果入参phase与当前Phaser的Phase阶段编号不一致，则会立即返回，
 * 	如果当前的Phaser已经被销毁，那么它同样不会工作，并且调用该方法的返回值为负数，下面通过代码的形式为大家演示。
 * 2).awaitAdvanceInterruptibly(int phase):该方法的作用与前一个方法一致，但是增加了可被中断的功能。
 * 3).awaitAdvanceInterruptibly(int phase, long timeout, TimeUnit unit):该方法同上，除了增加了可被中断的功能之外，
 * 	还具备了超时的功能，这就需要我们在调用的时候对超时时间进行设置了。
 * 
 * 本节从观察Phase阶段编号这个维度切入了解了大部分的Phaser API的使用以及用法，Phase阶段编号在Phaser中是比较重要的，稍不注意就会用错，
 * 而且它还不会向你提醒有错误发生，比如当Phaser的Phase阶段编号为负数的时候，代表着当前的Phaser已经被销毁，如果此时再用它进行访问控制，
 * 则可能不会达到你想要的效果，当然了，Phaser还提供了一些用于终止、查询类的方法，使用方法比较简单，由于篇幅关系，这里就不再赘述了，读者可以自行学习。
 * @author 14378
 *
 */
public class PhaserTest {

	/**
	 * 为了观察图3-6中Phase(阶段)编号的数值，我们通过程序的方式对其进行验证为了使得程序的执行步骤看起来更简单一些，我们直接使用非阻塞arrive方法，
	 * 运行上面的程序你会发现所有的assertion语句都能顺利通过。在上面的这段代码片段中，我们使用了getPhase方法获取Phaser当前的Phase(阶段)编号。
	 * 根据官方文档对该方法的描述：“getPhase()方法获取当前Phaser的Phase(阶段)编号，最大的Phase(阶段)为Integer.MAX_VALUE，
	 * 如果到达Integer.MAX_VALUE这个值，那么Phase编号将会又从0开始；当Phaser被终止的时候，调用getPhase()将返回负数，如果我们想要获得
	 * Phaser终止前的前一个Phase(阶段)编号，则可以通过getPhase() + Integer.MAX_VALUE进行计算和获取”。
	 * Phase编号在Phaser中比较重要，正因为如此，除了getPhase()方法会返回Phase(阶段)编号之外，在Phaser中，几乎所有方法的返回值都是Phase
	 * (阶段)编号，本节将为大家介绍其中一些Phase编号。
	 */
	@Test
	public void phase() {
		//定义Phaser指令初始parties为3
		final Phaser phaser = new Phaser(3);
		//新定义的Phaser，Phase(阶段)编号为0
		assert phaser.getPhase() == 0 : "current phase number is 0";
		//调用三次arrive方法，使得所有任务都arrive
		phaser.arrive();
		System.out.println(phaser.getPhase());
		phaser.arrive();
		System.out.println(phaser.getPhase());
		phaser.arrive();
		System.out.println(phaser.getPhase());
		//当parties个任务arrive之后，Phase(阶段)的编号就变为1
		assert phaser.getPhase() == 1 : "current phase number is 1";
		//新增一个parties，bulkRegister(1)方法等价于register()方法
		phaser.bulkRegister(1);
		//调用四次arrive方法，使得所有任务都arrive
		phaser.arrive();
		System.out.println(phaser.getPhase());
		phaser.arrive();
		System.out.println(phaser.getPhase());
		phaser.arrive();
		System.out.println(phaser.getPhase());
		phaser.arrive();
		System.out.println(phaser.getPhase());
		//当parties个任务arrive之后，Phase(阶段)编号就变为2
		assert phaser.getPhase() == 2 : "current phase number is 2";
	}
	
	/**
	 * 运行上面的代码，所有的assert语句应该都能通过。
	 */
	@Test
	public void register1() {
		//定义Phaser
		final Phaser phaser = new Phaser();
		//当前线程调用注册方法，返回当前Phaser的Phase(阶段)编号
		int phaseID = phaser.register();
		//由于当前Phaser首次定义且未到达下一个Phase(阶段)，因此register()会返回当前Phaser的Phase(阶段)编号
		assert phaseID == 0 : "The register phase ID is 0";
		assert phaser.getPhase() == 0 : "The phaser phase ID is 0";
		//调用arrive方法到达下一个Phase(阶段)，但是arrive方法会返回当前的Phase编号
		phaseID = phaser.arrive();
		assert phaseID == 0 : "The phaser arrived phase ID is 0";
		//再次调用注册方法，当前的parties(分片)数量为2，且处于新的Phase(阶段)
		phaseID = phaser.register();
		//phaseID则为第二个Phase(阶段)，即Phase number=1
		assert phaseID == phaser.getPhase() && phaseID == 1 : "current phase number is 1";
	}
	/**
	 * 我们在前面也说过，有些时候在调用register方法时会进入阻塞等待状态，原因是Phaser的onAdvance方法恰好被调用且耗时较长，
	 * 那么register方法就只有等待onAdvance方法完全结束后才能执行，下面通过一个代码片段验证一下。
	 * 
	 * 运行上面的程序，第二次的register调用会进入等待阻塞，其中耗时大概在58秒左右，加上前面休眠的2秒刚好是1分钟左右，
	 * 大家可以思考一下，为什么在调用register方法的时候会进入阻塞等待状态呢？其实原因很简单，我们都知道，
	 * 当Phaser的某个Phase(阶段)的所有分片任务全部都抵达时，会触发onAdvance方法的调用。
	 * 如果在onAdvance方法执行的过程中有新的线程要求加入Phaser，比较合理的做法就是Phaser做好收尾之后再接纳新的分片任务进来，否则就会出现矛盾。
	 * 比如新的分区进来返回了当前的Phase阶段编号，但是当前阶段在进行结束收尾操作时却没有新的分区任务什么事，所以等待是一个比较合理的设计，
	 * 但是有一点需要注意的是：如果有一个线程因为执行了Phaser的register方法而进入阻塞等待状态，尤其是该线程还无法被其他线程执行中断操作，
	 * 那么尽可能不要在onAdvance方法中写入过多复杂且耗时的逻辑。
	 * @throws InterruptedException
	 */
	@Test
	public void register2() throws InterruptedException {
		//定义只有一个parties(分片)的Phaser，并且重写onAdvance方法
		final Phaser phaser = new Phaser(1) {
			protected boolean onAdvance(int phase, int registeredParties) {
				try {
					//休眠1分钟的时间
					TimeUnit.MINUTES.sleep(1);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				return super.onAdvance(phase, registeredParties);
			}
		};
		//启动一个新的线程，该线程的逻辑非常简单，就是调用一下arrive方法使得onAdvance方法能够执行，因为当前Phase(阶段)的所有分片任务均已到达
		new Thread(phaser::arrive).start();
		//休眠，确保线程先启动
		TimeUnit.SECONDS.sleep(2);
		long startTimestamp = System.currentTimeMillis();
		//再次调用register方法，该方法将会陷入等待
		int phaseNumber = phaser.register();
		assert phaseNumber == 1 : "current phase number is 1";
		System.out.println("register ELT: " + (System.currentTimeMillis() - startTimestamp));
	}
	/**
	 * 下面的代码非常简单，由于Phaser设定了两个分片(parties)，在注释①处调用了arrive方法返回当前的Phase(阶段)编号，这比较符合arrive方法的语义，
	 * 毕竟当前的Phaser还处在Phase(阶段)0，因为还有其他的分片未到达。当程序运行到注释②处时，所有的分片(parties)均已到达，此时Phaser的Phase(阶段)应该为1，
	 * 但是我们的断言语句能够顺利通过，这一点看起来会有些矛盾。当然了，注释③更进一步验证了当前的Phase(阶段)处于1这个位置，因此这块需要注意一下。
	 */
	@Test
	public void arrive() {
		//定义只有两个分片(parties)的Phaser
		final Phaser phaser = new Phaser(2);
		//毫无疑问当前的Phase(阶段)编号为0
		assert phaser.getPhase() == 0 : "phaser current phase number is 0";
		//①第一次调用arrive方法返回当前Phaser的Phase(阶段)编号
		assert phaser.arrive() == 0 : "arrived phase number is 0";
		//②第二次调用arrive方法返回当前Phaser的Phase(阶段)编号还是0？
		assert phaser.arrive() == 0 : "arrived phase number is 0";
		//③当前的Phaser已经处于另外一个Phase(阶段)了
		assert phaser.getPhase() == 1 : "phaser current phase number is 1";
	}
	
	@Test
	public void arriveAndDeregister() {
		//定义只有两个分片(parties)的Phaser
		final Phaser phaser = new Phaser(2);
		//其中一个分区part到达，并且是Phaser注册的Parties数量减1
		assert phaser.arriveAndDeregister() == 0 : "arrived phase number is 0";
		//当前注册的分区(part)数量为1
		assert phaser.getRegisteredParties() == 1 : "now the register parties is 1";
		//当前的Phaser Phase(阶段)编号为0
		assert phaser.getPhase() == 0 : "phaser current phase number is 0";
		//调用arriveAndAwaitAdvance方法，该方法始终会返回下一个Phase(阶段)编号
		assert phaser.arriveAndAwaitAdvance() == 1 : "the next phase number is 1";
		//当前的Phaser Phase(阶段)编号为1
		assert phaser.getPhase() == 1 : "phaser current phase number is 1";
	}
	/**
	 * 传递了错误的phase编号awaitAdvance方法并不会抛出错误，因此在使用的时候一定要注意，如果某个phase所有的关联分片任务都没有到达，
	 * 那么此刻调用awaitAdvance方法的线程将会陷入阻塞状态，并且还会无法对其执行中断操作。
	 * @throws InterruptedException
	 */
	@Test
	public void awaitAdvance() throws InterruptedException {
		final Phaser phaser = new Phaser(1);
		Thread thread = new Thread(() -> {
			//断言当前的phase编号为0
			assert phaser.getPhase() == 0;
			//调用awaitAdvance方法，顺便将Phaser当前的phase编号传递进去
			int phaseNumber = phaser.awaitAdvance(phaser.getPhase());
//			int phaseNumber = phaser.awaitAdvance(phaser.getPhase() + 1);
			//只有当Phaser所关联的所有分片任务都arrive了，awaitAdvance方法才会退出阻塞，并且返回下一个phase编号
//			assert phaseNumber == 1;
			System.out.println(phaseNumber);
			System.out.println("B");
		});
		thread.start();
//		TimeUnit.MINUTES.sleep(1);
		TimeUnit.SECONDS.sleep(10);
		System.out.println("A");
		//1分钟后仅有的一个分片任务arrive
		assert phaser.arriveAndAwaitAdvance() == 1;
		assert phaser.getPhase() == 1;
	}
	
	@Test
	public void awaitAdvance2() throws InterruptedException {
		final Phaser phaser = new Phaser();
		System.out.println("phaser.getArrivedParties():" + phaser.getArrivedParties());
		System.out.println("phaser.getUnarrivedParties():" + phaser.getUnarrivedParties());
		System.out.println("phaser.register():" + phaser.register());
		System.out.println("================================================");
		System.out.println("phaser.getArrivedParties():" + phaser.getArrivedParties());
		System.out.println("phaser.getUnarrivedParties():" + phaser.getUnarrivedParties());
		System.out.println("phaser.register():" + phaser.register());
		System.out.println("================================================");
		System.out.println("phaser.getArrivedParties():" + phaser.getArrivedParties());
		System.out.println("phaser.getUnarrivedParties():" + phaser.getUnarrivedParties());
		System.out.println("phaser.register():" + phaser.register());
		System.out.println("================================================");
		System.out.println("phaser.getArrivedParties():" + phaser.getArrivedParties());
		System.out.println("phaser.getUnarrivedParties():" + phaser.getUnarrivedParties());
		System.out.println("phaser.register():" + phaser.register());
		System.out.println("================================================");
		System.out.println("phaser.getArrivedParties():" + phaser.getArrivedParties());
		System.out.println("phaser.getUnarrivedParties():" + phaser.getUnarrivedParties());
		System.out.println("phaser.arrive():" + phaser.arriveAndDeregister());
		System.out.println("================================================");
		System.out.println("phaser.getArrivedParties():" + phaser.getArrivedParties());
		System.out.println("phaser.getUnarrivedParties():" + phaser.getUnarrivedParties());
		System.out.println("phaser.arrive():" + phaser.arriveAndDeregister());
		System.out.println("================================================");
		System.out.println("phaser.getArrivedParties():" + phaser.getArrivedParties());
		System.out.println("phaser.getUnarrivedParties():" + phaser.getUnarrivedParties());
		System.out.println("phaser.arrive():" + phaser.arriveAndDeregister());
		System.out.println("================================================");
		System.out.println("phaser.getArrivedParties():" + phaser.getArrivedParties());
		System.out.println("phaser.getUnarrivedParties():" + phaser.getUnarrivedParties());
		System.out.println("phaser.arrive():" + phaser.arriveAndDeregister());
		System.out.println("================================================");
		System.out.println("phaser.getArrivedParties():" + phaser.getArrivedParties());
		System.out.println("phaser.getUnarrivedParties():" + phaser.getUnarrivedParties());
		System.out.println("phaser.arrive():" + phaser.arriveAndDeregister());
		System.out.println("================================================");
		phaser.awaitAdvance(1);
		System.out.println("================================================");
		System.out.println("================================================");
		System.out.println("================================================");
	}
}
