package com.lbq.concurrent.jmh;

import java.util.concurrent.TimeUnit;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

/**
 * 1.3.2 Warmup以及Measurement
 * Warmup以及Measurement的比较与1.2.1节中的ArrayListVSLinkedList的思路是一样的，主要是分批次地执行基准测试方法。
 * 在每个批次中，调用基准测试方法的次数受到两个因素影响，第一，要根据相关的参数进行设置，第二则是根据该方法具体的CPU时间而定，
 * 但是通常情况下，我们更多关注批次数量即可。
 * Warmup可直译为“预热”的意思，在JMH中，Warmup所做的就是在基准测试代码正式度量之前，先对其进行预热，
 * 使得代码的执行是经历了类的早期优化、JVM运行期编译、JIT优化之后的最终状态，从而能够获得代码真实的性能数据。
 * Measurement则是真正的度量操作，在每一轮的度量中，所有的度量数据会被纳入统计之中(预热数据不会纳入统计之中)。
 * 
 * 好了，下面我们来看看Warmup和Measurement的用法。
 * 1.设置全局的Warmup和Measurement
 * 设置全局的Warmup和Measurement执行批次，既可以通过构造Options时设置，也可以在对应的class上用相应的注解进行设置。
 * (1)构造Options时设置Warmup和Measurement的执行批次
 * 除了在构造Options时设置Warmup和Measurement，我们还可以通过注解的方式指定预热和度量各自的批次。
 * (2)使用@Measurement和@Warmup注解进行设置
 * 2.在基准测试方法上设置Warmup和Measurement
 * 我们除了可以设置全局的Warmup和Measurement参数之外，还可以在方法上设置对应基准测试方法的批次参数。
 * 
 * 运行基准测试我们会发现，test()基准方法执行了2个批次的预热和5个批次的度量，而test2()方法则执行了10个批次的度量和5个批次的预热操作，
 * 也就是说test2通过注解的方法覆盖了全局的设置。
 * Benchmark           Mode  Cnt  Score   Error  Units
 * JMHExample03.test   avgt    5  1.794 ± 0.044  ms/op
 * JMHExample03.test2  avgt   10  1.819 ± 0.039  ms/op
 * 
 * 注意：笔者经过测试发现，通过类注解的方式设置的全局Measurement和Warmup参数是可以被基准测试方法通过同样的方式覆盖的，
 * 但是通过Options进行的全局设置则无法被覆盖，也就是说，通过Options设置的参数会应用于所有的基准测试方法且无法被修改(当然不同的版本可能会存在差异)。
 * 
 * 3.Warmup和Measurement执行相关的输出
 * 每次执行微基准测试都会输出有关Warmup和Measurement的详细信息，笔者对下面的输出信息增加了中文的描述(下面选取了test()方法的微基准测试执行输出)：
 * @author 14378
 *
 */
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@State(Scope.Thread)
@Measurement(iterations = 5)//度量5个批次
@Warmup(iterations = 2)//预热2个批次
public class JMHExample03 {
	@Benchmark
	public void test() throws InterruptedException {
		TimeUnit.MILLISECONDS.sleep(1);
	}
	/**
	 * 预热5个批次
	 * 度量10个批次
	 * @throws InterruptedException
	 */
	@Measurement(iterations = 10)
	@Warmup(iterations = 5)
	@Benchmark
	public void test2() throws InterruptedException {
		TimeUnit.MILLISECONDS.sleep(1);
	}
	
	public static void main(String[] args) throws RunnerException {
		final Options opts = new OptionsBuilder()
				.include(JMHExample03.class.getSimpleName())
				.forks(1)
				//度量执行的批次为5，也就是说在这5个批次中，对基准方法的执行与调用将会纳入统计
				//.measurementIterations(5)
				//在真正的度量之前，首先会对代码进行3个批次的热身，使代码的运行达到JVM已经优化的效果
				//.warmupIterations(3)
				.build();
		new Runner(opts).run();
	}

}
//  使用的JMH版本是1.25
//# JMH version: 1.25
//  下面是JDK的版本信息
//# VM version: JDK 1.8.0_202, Java HotSpot(TM) 64-Bit Server VM, 25.202-b08
//  Java命令的目录
//# VM invoker: D:\DevInstall\Java\jdk1.8.0_202\jre\bin\java.exe
//  JVM运行时指定的参数
//# VM options: -Dfile.encoding=UTF-8
//  热身的批次为3，每一个批次都将会不断地调用test方法，每一个批次的执行时间均为10秒
//# Warmup: 3 iterations, 10 s each
// 	真正度量的批次为5，这5个批次的调用产生的性能数据才会真正地纳入统计中，同样每一个批次的度量执行的时间也为10秒
//# Measurement: 5 iterations, 10 s each
//  每一个批次的超时时间(在后文中还会继续解释)
//# Timeout: 10 min per iteration
//  执行基准测试的线程数量
//# Threads: 1 thread, will synchronize iterations
//  Benchmark的Mode，这里表明统计的是方法调用一次所耗费的单位时间
//# Benchmark mode: Average time, time/op
//  Benchmark方法的绝对路径
//# Benchmark: com.lbq.concurrent.jmh.JMHExample03.test
//  执行进度
//# Run progress: 0.00% complete, ETA 00:02:40
//# Fork: 1 of 1
//  执行三个批次的热身，第一批次调度方法的平均耗时为1.777毫秒，第二批次调度方法的平均耗时为1.826毫秒，第三批次调度方法的平均耗时为1.765毫秒
//# Warmup Iteration   1: 1.777 ms/op
//# Warmup Iteration   2: 1.826 ms/op
//# Warmup Iteration   3: 1.765 ms/op
// 执行五个批次的度量
//Iteration   1: 1.748 ms/op
//Iteration   2: 1.746 ms/op
//Iteration   3: 1.747 ms/op
//Iteration   4: 1.807 ms/op
//Iteration   5: 1.742 ms/op
//
// 最终的统计结果
//Result "com.lbq.concurrent.jmh.JMHExample03.test":
//  1.758 ±(99.9%) 0.105 ms/op [Average]
//  最小、平均、最大以及标准误差
//  (min, avg, max) = (1.742, 1.758, 1.807), stdev = 0.027
//  CI (99.9%): [1.653, 1.863] (assumes normal distribution)
//
//
//# JMH version: 1.25
//# VM version: JDK 1.8.0_202, Java HotSpot(TM) 64-Bit Server VM, 25.202-b08
//# VM invoker: D:\DevInstall\Java\jdk1.8.0_202\jre\bin\java.exe
//# VM options: -Dfile.encoding=UTF-8
//# Warmup: 3 iterations, 10 s each
//# Measurement: 5 iterations, 10 s each
//# Timeout: 10 min per iteration
//# Threads: 1 thread, will synchronize iterations
//# Benchmark mode: Average time, time/op
//# Benchmark: com.lbq.concurrent.jmh.JMHExample03.test2
//
//# Run progress: 50.00% complete, ETA 00:01:20
//# Fork: 1 of 1
//# Warmup Iteration   1: 1.761 ms/op
//# Warmup Iteration   2: 1.757 ms/op
//# Warmup Iteration   3: 1.775 ms/op
//Iteration   1: 1.802 ms/op
//Iteration   2: 1.827 ms/op
//Iteration   3: 1.818 ms/op
//Iteration   4: 1.817 ms/op
//Iteration   5: 1.834 ms/op
//
//
//Result "com.lbq.concurrent.jmh.JMHExample03.test2":
//  1.820 ±(99.9%) 0.047 ms/op [Average]
//  (min, avg, max) = (1.802, 1.820, 1.834), stdev = 0.012
//  CI (99.9%): [1.773, 1.866] (assumes normal distribution)
//
//
//# Run complete. Total time: 00:02:41
//
//REMEMBER: The numbers below are just data. To gain reusable insights, you need to follow up on
//why the numbers are the way they are. Use profilers (see -prof, -lprof), design factorial
//experiments, perform baseline and negative tests that provide experimental control, make sure
//the benchmarking environment is safe on JVM/OS/HW level, ask for reviews from the domain experts.
//Do not assume the numbers tell you what you want them to tell.
//
//Benchmark           Mode  Cnt  Score   Error  Units
//JMHExample03.test   avgt    5  1.758 ± 0.105  ms/op
//JMHExample03.test2  avgt    5  1.820 ± 0.047  ms/op
