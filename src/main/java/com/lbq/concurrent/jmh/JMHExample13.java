package com.lbq.concurrent.jmh;

import java.util.concurrent.TimeUnit;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
/**
 * 1.4 编写正确的微基准测试以及高级用法
 * 虽然JMH可以帮我们更好地了了解我们所编写的代码，但是如果我们所编写的JMH基准测试方法本身就有问题，那么就会很难起到指导的作用，
 * 甚至还会可能会产生误导，本节将介绍如何避免编写错误的微基准测试方法，同时学习一些较为高级的基准测试方案。
 * 
 * 1.4.1 编写正确的微基准测试用例
 * 现代的Java虚拟机已经发展得越来越智能了，它在类的早期编译阶段、加载阶段以后后期的运行时都可以为我们的代码进行相关的优化，
 * 比如Dead Code的擦除、常量的折叠，还有循环的打开，甚至是进程Profiler的优化，等等，因此要掌握如何编写良好的微基准测试方法，
 * 首先我们要知道什么样的基准测试代码是有问题的。
 * 1.避免DCE(Dead Code Elimination)
 * 所谓Dead Code Elimination是指JVM为我们擦去了一些上下文无关，甚至经过计算之后确定压根不会用到的代码，比如下面这样的代码片段。
 * public void test(){
 * 		int x = 10;
 * 		int y = 10;
 * 		int z = x + y;
 * }
 * 我们在test方法中分别定义了x和y，并且经过相加运算得到了z，但是在该方法的下文中再也没有其他地方使用到z
 * （既没有对z进行返回，也没有对其进行二次使用，z甚至不是一个全局的变量），
 * JVM很可能会将test()方法当作一个空的方法来看待，也就是说会擦除对x、y的定义，以及计算z的相关代码。
 * 
 * 1.baseline方法作为一个空的方法，主要用于做基准数据。
 * 2.measureLog1中虽然进行了log运算，但是结果既没有再进行二次使用，也没有进行返回。
 * 3.measureLog2中同样进行了log运算，虽然第一次的运算结果是作为第二次入参来使用的，但是第二次执行结束后也再没有对其有更进一步的使用。
 * 4.measureLog3方法与measureLog1的方法类似，但是该方法对运算结果进行了返回操作。
 * 下面就来运行一下这段JMHExample13，将会得到如下的输出结果。
 * Benchmark                 Mode  Cnt   Score    Error  Units
 * JMHExample13.baseline     avgt    5  ≈ 10⁻³           us/op
 * JMHExample13.measureLog1  avgt    5  ≈ 10⁻³           us/op
 * JMHExample13.measureLog2  avgt    5  ≈ 10⁻³           us/op
 * JMHExample13.measureLog3  avgt    5   0.003 ±  0.001  us/op
 * 从输出结果可以看出，measureLog1和measureLog2方法的基准性能与baseline几乎完全一致，
 * 因此我们可以肯定的是，这两个方法中代码进行过擦除操作，这样的代码被称为Dead Code(死代码，
 * 其他地方都没有用到的代码片段)，而measureLog3则与上述两个方法不同，由于它对结果进行了返回，
 * 因此Math.log(PI)不会被认为它是Dead Code，因此它将占用一定的CPU时间。
 * 
 * 通过这个例子我们可以发现，若想要编写性能良好的微基准测试方法，则不要让方法存在Dead Code，最好每一个基准测试方法都有返回值。
 * @author 14378
 *
 */
@BenchmarkMode(Mode.AverageTime)
@Fork(1)
@Warmup(iterations = 5)
@Measurement(iterations = 5)
@OutputTimeUnit(TimeUnit.MICROSECONDS)
@State(Scope.Thread)
public class JMHExample13 {

	@Benchmark
	public void baseline() {
		//空方法
	}
	@Benchmark
	public void measureLog1() {
		//进行数学运算，但是在局部方法内
		Math.log(Math.PI);
	}
	
	@Benchmark
	public void measureLog2() {
		//result是通过数学运算所得并且在下一行代码中得到了使用
		double result = Math.log(Math.PI);
		//对result进行数学运算，但是结果既不保存也不返回，更不会进行二次运算
		Math.log(result);
	}
	@Benchmark
	public double measureLog3() {
		//返回数学运算结果
		return Math.log(Math.PI);
	}
	
	public static void main(String[] args) throws RunnerException {
		final Options opts = new OptionsBuilder()
				.include(JMHExample13.class.getSimpleName())
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
//# Threads: 1 thread, will synchronize iterations
//# Benchmark mode: Average time, time/op
//# Benchmark: com.lbq.concurrent.jmh.JMHExample13.baseline
//
//# Run progress: 0.00% complete, ETA 00:00:40
//# Fork: 1 of 1
//# Warmup Iteration   1: ≈ 10⁻³ us/op
//# Warmup Iteration   2: ≈ 10⁻³ us/op
//# Warmup Iteration   3: ≈ 10⁻³ us/op
//# Warmup Iteration   4: ≈ 10⁻³ us/op
//# Warmup Iteration   5: ≈ 10⁻³ us/op
//Iteration   1: ≈ 10⁻³ us/op
//Iteration   2: ≈ 10⁻³ us/op
//Iteration   3: ≈ 10⁻³ us/op
//Iteration   4: ≈ 10⁻³ us/op
//Iteration   5: ≈ 10⁻³ us/op
//
//
//Result "com.lbq.concurrent.jmh.JMHExample13.baseline":
//  ≈ 10⁻³ us/op
//
//
//# JMH version: 1.19
//# VM version: JDK 1.8.0_202, VM 25.202-b08
//# VM invoker: D:\DevInstall\Java\jdk1.8.0_202\jre\bin\java.exe
//# VM options: -Dfile.encoding=UTF-8
//# Warmup: 5 iterations, 1 s each
//# Measurement: 5 iterations, 1 s each
//# Timeout: 10 min per iteration
//# Threads: 1 thread, will synchronize iterations
//# Benchmark mode: Average time, time/op
//# Benchmark: com.lbq.concurrent.jmh.JMHExample13.measureLog1
//
//# Run progress: 25.00% complete, ETA 00:00:32
//# Fork: 1 of 1
//# Warmup Iteration   1: ≈ 10⁻³ us/op
//# Warmup Iteration   2: ≈ 10⁻³ us/op
//# Warmup Iteration   3: ≈ 10⁻³ us/op
//# Warmup Iteration   4: ≈ 10⁻³ us/op
//# Warmup Iteration   5: ≈ 10⁻³ us/op
//Iteration   1: ≈ 10⁻³ us/op
//Iteration   2: ≈ 10⁻³ us/op
//Iteration   3: ≈ 10⁻³ us/op
//Iteration   4: ≈ 10⁻³ us/op
//Iteration   5: ≈ 10⁻³ us/op
//
//
//Result "com.lbq.concurrent.jmh.JMHExample13.measureLog1":
//  ≈ 10⁻³ us/op
//
//
//# JMH version: 1.19
//# VM version: JDK 1.8.0_202, VM 25.202-b08
//# VM invoker: D:\DevInstall\Java\jdk1.8.0_202\jre\bin\java.exe
//# VM options: -Dfile.encoding=UTF-8
//# Warmup: 5 iterations, 1 s each
//# Measurement: 5 iterations, 1 s each
//# Timeout: 10 min per iteration
//# Threads: 1 thread, will synchronize iterations
//# Benchmark mode: Average time, time/op
//# Benchmark: com.lbq.concurrent.jmh.JMHExample13.measureLog2
//
//# Run progress: 50.00% complete, ETA 00:00:21
//# Fork: 1 of 1
//# Warmup Iteration   1: ≈ 10⁻³ us/op
//# Warmup Iteration   2: ≈ 10⁻³ us/op
//# Warmup Iteration   3: ≈ 10⁻³ us/op
//# Warmup Iteration   4: ≈ 10⁻³ us/op
//# Warmup Iteration   5: ≈ 10⁻³ us/op
//Iteration   1: ≈ 10⁻³ us/op
//Iteration   2: ≈ 10⁻³ us/op
//Iteration   3: ≈ 10⁻³ us/op
//Iteration   4: ≈ 10⁻³ us/op
//Iteration   5: ≈ 10⁻³ us/op
//
//
//Result "com.lbq.concurrent.jmh.JMHExample13.measureLog2":
//  ≈ 10⁻³ us/op
//
//
//# JMH version: 1.19
//# VM version: JDK 1.8.0_202, VM 25.202-b08
//# VM invoker: D:\DevInstall\Java\jdk1.8.0_202\jre\bin\java.exe
//# VM options: -Dfile.encoding=UTF-8
//# Warmup: 5 iterations, 1 s each
//# Measurement: 5 iterations, 1 s each
//# Timeout: 10 min per iteration
//# Threads: 1 thread, will synchronize iterations
//# Benchmark mode: Average time, time/op
//# Benchmark: com.lbq.concurrent.jmh.JMHExample13.measureLog3
//
//# Run progress: 75.00% complete, ETA 00:00:11
//# Fork: 1 of 1
//# Warmup Iteration   1: 0.004 us/op
//# Warmup Iteration   2: 0.004 us/op
//# Warmup Iteration   3: 0.003 us/op
//# Warmup Iteration   4: 0.003 us/op
//# Warmup Iteration   5: 0.003 us/op
//Iteration   1: 0.003 us/op
//Iteration   2: 0.003 us/op
//Iteration   3: 0.003 us/op
//Iteration   4: 0.003 us/op
//Iteration   5: 0.003 us/op
//
//
//Result "com.lbq.concurrent.jmh.JMHExample13.measureLog3":
//  0.003 ±(99.9%) 0.001 us/op [Average]
//  (min, avg, max) = (0.003, 0.003, 0.003), stdev = 0.001
//  CI (99.9%): [0.003, 0.003] (assumes normal distribution)
//
//
//# Run complete. Total time: 00:00:44
//
//Benchmark                 Mode  Cnt   Score    Error  Units
//JMHExample13.baseline     avgt    5  ≈ 10⁻³           us/op
//JMHExample13.measureLog1  avgt    5  ≈ 10⁻³           us/op
//JMHExample13.measureLog2  avgt    5  ≈ 10⁻³           us/op
//JMHExample13.measureLog3  avgt    5   0.003 ±  0.001  us/op