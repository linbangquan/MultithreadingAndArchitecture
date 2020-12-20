package com.lbq.concurrent.jmh;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.concurrent.TimeUnit;
/**
 * 2.使用Blackhole
 * 假设在基准测试方法中，需要将两个计算结果作为返回值，那么我们该如何去做呢?
 * 我们第一时间想到的可能是将结果存放到某个数组或者容器当中作为返回值，但是这种对数组或者容器的操作会对性能统计造成干扰，因为对数组或者容器的写操作也是需要花费一定的CPU时间的。
 * JMH提供了一个称为Blackhole的类，可以在不做任何返回的情况下避免Dead Code的发生，Blackhole直译为“黑洞”，与Linux系统下的黑洞设备/dev/null非常相似，请看下面的代码示例。
 * 1.baseline方法中对x1进行了pow运算，之后返回，因此这个基准测试方法是非常合理的。
 * 2.powButReturnOne方法中的第一个pow运算仍然避免不了被当作Dead Code的命运，因此我们很难得到两次pow计算的方法耗时，但是对x2的pow运算会作为返回值返回，因此不是dead code。
 * 3.powThenAdd方法就比较聪明，它同样会有返回值，两次pow操作也会被正常执行，但是由于采取的是加法运算，因此相加操作的CPU耗时也被计算到了两次pow操作中。
 * 4.useBlackhole方法中两次pow方法都会被执行，但是我们并没有对其进行返回操作，而是将其写入了black hole之中。
 * 下面我们看一下执行结果。
 * Benchmark                     Mode  Cnt  Score   Error  Units
 * JMHExample14.baseline         avgt    5  3.370 ± 0.234  ns/op
 * JMHExample14.powButReturnOne  avgt    5  3.361 ± 0.288  ns/op
 * JMHExample14.powThenAdd       avgt    5  3.536 ± 0.271  ns/op
 * JMHExample14.useBlackhole     avgt    5  5.852 ± 0.138  ns/op
 * 
 * 输出结果表明，baseline和putButReturnOne方法的性能几乎是一样的，
 * powThenAdd的性能相比前两个方法占用CPU的时间要稍微长一些，原因是该方法执行了两次pow操作。
 * 在useBlackhole中虽然没有对两个参数进行任何的合并操作，但是由于执行了black hole的consume方法，因此也会占用一定的CPU资源。
 * 虽然blackhole的consume方法会占用一定的CPU资源，但是如果在无返回值的基准测试方法中针对局部变量的使用都统一通过blackhole进行consume，
 * 那么就可以确保同样的基准执行条件，就好比拳击比赛，对抗的拳手之间需要统一的体重量级一样。
 * 
 * 总结起来，Blackhole可以帮助你在无返回值的基准测试方法中避免DC (Dead Code)情况的发生。
 * @author 14378
 *
 */
@BenchmarkMode(Mode.AverageTime)
@Fork(1)
@Warmup(iterations = 5)
@Measurement(iterations = 5)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@State(Scope.Thread)
public class JMHExample14 {

	double x1 = Math.PI;
	double x2 = Math.PI * 2;
	
	@Benchmark
	public double baseline() {
		//不是Dead Code，因为对结果进行了返回
		return Math.pow(x1, 2);
	}
	
	@Benchmark
	public double powButReturnOne() {
		//Dead Code会被擦除
		Math.pow(x1, 2);
		//不会被擦除，因为对结果进行了返回
		return Math.pow(x2, 2);
	}
	
	@Benchmark
	public double powThenAdd() {
		//通过加法运算对两个结果进行了合并，因此两次的计算都会生效
		return Math.pow(x1, 2) + Math.pow(x2, 2);
	}
	
	@Benchmark
	public void useBlackhole(Blackhole hole) {
		//将结果存放至black hole中，因此两次pow操作都会生效
		hole.consume(Math.pow(x1, 2));
		hole.consume(Math.pow(x2, 2));
	}
	
	public static void main(String[] args) throws RunnerException {
		final Options opts = new OptionsBuilder()
				.include(JMHExample14.class.getSimpleName())
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
//# Benchmark: com.lbq.concurrent.jmh.JMHExample14.baseline
//
//# Run progress: 0.00% complete, ETA 00:00:40
//# Fork: 1 of 1
//# Warmup Iteration   1: 3.537 ns/op
//# Warmup Iteration   2: 3.874 ns/op
//# Warmup Iteration   3: 3.346 ns/op
//# Warmup Iteration   4: 3.321 ns/op
//# Warmup Iteration   5: 3.338 ns/op
//Iteration   1: 3.326 ns/op
//Iteration   2: 3.340 ns/op
//Iteration   3: 3.336 ns/op
//Iteration   4: 3.371 ns/op
//Iteration   5: 3.474 ns/op
//
//
//Result "com.lbq.concurrent.jmh.JMHExample14.baseline":
//  3.370 ±(99.9%) 0.234 ns/op [Average]
//  (min, avg, max) = (3.326, 3.370, 3.474), stdev = 0.061
//  CI (99.9%): [3.135, 3.604] (assumes normal distribution)
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
//# Benchmark: com.lbq.concurrent.jmh.JMHExample14.powButReturnOne
//
//# Run progress: 25.00% complete, ETA 00:00:32
//# Fork: 1 of 1
//# Warmup Iteration   1: 3.598 ns/op
//# Warmup Iteration   2: 3.861 ns/op
//# Warmup Iteration   3: 3.355 ns/op
//# Warmup Iteration   4: 3.583 ns/op
//# Warmup Iteration   5: 3.357 ns/op
//Iteration   1: 3.275 ns/op
//Iteration   2: 3.341 ns/op
//Iteration   3: 3.352 ns/op
//Iteration   4: 3.356 ns/op
//Iteration   5: 3.481 ns/op
//
//
//Result "com.lbq.concurrent.jmh.JMHExample14.powButReturnOne":
//  3.361 ±(99.9%) 0.288 ns/op [Average]
//  (min, avg, max) = (3.275, 3.361, 3.481), stdev = 0.075
//  CI (99.9%): [3.073, 3.649] (assumes normal distribution)
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
//# Benchmark: com.lbq.concurrent.jmh.JMHExample14.powThenAdd
//
//# Run progress: 50.00% complete, ETA 00:00:21
//# Fork: 1 of 1
//# Warmup Iteration   1: 3.891 ns/op
//# Warmup Iteration   2: 3.915 ns/op
//# Warmup Iteration   3: 3.512 ns/op
//# Warmup Iteration   4: 3.604 ns/op
//# Warmup Iteration   5: 3.559 ns/op
//Iteration   1: 3.518 ns/op
//Iteration   2: 3.540 ns/op
//Iteration   3: 3.464 ns/op
//Iteration   4: 3.504 ns/op
//Iteration   5: 3.651 ns/op
//
//
//Result "com.lbq.concurrent.jmh.JMHExample14.powThenAdd":
//  3.536 ±(99.9%) 0.271 ns/op [Average]
//  (min, avg, max) = (3.464, 3.536, 3.651), stdev = 0.070
//  CI (99.9%): [3.265, 3.806] (assumes normal distribution)
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
//# Benchmark: com.lbq.concurrent.jmh.JMHExample14.useBlackhole
//
//# Run progress: 75.00% complete, ETA 00:00:10
//# Fork: 1 of 1
//# Warmup Iteration   1: 6.767 ns/op
//# Warmup Iteration   2: 6.185 ns/op
//# Warmup Iteration   3: 6.074 ns/op
//# Warmup Iteration   4: 5.757 ns/op
//# Warmup Iteration   5: 5.834 ns/op
//Iteration   1: 5.862 ns/op
//Iteration   2: 5.865 ns/op
//Iteration   3: 5.790 ns/op
//Iteration   4: 5.883 ns/op
//Iteration   5: 5.861 ns/op
//
//
//Result "com.lbq.concurrent.jmh.JMHExample14.useBlackhole":
//  5.852 ±(99.9%) 0.138 ns/op [Average]
//  (min, avg, max) = (5.790, 5.852, 5.883), stdev = 0.036
//  CI (99.9%): [5.714, 5.990] (assumes normal distribution)
//
//
//# Run complete. Total time: 00:00:42
//
//Benchmark                     Mode  Cnt  Score   Error  Units
//JMHExample14.baseline         avgt    5  3.370 ± 0.234  ns/op
//JMHExample14.powButReturnOne  avgt    5  3.361 ± 0.288  ns/op
//JMHExample14.powThenAdd       avgt    5  3.536 ± 0.271  ns/op
//JMHExample14.useBlackhole     avgt    5  5.852 ± 0.138  ns/op

