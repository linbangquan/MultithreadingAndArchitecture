package com.lbq.concurrent.jmh;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
/**
 * 1.3.7 JMH的测试套件(Fixture)
 * 在使用Junit编写单元测试的时候，我们可以使用的套件有@Before、@After、@BeforeClass、@AfterClass等。
 * 在JMH中，有没有哪些套件方法可以支持对基准测试方法的初始化以及资源回收呢？
 * 答案是有的，本节将为大家介绍一下JMH有哪些测试套件以及具体的用法。
 * 
 * 1.Setup以及TearDown
 * JMH提供了两个注解@Setup和@TestDown用于套件测试，其中@Setup会在每一个基准测试方法执行前被调用，通常用于资源的初始化，
 * 而@TestDown则会在基准测试方法被执行之后被调用，通常可用于资源的回收清理工作，下面我们来看看具体的示例。
 * 
 * 运行上面的基准测试程序，我们会发现measureRight基准测试方法能够正确地执行，但是measureWrong却会失败。
 * 
 * 2.Level
 * 1.3.7节使用Setup和TearDown时，在默认情况下，Setup和TearDown会在一个基准方法的所有批次执行前后分别执行，
 * 如果需要在每一个批次或者每一次基准方法调用执行的前后执行对应的套件方法，则需要对@Setup和@TearDown进行简单的配置。
 * 1.Trial：Setup和TearDown默认的配置，该套件方法会在每一个基准测试方法的所有批次执行的前后被执行。（总共只执行一次）
 * 2.Iteration：由于我们可以设置Warmup和Measurement，因此每一个基准测试方法都会被执行若干个批次，如果想要在每一个基准测试批次执行的前后调用套件方法，可以将Level设置为Iteration。
 * 3.Invocation：将Level设置为Invocation意味着在每一个批次的度量过程中，每一次对基准方法的调用前后都会执行套件方法。
 * 
 * 需要注意的是，套件方法的 执行也会产生CPU时间的消耗，但是JMH并不会将这部分时间纳入基准方法的统计之中，这一点更进一步地说明了JMH的严谨之处。
 * 
 * @author 14378
 *
 */
@BenchmarkMode(Mode.AverageTime)
@Fork(1)
@Warmup(iterations = 5)
@Measurement(iterations = 5)
@OutputTimeUnit(TimeUnit.MICROSECONDS)
@State(Scope.Thread)
public class JMHExample11 {

	//定义了一个List<String>，但是没有对其进行初始化
	private List<String> list;
	//将方法标记为@Setup，执行初始化操作
	//@Setup(Level.Trial)//总共只执行一次
	//@Setup(Level.Iteration)//每个批次都执行
	@Setup(Level.Invocation)
	public void setUp() {
		System.out.println("setUp");
		this.list = new ArrayList<>();
	}
	
	//简单地调用list的add方法
	@Benchmark
	public void measureRight() throws InterruptedException {
		TimeUnit.SECONDS.sleep(1);
		this.list.add("Test");
	}
	
	//该方法什么都不做
	@Benchmark
	public void measureWrong() {
		//do nothing
	}
	
	//将方法标记为@TearDown，运行资源回收甚至断言的操作
	//@TearDown
	//@TearDown(Level.Iteration)
	@TearDown(Level.Invocation)
	public void testDown() {
		System.out.println("testDown");
		assert this.list.size() > 0 : "The list elements must greater than zero";
	}
	
	public static void main(String[] args) throws RunnerException {
		final Options opts = new OptionsBuilder()
				.include(JMHExample11.class.getSimpleName())
				.jvmArgs("-ea")//激活断言，enable assertion的意思
				.build();
		new Runner(opts).run();

	}

}

//# JMH version: 1.19
//# VM version: JDK 1.8.0_202, VM 25.202-b08
//# VM invoker: D:\DevInstall\Java\jdk1.8.0_202\jre\bin\java.exe
//# VM options: -ea
//# Warmup: 5 iterations, 1 s each
//# Measurement: 5 iterations, 1 s each
//# Timeout: 10 min per iteration
//# Threads: 1 thread, will synchronize iterations
//# Benchmark mode: Average time, time/op
//# Benchmark: com.lbq.concurrent.jmh.JMHExample11.measureRight
//
//# Run progress: 0.00% complete, ETA 00:00:20
//# Fork: 1 of 1
//# Warmup Iteration   1: 1000300.400 us/op
//# Warmup Iteration   2: 1000242.450 us/op
//# Warmup Iteration   3: 1000206.500 us/op
//# Warmup Iteration   4: 1000142.600 us/op
//# Warmup Iteration   5: 1000151.800 us/op
//Iteration   1: 1000774.900 us/op
//Iteration   2: 1000057.050 us/op
//Iteration   3: 1000209.350 us/op
//Iteration   4: 1000807.900 us/op
//Iteration   5: 1000044.950 us/op
//
//
//Result "com.lbq.concurrent.jmh.JMHExample11.measureRight":
//  1000378.830 ±(99.9%) 1472.228 us/op [Average]
//  (min, avg, max) = (1000044.950, 1000378.830, 1000807.900), stdev = 382.333
//  CI (99.9%): [998906.602, 1001851.058] (assumes normal distribution)
//
//
//# JMH version: 1.19
//# VM version: JDK 1.8.0_202, VM 25.202-b08
//# VM invoker: D:\DevInstall\Java\jdk1.8.0_202\jre\bin\java.exe
//# VM options: -ea
//# Warmup: 5 iterations, 1 s each
//# Measurement: 5 iterations, 1 s each
//# Timeout: 10 min per iteration
//# Threads: 1 thread, will synchronize iterations
//# Benchmark mode: Average time, time/op
//# Benchmark: com.lbq.concurrent.jmh.JMHExample11.measureWrong
//
//# Run progress: 50.00% complete, ETA 00:00:17
//# Fork: 1 of 1
//# Warmup Iteration   1: ≈ 10⁻⁴ us/op
//# Warmup Iteration   2: ≈ 10⁻⁴ us/op
//# Warmup Iteration   3: ≈ 10⁻⁴ us/op
//# Warmup Iteration   4: ≈ 10⁻⁴ us/op
//# Warmup Iteration   5: ≈ 10⁻⁴ us/op
//Iteration   1: ≈ 10⁻⁴ us/op
//Iteration   2: ≈ 10⁻⁴ us/op
//Iteration   3: ≈ 10⁻⁴ us/op
//Iteration   4: ≈ 10⁻⁴ us/op
//Iteration   5: <failure>
//
//java.lang.AssertionError: The list elements must greater than zero
//	at com.lbq.concurrent.jmh.JMHExample11.testDown(JMHExample11.java:44)
//	at com.lbq.concurrent.jmh.generated.JMHExample11_measureWrong_jmhTest.measureWrong_AverageTime(JMHExample11_measureWrong_jmhTest.java:165)
//	at sun.reflect.NativeMethodAccessorImpl.invoke0(Native Method)
//	at sun.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:62)
//	at sun.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43)
//	at java.lang.reflect.Method.invoke(Method.java:498)
//	at org.openjdk.jmh.runner.BenchmarkHandler$BenchmarkTask.call(BenchmarkHandler.java:453)
//	at org.openjdk.jmh.runner.BenchmarkHandler$BenchmarkTask.call(BenchmarkHandler.java:437)
//	at java.util.concurrent.FutureTask.run(FutureTask.java:266)
//	at java.util.concurrent.Executors$RunnableAdapter.call(Executors.java:511)
//	at java.util.concurrent.FutureTask.run(FutureTask.java:266)
//	at java.util.concurrent.ThreadPoolExecutor.runWorker(ThreadPoolExecutor.java:1149)
//	at java.util.concurrent.ThreadPoolExecutor$Worker.run(ThreadPoolExecutor.java:624)
//	at java.lang.Thread.run(Thread.java:748)
//
//
//
//
//Result "com.lbq.concurrent.jmh.JMHExample11.measureWrong":
//  ≈ 10⁻⁴ us/op
//
//
//# Run complete. Total time: 00:00:27
//
//Benchmark                  Mode  Cnt        Score      Error  Units
//JMHExample11.measureRight  avgt    5  1000378.830 ± 1472.228  us/op
//JMHExample11.measureWrong  avgt    4       ≈ 10⁻⁴             us/op