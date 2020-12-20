package com.lbq.concurrent.jmh;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.concurrent.TimeUnit;
/**
 * 5.Fork 用于避免Profile-guided optimizations
 * 我们在所有的基准测试代码中几乎都使用了Fork，那么它到底是用来干什么的呢?似乎前文一直没有交代，这是笔者故意这样安排的，
 * 本节将会为大家介绍Fork的作用以及JVM的Profile-guided optimizations。
 * 
 * 在开始解释Fork之前，我们想象一下平时是如何进行应用性能测试的，比如我们要测试一下Redis分别在50、100、200个线程中同时进行共计一亿次的写操作时的响应速度，一般会怎样做?
 * 首先，我们会将Redis库清空，尽可能地保证每一次测试的时候，不同的测试用例站在同样的起跑线上，比如，服务器内存的大小，服务器磁盘的大小、服务器CPU的大小等基本上相同，
 * 这样的对比才是有意义的，然后根据测试用例对其进行测试，接着清理Redis服务器资源，使其回到测试之前的状态，最后统计测试结果做出测试报告。
 * 
 * Fork的引入也是考虑到了这个问题，虽然Java支持多线程，但是不支持多进程，这就导致了所有的代码都在一个进程中运行，
 * 相同的代码在不同时刻的执行可能会引入前一阶段对进程profiler的优化，甚至会混入其他代码profiler优化时的参数，
 * 这很可能会导致我们所编写的微基准测试出现不准确的问题。对于这种说法大家可能会觉得有些抽象，下面我们还是通过代码实例为大家进行演示。
 * 
 * 将Fork设置为0，每一个基准测试方法都将会与JMHExample17使用同一个JVM进程，因此基准测试方法可能会混入JMHExample17进程的Profiler，运行基准测试将会得出如下的结果。
 * Benchmark                   Mode  Cnt  Score    Error  Units
 * JMHExample17.measure_inc_1  avgt    5  0.003 ±  0.001  us/op
 * JMHExample17.measure_inc_2  avgt    5  0.020 ±  0.001  us/op
 * JMHExample17.measure_inc_3  avgt    5  0.019 ±  0.002  us/op
 * 
 * measure_inc_1和measure_inc_2的实现方式几乎是一致的，它们的性能却存在着较大的差距，
 * 虽然measure_inc_1和measure_inc_3的代码实现完全相同，但还是存在着不同的性能数据，
 * 这其实就是JVM Profiler-guided optimizations导致的，由于我们所有的基准测试方法都
 * 与JMHExample17的JVM进程共享，因此难免在其中混入JMHExample17进程的Profiler，
 * 但是在将在Fork设置为1的时候，也就是说每一次运行基准测试时都会开辟一个全新的JVM进程对其进行测试，
 * 那么多个基准测试之间将不会再存在干扰。
 * Benchmark                   Mode  Cnt  Score    Error  Units
 * JMHExample17.measure_inc_1  avgt    5  0.005 ±  0.001  us/op
 * JMHExample17.measure_inc_2  avgt    5  0.005 ±  0.001  us/op
 * JMHExample17.measure_inc_3  avgt    5  0.005 ±  0.001  us/op
 * 以上输出是将Fork设置为1的结果，是不是合理了很多，若将Fork设置为0，则会与运行基准测试的类共享同样的进程Profiler，
 * 若设置为1则会为每一个基准测试方法开辟新的进程去运行，当然，你可以将Fork设置为大于1的数值，那么它将多次运行在不同的进程中，
 * 不过一般情况下，我们只需要将Fork设置为1即可。
 * @author 14378
 *
 */
@BenchmarkMode(Mode.AverageTime)
//将Fork设置为0
@Fork(0)
@Warmup(iterations = 5)
@Measurement(iterations = 5)
@OutputTimeUnit(TimeUnit.MICROSECONDS)
@State(Scope.Thread)
public class JMHExample17 {

	interface Inc {
		int inc();
	}
	
	public static class Inc1 implements Inc {
		private int i = 0;
		
		@Override
		public int inc() {
			return ++i;
		}
	}
	public static class Inc2 implements Inc {
		private int i = 0;
		
		@Override
		public int inc() {
			return ++i;
		}
	}
	
	private Inc inc1 = new Inc1();
	private Inc inc2 = new Inc2();
	
	private int measure(Inc inc) {
		int result = 0;
		for(int i = 0; i < 10; i++) {
			result += inc.inc();
		}
		return result;
	}
	
	@Benchmark
	public int measure_inc_1() {
		return this.measure(inc1);
	}
	
	@Benchmark
	public int measure_inc_2() {
		return this.measure(inc2);
	}
	
	@Benchmark
	public int measure_inc_3() {
		return this.measure(inc1);
	}
	
	public static void main(String[] args) throws RunnerException {
		final Options opts = new OptionsBuilder()
				.include(JMHExample17.class.getSimpleName())
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
//# Benchmark: com.lbq.concurrent.jmh.JMHExample17.measure_inc_1
//
//# Run progress: 0.00% complete, ETA 00:00:30
//# Fork: N/A, test runs in the host VM
//# *** WARNING: Non-forked runs may silently omit JVM options, mess up profilers, disable compiler hints, etc. ***
//# *** WARNING: Use non-forked runs only for debugging purposes, not for actual performance runs. ***
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
//Result "com.lbq.concurrent.jmh.JMHExample17.measure_inc_1":
//  0.003 ±(99.9%) 0.001 us/op [Average]
//  (min, avg, max) = (0.003, 0.003, 0.003), stdev = 0.001
//  CI (99.9%): [0.003, 0.004] (assumes normal distribution)
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
//# Benchmark: com.lbq.concurrent.jmh.JMHExample17.measure_inc_2
//
//# Run progress: 33.33% complete, ETA 00:00:20
//# Fork: N/A, test runs in the host VM
//# *** WARNING: Non-forked runs may silently omit JVM options, mess up profilers, disable compiler hints, etc. ***
//# *** WARNING: Use non-forked runs only for debugging purposes, not for actual performance runs. ***
//# Warmup Iteration   1: 0.021 us/op
//# Warmup Iteration   2: 0.020 us/op
//# Warmup Iteration   3: 0.020 us/op
//# Warmup Iteration   4: 0.019 us/op
//# Warmup Iteration   5: 0.019 us/op
//Iteration   1: 0.020 us/op
//Iteration   2: 0.020 us/op
//Iteration   3: 0.019 us/op
//Iteration   4: 0.019 us/op
//Iteration   5: 0.020 us/op
//
//Result "com.lbq.concurrent.jmh.JMHExample17.measure_inc_2":
//  0.020 ±(99.9%) 0.001 us/op [Average]
//  (min, avg, max) = (0.019, 0.020, 0.020), stdev = 0.001
//  CI (99.9%): [0.018, 0.021] (assumes normal distribution)
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
//# Benchmark: com.lbq.concurrent.jmh.JMHExample17.measure_inc_3
//
//# Run progress: 66.67% complete, ETA 00:00:10
//# Fork: N/A, test runs in the host VM
//# *** WARNING: Non-forked runs may silently omit JVM options, mess up profilers, disable compiler hints, etc. ***
//# *** WARNING: Use non-forked runs only for debugging purposes, not for actual performance runs. ***
//# Warmup Iteration   1: 0.019 us/op
//# Warmup Iteration   2: 0.019 us/op
//# Warmup Iteration   3: 0.019 us/op
//# Warmup Iteration   4: 0.019 us/op
//# Warmup Iteration   5: 0.019 us/op
//Iteration   1: 0.019 us/op
//Iteration   2: 0.019 us/op
//Iteration   3: 0.020 us/op
//Iteration   4: 0.020 us/op
//Iteration   5: 0.019 us/op
//
//Result "com.lbq.concurrent.jmh.JMHExample17.measure_inc_3":
//  0.019 ±(99.9%) 0.002 us/op [Average]
//  (min, avg, max) = (0.019, 0.019, 0.020), stdev = 0.001
//  CI (99.9%): [0.017, 0.021] (assumes normal distribution)
//
//
//# Run complete. Total time: 00:00:30
//
//Benchmark                   Mode  Cnt  Score    Error  Units
//JMHExample17.measure_inc_1  avgt    5  0.003 ±  0.001  us/op
//JMHExample17.measure_inc_2  avgt    5  0.020 ±  0.001  us/op
//JMHExample17.measure_inc_3  avgt    5  0.019 ±  0.002  us/op

//----------------------------------------------------------------------------------------------------------------------------------------
//# JMH version: 1.19
//# VM version: JDK 1.8.0_202, VM 25.202-b08
//# VM invoker: D:\DevInstall\Java\jdk1.8.0_202\jre\bin\java.exe
//# VM options: -Dfile.encoding=UTF-8
//# Warmup: 5 iterations, 1 s each
//# Measurement: 5 iterations, 1 s each
//# Timeout: 10 min per iteration
//# Threads: 1 thread, will synchronize iterations
//# Benchmark mode: Average time, time/op
//# Benchmark: com.lbq.concurrent.jmh.JMHExample17.measure_inc_1
//
//# Run progress: 0.00% complete, ETA 00:00:30
//# Fork: 1 of 1
//# Warmup Iteration   1: 0.006 us/op
//# Warmup Iteration   2: 0.006 us/op
//# Warmup Iteration   3: 0.005 us/op
//# Warmup Iteration   4: 0.005 us/op
//# Warmup Iteration   5: 0.005 us/op
//Iteration   1: 0.005 us/op
//Iteration   2: 0.005 us/op
//Iteration   3: 0.005 us/op
//Iteration   4: 0.005 us/op
//Iteration   5: 0.005 us/op
//
//
//Result "com.lbq.concurrent.jmh.JMHExample17.measure_inc_1":
//  0.005 ±(99.9%) 0.001 us/op [Average]
//  (min, avg, max) = (0.005, 0.005, 0.005), stdev = 0.001
//  CI (99.9%): [0.004, 0.005] (assumes normal distribution)
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
//# Benchmark: com.lbq.concurrent.jmh.JMHExample17.measure_inc_2
//
//# Run progress: 33.33% complete, ETA 00:00:21
//# Fork: 1 of 1
//# Warmup Iteration   1: 0.006 us/op
//# Warmup Iteration   2: 0.006 us/op
//# Warmup Iteration   3: 0.005 us/op
//# Warmup Iteration   4: 0.005 us/op
//# Warmup Iteration   5: 0.005 us/op
//Iteration   1: 0.005 us/op
//Iteration   2: 0.005 us/op
//Iteration   3: 0.005 us/op
//Iteration   4: 0.005 us/op
//Iteration   5: 0.005 us/op
//
//
//Result "com.lbq.concurrent.jmh.JMHExample17.measure_inc_2":
//  0.005 ±(99.9%) 0.001 us/op [Average]
//  (min, avg, max) = (0.005, 0.005, 0.005), stdev = 0.001
//  CI (99.9%): [0.005, 0.005] (assumes normal distribution)
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
//# Benchmark: com.lbq.concurrent.jmh.JMHExample17.measure_inc_3
//
//# Run progress: 66.67% complete, ETA 00:00:10
//# Fork: 1 of 1
//# Warmup Iteration   1: 0.007 us/op
//# Warmup Iteration   2: 0.006 us/op
//# Warmup Iteration   3: 0.005 us/op
//# Warmup Iteration   4: 0.005 us/op
//# Warmup Iteration   5: 0.005 us/op
//Iteration   1: 0.005 us/op
//Iteration   2: 0.005 us/op
//Iteration   3: 0.005 us/op
//Iteration   4: 0.005 us/op
//Iteration   5: 0.005 us/op
//
//
//Result "com.lbq.concurrent.jmh.JMHExample17.measure_inc_3":
//  0.005 ±(99.9%) 0.001 us/op [Average]
//  (min, avg, max) = (0.005, 0.005, 0.005), stdev = 0.001
//  CI (99.9%): [0.004, 0.005] (assumes normal distribution)
//
//
//# Run complete. Total time: 00:00:32
//
//Benchmark                   Mode  Cnt  Score    Error  Units
//JMHExample17.measure_inc_1  avgt    5  0.005 ±  0.001  us/op
//JMHExample17.measure_inc_2  avgt    5  0.005 ±  0.001  us/op
//JMHExample17.measure_inc_3  avgt    5  0.005 ±  0.001  us/op