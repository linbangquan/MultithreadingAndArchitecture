package com.lbq.concurrent.jmh;

import java.util.concurrent.TimeUnit;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
/**
 * 1.3 JMH的基本用法
 * 本节将学习JMH的基本用法，让读者彻底掌握如何使用这套工具集进行代码基准测试的操作。
 * 
 * 1.3.1 @Benchmark 标记基准测试方法
 * 与Junit4.x版本需要使用@Test注解标记单元测试方法一样，
 * JMH对基准测试的方法需要使用@Benchmark注解进行标记，否则方法将被视为普通方法，并且不会对其执行基准测试。
 * 如果一个类中没有任何的基准测试方法(被@Benchmark标记的方法)，那么对其进行基准测试则会出现异常。
 * 下面的代码虽然被include为需要执行基准测试的class，但是并没有一个方法被标注为@Benchmark。
 * 
 * 在JMHExample02中，并没有一个被@Benchmark标记的方法，运行该类的时候会出现异常，如下所示。
 * Exception in thread "main" No benchmarks to run; check the include/exclude regexps.
 * 		at org.openjdk.jmh.runner.Runner.internalRun(Runner.java:271)
 * 		at org.openjdk.jmh.runner.Runner.run(Runner.java:209)
 * 		at com.lbq.concurrent.jmh.JMHExample02.main(JMHExample02.java:45)
 * 因此请务必使用@Benchmark标记需要进行基准测试的方法。
 * 
 * This class not contains any method that be annotated by @Benchmark
 * @author 14378
 *
 */
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MICROSECONDS)
@State(Scope.Thread)
public class JMHExample02 {

	/**
	 * normal instance method
	 */
	//@Benchmark
	public void normalMethod() {
		
	}
	
	public static void main(String[] args) throws RunnerException {
		final Options opts = new OptionsBuilder()
				.include(JMHExample02.class.getSimpleName())
				.forks(1)
				.measurementIterations(10)
				.warmupIterations(10)
				.build();
		new Runner(opts).run();

	}

}
