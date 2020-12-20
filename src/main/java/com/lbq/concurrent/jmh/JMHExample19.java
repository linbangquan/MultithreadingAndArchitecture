package com.lbq.concurrent.jmh;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Group;
import org.openjdk.jmh.annotations.GroupThreads;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Warmup;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import org.openjdk.jmh.runner.options.TimeValue;
/**
 * 2.Interrupts Benchmark
 * 前面的例子中为大家演示了多线程情况下同时对AtomicInteger执行读写操作的情况，
 * 虽然基准测试能够顺利地运行，但是有些时候我们想要执行某些容器的读写操作时可能会引起阻塞，
 * 这种阻塞并不是容器无法保证线程安全问题引起的，而是由于JMH框架的机制引起的，
 * 下面我们来看一下代码示例。
 * 在JMHExample19.java中我们针对BlockingQueue同时进行读(take)和写(put)的操作，
 * 但是很遗憾，在某些情况下(或许是第一次运行时)程序会出现长时间的阻塞，
 * 对于每一批次的Measurement，当然也包括Warmup中，put和take方法都会同时被多线程执行。
 * 想象一下，假设put方法最先执行结束，take方法无法再次从blocking queue中获取元素的时候将会一直阻塞下去，
 * 同样，take方法最先执行结束后，put方法在放满10个元素后再也无法存入新的元素，进而进入了阻塞状态，
 * 这两种情况都会等到每一次iteration(批次)超时(默认是10分钟)后才能继续往下执行。
 * 
 * 难道我们就没有办法测试高并发容器在线程挂起(详见《Java高并发编程详解：多线程与架构设计》一书中的第20章“Guarded Suspension设计模式”)时的性能了吗？
 * 事实上，JMH的设计者们早就为我们想好了对应的解决方案，我们可以通过设置Options的timeout来强制让每一个批次的度量超时，
 * 超时的基准测试数据将不会被纳入统计之中，这也是JMH的另外一个严谨之外，下面对JMHExample19.java进行简单的修改。
 * 
 * 好了，我们再来执行增加了超时设置的基准测试，观察输出结果会发现当出现阻塞时，jmh最多等待指定的超时时间会继续执行而不是像之前那样陷入长时间的阻塞。
 * @author 14378
 *
 */
@BenchmarkMode(Mode.AverageTime)
@Fork(1)
@Warmup(iterations = 5)
@Measurement(iterations = 5)
@OutputTimeUnit(TimeUnit.MICROSECONDS)
@State(Scope.Group)
public class JMHExample19 {

	private BlockingQueue<Integer> queue;
	
	private final static int VALUE = Integer.MAX_VALUE;
	
	@Setup
	public void init() {
		this.queue = new ArrayBlockingQueue<>(10);
	}
	
	@GroupThreads(5)
	@Group("blockingQueue")
	@Benchmark
	public void put() throws InterruptedException {
		this.queue.put(VALUE);
	}
	
	@GroupThreads(5)
	@Group("blockingQueue")
	@Benchmark
	public int take() throws InterruptedException {
		return this.queue.take();
	}
	
	public static void main(String[] args) throws RunnerException {
		final Options opts = new OptionsBuilder()
				.include(JMHExample19.class.getSimpleName())
				//将每个批次的超时时间设置为10秒
				.timeout(TimeValue.seconds(10))
				.build();
		new Runner(opts).run();

	}

}

//# JMH version: 1.19
//# VM version: JDK 1.8.0_202, VM 25.202-b08
//# VM invoker: D:\DevInstall\Java\jdk1.8.0_202\jre\bin\java.exe
//# VM options: -Dfile.encoding=UTF-8
//# Warmup: 5 iterations, 1 s each
//# Measurement: 5 iterations, 1 s each
//# Timeout: 10 min per iteration
//# Threads: 10 threads (1 group; 5x "put", 5x "take" in each group), will synchronize iterations
//# Benchmark mode: Average time, time/op
//# Benchmark: com.lbq.concurrent.jmh.JMHExample19.blockingQueue
//
//# Run progress: 0.00% complete, ETA 00:00:10
//# Fork: 1 of 1
//# Warmup Iteration   1: 46522.210 ±(99.9%) 182128.488 us/op
//# Warmup Iteration   2: 78.058 ±(99.9%) 84.717 us/op
//# Warmup Iteration   3: 56911.829 ±(99.9%) 268283.226 us/op
//# Warmup Iteration   4: 14425191.521 ±(99.9%) 44812519.223 us/op
//# Warmup Iteration   5: 12943590.781 ±(99.9%) 41051351.708 us/op
//Iteration   1: 136838.294 ±(99.9%) 652338.452 us/op
//                 put:  670.730 ±(99.9%) 5166.240 us/op
//                 take: 273005.858 ±(99.9%) 2350283.726 us/op
//
//Iteration   2: 99.237 ±(99.9%) 364.627 us/op
//                 put:  189.487 ±(99.9%) 1279.721 us/op
//                 take: 8.988 ±(99.9%) 30.716 us/op
//
//Iteration   3: 75.839 ±(99.9%) 193.305 us/op
//                 put:  58.801 ±(99.9%) 225.218 us/op
//                 take: 92.877 ±(99.9%) 695.638 us/op
//
//Iteration   4: 14635.899 ±(99.9%) 69515.156 us/op
//                 put:  29191.877 ±(99.9%) 250355.586 us/op
//                 take: 79.921 ±(99.9%) 419.582 us/op
//
//Iteration   5: 155990.390 ±(99.9%) 745727.176 us/op
//                 put:  311974.365 ±(99.9%) 2686062.868 us/op
//                 take: 6.414 ±(99.9%) 32.242 us/op
//
//
//
//Result "com.lbq.concurrent.jmh.JMHExample19.blockingQueue":
//  61527.932 ±(99.9%) 300396.589 us/op [Average]
//  (min, avg, max) = (75.839, 61527.932, 155990.390), stdev = 78012.040
//  CI (99.9%): [≈ 0, 361924.520] (assumes normal distribution)
//
//Secondary result "com.lbq.concurrent.jmh.JMHExample19.blockingQueue:put":
//  68417.052 ±(99.9%) 526483.840 us/op [Average]
//  (min, avg, max) = (58.801, 68417.052, 311974.365), stdev = 136726.181
//  CI (99.9%): [≈ 0, 594900.892] (assumes normal distribution)
//
//Secondary result "com.lbq.concurrent.jmh.JMHExample19.blockingQueue:take":
//  54638.812 ±(99.9%) 470051.556 us/op [Average]
//  (min, avg, max) = (6.414, 54638.812, 273005.858), stdev = 122070.896
//  CI (99.9%): [≈ 0, 524690.368] (assumes normal distribution)
//
//
//# Run complete. Total time: 00:11:08
//
//Benchmark                        Mode  Cnt      Score        Error  Units
//JMHExample19.blockingQueue       avgt    5  61527.932 ± 300396.589  us/op
//JMHExample19.blockingQueue:put   avgt    5  68417.052 ± 526483.840  us/op
//JMHExample19.blockingQueue:take  avgt    5  54638.812 ± 470051.556  us/op
//------------------------------------------------------------------------------------------------------------------------------------

//# JMH version: 1.19
//# VM version: JDK 1.8.0_202, VM 25.202-b08
//# VM invoker: D:\DevInstall\Java\jdk1.8.0_202\jre\bin\java.exe
//# VM options: -Dfile.encoding=UTF-8
//# Warmup: 5 iterations, 1 s each
//# Measurement: 5 iterations, 1 s each
//  经过修改后的超时时间为10秒
//# Timeout: 10 s per iteration
//# Threads: 10 threads (1 group; 5x "put", 5x "take" in each group), will synchronize iterations
//# Benchmark mode: Average time, time/op
//# Benchmark: com.lbq.concurrent.jmh.JMHExample19.blockingQueue
//
//# Run progress: 0.00% complete, ETA 00:00:10
//# Fork: 1 of 1
//# Warmup Iteration   1: 9505504.893 ±(99.9%) 45013654.325 us/op
//# Warmup Iteration   2: 818081.322 ±(99.9%) 3868480.030 us/op
//# Warmup Iteration   3: 1065.124 ±(99.9%) 4956.251 us/op
//# Warmup Iteration   4: 84.297 ±(99.9%) 199.113 us/op
//# Warmup Iteration   5: 12.293 ±(99.9%) 27.828 us/op
//Iteration   1: 5998331.276 ±(99.9%) 28676462.881 us/op
//                 put:  15.703 ±(99.9%) 71.782 us/op
//                 take: 11996646.850 ±(99.9%) 103290723.890 us/op
//
//Iteration   2: 15.726 ±(99.9%) 30.393 us/op
//                 put:  20.072 ±(99.9%) 105.307 us/op
//                 take: 11.381 ±(99.9%) 41.143 us/op
//
//Iteration   3: 102672.309 ±(99.9%) 342828.072 us/op
//                 put:  23.523 ±(99.9%) 170.178 us/op
//                 take: 205321.096 ±(99.9%) 1151032.236 us/op
//
//Iteration   4: 125689.207 ±(99.9%) 600847.720 us/op
//                 put:  12.748 ±(99.9%) 47.926 us/op
//                 take: 251365.665 ±(99.9%) 2164222.626 us/op
//
//Iteration   5: 160221.024 ±(99.9%) 765851.986 us/op
//                 put:  10.012 ±(99.9%) 34.313 us/op
//                 take: 320432.036 ±(99.9%) 2758513.458 us/op
//
//
//
//Result "com.lbq.concurrent.jmh.JMHExample19.blockingQueue":
//  1277385.909 ±(99.9%) 10164790.957 us/op [Average]
//  (min, avg, max) = (15.726, 1277385.909, 5998331.276), stdev = 2639763.932
//  CI (99.9%): [≈ 0, 11442176.865] (assumes normal distribution)
//
//Secondary result "com.lbq.concurrent.jmh.JMHExample19.blockingQueue:put":
//  16.411 ±(99.9%) 20.990 us/op [Average]
//  (min, avg, max) = (10.012, 16.411, 23.523), stdev = 5.451
//  CI (99.9%): [≈ 0, 37.401] (assumes normal distribution)
//
//Secondary result "com.lbq.concurrent.jmh.JMHExample19.blockingQueue:take":
//  2554755.406 ±(99.9%) 20329583.739 us/op [Average]
//  (min, avg, max) = (11.381, 2554755.406, 11996646.850), stdev = 5279528.338
//  CI (99.9%): [≈ 0, 22884339.145] (assumes normal distribution)
//
//
//# Run complete. Total time: 00:09:03
//
//Benchmark                        Mode  Cnt        Score          Error  Units
//JMHExample19.blockingQueue       avgt    5  1277385.909 ± 10164790.957  us/op
//JMHExample19.blockingQueue:put   avgt    5       16.411 ±       20.990  us/op
//JMHExample19.blockingQueue:take  avgt    5  2554755.406 ± 20329583.739  us/op