package com.lbq.concurrent.jmh;

import java.util.concurrent.TimeUnit;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
/**
 * 1.3.3 四大BenchmarkMode
 * JMH使用@BenchmarkMode这个注解来声明使用哪一种模式来运行，JMH为我们提供了四种运行模式，当然它还允许若干个模式同时存在，
 * 在笔者看来，Mode无非就是统计基准测试数据的不同方式和纬度口径，本节将逐一探讨JMH为我们提供的四种运行模式。
 * 
 * BenchmarkMode既可以在class上进行注解设置，也可以在基准方法上进行注解设置，方法中设置的模式将会覆盖class注解上的设置，
 * 同样，在Options中也可以进行设置，它将会覆盖所有基准方法上的设置。
 * @author 14378
 *
 */
//@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@State(Scope.Thread)
public class JMHExample04 {
	
	/**
	 * 1.AverageTime(平均响应时间)模式在前文中已经出现过几次了，它主要用于输出基准测试方法的每调用一次的所耗费的时间，也就是elapsed time/operation。
	 * 运行下面的基准测试方法，我们会得出如下的测试结果。
	 * Benchmark                        Mode    Cnt  Score   Error   Units
	 * JMHExample04.testAverageTime		avgt      5  1.787 ± 0.077   ms/op
	 * testAverageTime方法的平均执行耗时为1.787毫秒。
	 * @throws InterruptedException
	 */
	@BenchmarkMode(Mode.AverageTime)
	@Benchmark
	public void testAverageTime() throws InterruptedException {
		TimeUnit.MILLISECONDS.sleep(1);
	}
	/**
	 * 2.Throughput(方法吞吐量)则刚刚好与AverageTime相反，它的输出信息表明了在单位时间内可以对该方法调用多少次。
	 * 运行下面的基准测试方法，我们会得出如下的测试结果。
	 * Benchmark                        Mode    Cnt  Score   Error   Units
	 * JMHExample04.testThroughput      thrpt      5  0.563 ± 0.008  ops/ms
	 * 可以看到在1毫秒内，testThroughput方法只会被调用0.563次。
	 * @throws InterruptedException
	 */
	@BenchmarkMode(Mode.Throughput)
	@Benchmark
	public void testThroughput() throws InterruptedException {
		TimeUnit.MILLISECONDS.sleep(1);
	}
	/**
	 * 3.SampleTime(时间采样)的方式是指采用一种抽样的方式来统计基准测试方法的性能结果，
	 * 与我们常见的Histogram图(直方图)几乎是一样的，它会收集所有的性能数据，并且将其分布在不同的区间中。
	 * 运行下面的结果我们会看到非常多的统计数据，具体如下所示。
	 * Result "com.lbq.concurrent.jmh.JMHExample04.testSampleTime":
	 * N = 27243
	 * mean =      1.832 ±(99.9%) 0.007 ms/op
	 * 
	 * Histogram, ms/op:
	 * [0.000, 0.250) = 0 
	 * [0.250, 0.500) = 0 
	 * [0.500, 0.750) = 1 
	 * [0.750, 1.000) = 904 
	 * [1.000, 1.250) = 3208 
	 * [1.250, 1.500) = 555 
	 * [1.500, 1.750) = 84 
	 * [1.750, 2.000) = 15883 
	 * [2.000, 2.250) = 6602 
	 * [2.250, 2.500) = 5 
	 * [2.500, 2.750) = 1 
	 * 
	 * Percentiles, ms/op:
	 *     p(0.0000) =      0.614 ms/op
	 *    p(50.0000) =      1.985 ms/op
	 *    p(90.0000) =      2.042 ms/op
	 *    p(95.0000) =      2.056 ms/op
	 *    p(99.0000) =      2.075 ms/op
	 *    p(99.9000) =      2.166 ms/op
	 *    p(99.9900) =      2.330 ms/op
	 *    p(99.9990) =      2.744 ms/op
	 *    p(99.9999) =      2.744 ms/op
	 *   p(100.0000) =      2.744 ms/op
	 * Benchmark                        					Mode    Cnt  Score   Error   Units
	 * JMHExample04.testSampleTime                         sample  27243  1.832 ± 0.007   ms/op
	 * JMHExample04.testSampleTime:testSampleTime·p0.00    sample         0.614           ms/op
	 * JMHExample04.testSampleTime:testSampleTime·p0.50    sample         1.985           ms/op
	 * JMHExample04.testSampleTime:testSampleTime·p0.90    sample         2.042           ms/op
	 * JMHExample04.testSampleTime:testSampleTime·p0.95    sample         2.056           ms/op
	 * JMHExample04.testSampleTime:testSampleTime·p0.99    sample         2.075           ms/op
	 * JMHExample04.testSampleTime:testSampleTime·p0.999   sample         2.166           ms/op
	 * JMHExample04.testSampleTime:testSampleTime·p0.9999  sample         2.330           ms/op
	 * JMHExample04.testSampleTime:testSampleTime·p1.00    sample         2.744           ms/op    
	 * 
	 * 从输出结果中不难发现，对testSampleTime方法总共进行了27243次的调用，该方法的平均响应时间为1.100毫秒，并且有15883次的性能数据落点在1.750~2.000毫秒这个区间之中。
	 * @throws InterruptedException
	 */
	@BenchmarkMode(Mode.SampleTime)
	@Benchmark
	public void testSampleTime() throws InterruptedException {
		TimeUnit.MILLISECONDS.sleep(1);
	}
	/**
	 * 4.SingleShotTime主要可用来进行冷测试，不论是Warmup还是Measurement，在每一个批次中基准测试方法只会被执行一次，一般情况下，我们会将Warmup的批次设置为0。
	 * 运行下面的基准测试代码，与SampleTime非常类似，Single Shot Time 也是采用Histogram的方式进行统计的。
	 * # Run complete. Total time: 00:13:27
	 * Benchmark						Mode    Cnt  Score   Error   Units
	 * JMHExample04.testSingleShotTime	ss         1.137           ms/op
	 * @throws InterruptedException
	 */
	@Warmup(iterations = 0)
	@BenchmarkMode(Mode.SingleShotTime)
	@Benchmark
	public void testSingleShotTime() throws InterruptedException {
		TimeUnit.MILLISECONDS.sleep(1);
	}
	/**
	 * 5.多Mode以及All
	 * 我们除了对某个基准测试方法设置上述四个模式中的一个之外，还可以为其设置多个模式的方式运行基准测试方法，如果你愿意，甚至可以设置全部的Mode。
	 * @throws InterruptedException
	 */
	@BenchmarkMode({Mode.AverageTime, Mode.Throughput})
	@Benchmark
	public void testThroughputAndAverageTime() throws InterruptedException {
		TimeUnit.MILLISECONDS.sleep(1);
	}
	
	@BenchmarkMode(Mode.All)
	@Benchmark
	public void testAll() throws InterruptedException {
		TimeUnit.MILLISECONDS.sleep(1);
	}
	
	public static void main(String[] args) throws RunnerException {
		final Options opts = new OptionsBuilder()
				.include(JMHExample04.class.getSimpleName())
				.forks(1)
				//.mode(Mode.All)
				.build();
		new Runner(opts).run();

	}

}

//# JMH version: 1.25
//# VM version: JDK 1.8.0_202, Java HotSpot(TM) 64-Bit Server VM, 25.202-b08
//# VM invoker: D:\DevInstall\Java\jdk1.8.0_202\jre\bin\java.exe
//# VM options: -Dfile.encoding=UTF-8
//# Warmup: 5 iterations, 10 s each
//# Measurement: 5 iterations, 10 s each
//# Timeout: 10 min per iteration
//# Threads: 1 thread, will synchronize iterations
//# Benchmark mode: Throughput, ops/time
//# Benchmark: com.lbq.concurrent.jmh.JMHExample04.testAll
//
//# Run progress: 0.00% complete, ETA 00:13:20
//# Fork: 1 of 1
//# Warmup Iteration   1: 0.550 ops/ms
//# Warmup Iteration   2: 0.544 ops/ms
//# Warmup Iteration   3: 0.545 ops/ms
//# Warmup Iteration   4: 0.545 ops/ms
//# Warmup Iteration   5: 0.547 ops/ms
//Iteration   1: 0.556 ops/ms
//Iteration   2: 0.548 ops/ms
//Iteration   3: 0.559 ops/ms
//Iteration   4: 0.548 ops/ms
//Iteration   5: 0.562 ops/ms
//
//
//Result "com.lbq.concurrent.jmh.JMHExample04.testAll":
//  0.555 ±(99.9%) 0.024 ops/ms [Average]
//  (min, avg, max) = (0.548, 0.555, 0.562), stdev = 0.006
//  CI (99.9%): [0.530, 0.579] (assumes normal distribution)
//
//
//# JMH version: 1.25
//# VM version: JDK 1.8.0_202, Java HotSpot(TM) 64-Bit Server VM, 25.202-b08
//# VM invoker: D:\DevInstall\Java\jdk1.8.0_202\jre\bin\java.exe
//# VM options: -Dfile.encoding=UTF-8
//# Warmup: 5 iterations, 10 s each
//# Measurement: 5 iterations, 10 s each
//# Timeout: 10 min per iteration
//# Threads: 1 thread, will synchronize iterations
//# Benchmark mode: Throughput, ops/time
//# Benchmark: com.lbq.concurrent.jmh.JMHExample04.testThroughput
//
//# Run progress: 12.50% complete, ETA 00:11:44
//# Fork: 1 of 1
//# Warmup Iteration   1: 0.558 ops/ms
//# Warmup Iteration   2: 0.560 ops/ms
//# Warmup Iteration   3: 0.552 ops/ms
//# Warmup Iteration   4: 0.544 ops/ms
//# Warmup Iteration   5: 0.553 ops/ms
//Iteration   1: 0.560 ops/ms
//Iteration   2: 0.561 ops/ms
//Iteration   3: 0.563 ops/ms
//Iteration   4: 0.563 ops/ms
//Iteration   5: 0.565 ops/ms
//
//
//Result "com.lbq.concurrent.jmh.JMHExample04.testThroughput":
//  0.563 ±(99.9%) 0.008 ops/ms [Average]
//  (min, avg, max) = (0.560, 0.563, 0.565), stdev = 0.002
//  CI (99.9%): [0.555, 0.570] (assumes normal distribution)
//
//
//# JMH version: 1.25
//# VM version: JDK 1.8.0_202, Java HotSpot(TM) 64-Bit Server VM, 25.202-b08
//# VM invoker: D:\DevInstall\Java\jdk1.8.0_202\jre\bin\java.exe
//# VM options: -Dfile.encoding=UTF-8
//# Warmup: 5 iterations, 10 s each
//# Measurement: 5 iterations, 10 s each
//# Timeout: 10 min per iteration
//# Threads: 1 thread, will synchronize iterations
//# Benchmark mode: Throughput, ops/time
//# Benchmark: com.lbq.concurrent.jmh.JMHExample04.testThroughputAndAverageTime
//
//# Run progress: 25.00% complete, ETA 00:10:04
//# Fork: 1 of 1
//# Warmup Iteration   1: 0.552 ops/ms
//# Warmup Iteration   2: 0.564 ops/ms
//# Warmup Iteration   3: 0.560 ops/ms
//# Warmup Iteration   4: 0.561 ops/ms
//# Warmup Iteration   5: 0.560 ops/ms
//Iteration   1: 0.556 ops/ms
//Iteration   2: 0.563 ops/ms
//Iteration   3: 0.563 ops/ms
//Iteration   4: 0.563 ops/ms
//Iteration   5: 0.561 ops/ms
//
//
//Result "com.lbq.concurrent.jmh.JMHExample04.testThroughputAndAverageTime":
//  0.561 ±(99.9%) 0.012 ops/ms [Average]
//  (min, avg, max) = (0.556, 0.561, 0.563), stdev = 0.003
//  CI (99.9%): [0.549, 0.574] (assumes normal distribution)
//
//
//# JMH version: 1.25
//# VM version: JDK 1.8.0_202, Java HotSpot(TM) 64-Bit Server VM, 25.202-b08
//# VM invoker: D:\DevInstall\Java\jdk1.8.0_202\jre\bin\java.exe
//# VM options: -Dfile.encoding=UTF-8
//# Warmup: 5 iterations, 10 s each
//# Measurement: 5 iterations, 10 s each
//# Timeout: 10 min per iteration
//# Threads: 1 thread, will synchronize iterations
//# Benchmark mode: Average time, time/op
//# Benchmark: com.lbq.concurrent.jmh.JMHExample04.testAll
//
//# Run progress: 37.50% complete, ETA 00:08:23
//# Fork: 1 of 1
//# Warmup Iteration   1: 1.781 ms/op
//# Warmup Iteration   2: 1.783 ms/op
//# Warmup Iteration   3: 1.789 ms/op
//# Warmup Iteration   4: 1.777 ms/op
//# Warmup Iteration   5: 1.790 ms/op
//Iteration   1: 1.840 ms/op
//Iteration   2: 1.851 ms/op
//Iteration   3: 1.853 ms/op
//Iteration   4: 1.853 ms/op
//Iteration   5: 1.785 ms/op
//
//
//Result "com.lbq.concurrent.jmh.JMHExample04.testAll":
//  1.836 ±(99.9%) 0.113 ms/op [Average]
//  (min, avg, max) = (1.785, 1.836, 1.853), stdev = 0.029
//  CI (99.9%): [1.724, 1.949] (assumes normal distribution)
//
//
//# JMH version: 1.25
//# VM version: JDK 1.8.0_202, Java HotSpot(TM) 64-Bit Server VM, 25.202-b08
//# VM invoker: D:\DevInstall\Java\jdk1.8.0_202\jre\bin\java.exe
//# VM options: -Dfile.encoding=UTF-8
//# Warmup: 5 iterations, 10 s each
//# Measurement: 5 iterations, 10 s each
//# Timeout: 10 min per iteration
//# Threads: 1 thread, will synchronize iterations
//# Benchmark mode: Average time, time/op
//# Benchmark: com.lbq.concurrent.jmh.JMHExample04.testAverageTime
//
//# Run progress: 50.00% complete, ETA 00:06:43
//# Fork: 1 of 1
//# Warmup Iteration   1: 1.703 ms/op
//# Warmup Iteration   2: 1.818 ms/op
//# Warmup Iteration   3: 1.793 ms/op
//# Warmup Iteration   4: 1.773 ms/op
//# Warmup Iteration   5: 1.779 ms/op
//Iteration   1: 1.760 ms/op
//Iteration   2: 1.815 ms/op
//Iteration   3: 1.785 ms/op
//Iteration   4: 1.792 ms/op
//Iteration   5: 1.781 ms/op
//
//
//Result "com.lbq.concurrent.jmh.JMHExample04.testAverageTime":
//  1.787 ±(99.9%) 0.077 ms/op [Average]
//  (min, avg, max) = (1.760, 1.787, 1.815), stdev = 0.020
//  CI (99.9%): [1.709, 1.864] (assumes normal distribution)
//
//
//# JMH version: 1.25
//# VM version: JDK 1.8.0_202, Java HotSpot(TM) 64-Bit Server VM, 25.202-b08
//# VM invoker: D:\DevInstall\Java\jdk1.8.0_202\jre\bin\java.exe
//# VM options: -Dfile.encoding=UTF-8
//# Warmup: 5 iterations, 10 s each
//# Measurement: 5 iterations, 10 s each
//# Timeout: 10 min per iteration
//# Threads: 1 thread, will synchronize iterations
//# Benchmark mode: Average time, time/op
//# Benchmark: com.lbq.concurrent.jmh.JMHExample04.testThroughputAndAverageTime
//
//# Run progress: 62.50% complete, ETA 00:05:02
//# Fork: 1 of 1
//# Warmup Iteration   1: 1.791 ms/op
//# Warmup Iteration   2: 1.787 ms/op
//# Warmup Iteration   3: 1.815 ms/op
//# Warmup Iteration   4: 1.787 ms/op
//# Warmup Iteration   5: 1.799 ms/op
//Iteration   1: 1.789 ms/op
//Iteration   2: 1.800 ms/op
//Iteration   3: 1.770 ms/op
//Iteration   4: 1.764 ms/op
//Iteration   5: 1.788 ms/op
//
//
//Result "com.lbq.concurrent.jmh.JMHExample04.testThroughputAndAverageTime":
//  1.782 ±(99.9%) 0.057 ms/op [Average]
//  (min, avg, max) = (1.764, 1.782, 1.800), stdev = 0.015
//  CI (99.9%): [1.725, 1.840] (assumes normal distribution)
//
//
//# JMH version: 1.25
//# VM version: JDK 1.8.0_202, Java HotSpot(TM) 64-Bit Server VM, 25.202-b08
//# VM invoker: D:\DevInstall\Java\jdk1.8.0_202\jre\bin\java.exe
//# VM options: -Dfile.encoding=UTF-8
//# Warmup: 5 iterations, 10 s each
//# Measurement: 5 iterations, 10 s each
//# Timeout: 10 min per iteration
//# Threads: 1 thread, will synchronize iterations
//# Benchmark mode: Sampling time
//# Benchmark: com.lbq.concurrent.jmh.JMHExample04.testAll
//
//# Run progress: 75.00% complete, ETA 00:03:21
//# Fork: 1 of 1
//# Warmup Iteration   1: 1.771 ±(99.9%) 0.019 ms/op
//# Warmup Iteration   2: 1.787 ±(99.9%) 0.016 ms/op
//# Warmup Iteration   3: 1.805 ±(99.9%) 0.016 ms/op
//# Warmup Iteration   4: 1.760 ±(99.9%) 0.017 ms/op
//# Warmup Iteration   5: 1.791 ±(99.9%) 0.016 ms/op
//Iteration   1: 1.786 ±(99.9%) 0.016 ms/op
//                 testAll·p0.00:   0.903 ms/op
//                 testAll·p0.50:   1.978 ms/op
//                 testAll·p0.90:   2.034 ms/op
//                 testAll·p0.95:   2.054 ms/op
//                 testAll·p0.99:   2.075 ms/op
//                 testAll·p0.999:  2.207 ms/op
//                 testAll·p0.9999: 2.253 ms/op
//                 testAll·p1.00:   2.253 ms/op
//
//Iteration   2: 1.810 ±(99.9%) 0.016 ms/op
//                 testAll·p0.00:   0.922 ms/op
//                 testAll·p0.50:   1.987 ms/op
//                 testAll·p0.90:   2.038 ms/op
//                 testAll·p0.95:   2.056 ms/op
//                 testAll·p0.99:   2.073 ms/op
//                 testAll·p0.999:  2.208 ms/op
//                 testAll·p0.9999: 6.078 ms/op
//                 testAll·p1.00:   6.078 ms/op
//
//Iteration   3: 1.825 ±(99.9%) 0.016 ms/op
//                 testAll·p0.00:   0.815 ms/op
//                 testAll·p0.50:   1.987 ms/op
//                 testAll·p0.90:   2.042 ms/op
//                 testAll·p0.95:   2.058 ms/op
//                 testAll·p0.99:   2.073 ms/op
//                 testAll·p0.999:  2.142 ms/op
//                 testAll·p0.9999: 2.261 ms/op
//                 testAll·p1.00:   2.261 ms/op
//
//Iteration   4: 1.834 ±(99.9%) 0.015 ms/op
//                 testAll·p0.00:   0.923 ms/op
//                 testAll·p0.50:   1.985 ms/op
//                 testAll·p0.90:   2.042 ms/op
//                 testAll·p0.95:   2.058 ms/op
//                 testAll·p0.99:   2.076 ms/op
//                 testAll·p0.999:  2.173 ms/op
//                 testAll·p0.9999: 2.241 ms/op
//                 testAll·p1.00:   2.241 ms/op
//
//Iteration   5: 1.828 ±(99.9%) 0.016 ms/op
//                 testAll·p0.00:   0.929 ms/op
//                 testAll·p0.50:   1.987 ms/op
//                 testAll·p0.90:   2.044 ms/op
//                 testAll·p0.95:   2.060 ms/op
//                 testAll·p0.99:   2.077 ms/op
//                 testAll·p0.999:  2.178 ms/op
//                 testAll·p0.9999: 2.343 ms/op
//                 testAll·p1.00:   2.343 ms/op
//
//
//
//Result "com.lbq.concurrent.jmh.JMHExample04.testAll":
//  N = 27480
//  mean =      1.817 ±(99.9%) 0.007 ms/op
//
//  Histogram, ms/op:
//    [0.000, 0.500) = 0 
//    [0.500, 1.000) = 848 
//    [1.000, 1.500) = 4350 
//    [1.500, 2.000) = 15773 
//    [2.000, 2.500) = 6507 
//    [2.500, 3.000) = 1 
//    [3.000, 3.500) = 0 
//    [3.500, 4.000) = 0 
//    [4.000, 4.500) = 0 
//    [4.500, 5.000) = 0 
//    [5.000, 5.500) = 0 
//    [5.500, 6.000) = 0 
//    [6.000, 6.500) = 1 
//
//  Percentiles, ms/op:
//      p(0.0000) =      0.815 ms/op
//     p(50.0000) =      1.985 ms/op
//     p(90.0000) =      2.040 ms/op
//     p(95.0000) =      2.058 ms/op
//     p(99.0000) =      2.075 ms/op
//     p(99.9000) =      2.191 ms/op
//     p(99.9900) =      2.475 ms/op
//     p(99.9990) =      6.078 ms/op
//     p(99.9999) =      6.078 ms/op
//    p(100.0000) =      6.078 ms/op
//
//
//# JMH version: 1.25
//# VM version: JDK 1.8.0_202, Java HotSpot(TM) 64-Bit Server VM, 25.202-b08
//# VM invoker: D:\DevInstall\Java\jdk1.8.0_202\jre\bin\java.exe
//# VM options: -Dfile.encoding=UTF-8
//# Warmup: 5 iterations, 10 s each
//# Measurement: 5 iterations, 10 s each
//# Timeout: 10 min per iteration
//# Threads: 1 thread, will synchronize iterations
//# Benchmark mode: Sampling time
//# Benchmark: com.lbq.concurrent.jmh.JMHExample04.testSampleTime
//
//# Run progress: 87.50% complete, ETA 00:01:40
//# Fork: 1 of 1
//# Warmup Iteration   1: 1.833 ±(99.9%) 0.017 ms/op
//# Warmup Iteration   2: 1.837 ±(99.9%) 0.015 ms/op
//# Warmup Iteration   3: 1.845 ±(99.9%) 0.015 ms/op
//# Warmup Iteration   4: 1.836 ±(99.9%) 0.015 ms/op
//# Warmup Iteration   5: 1.832 ±(99.9%) 0.015 ms/op
//Iteration   1: 1.839 ±(99.9%) 0.015 ms/op
//                 testSampleTime·p0.00:   0.927 ms/op
//                 testSampleTime·p0.50:   1.980 ms/op
//                 testSampleTime·p0.90:   2.036 ms/op
//                 testSampleTime·p0.95:   2.050 ms/op
//                 testSampleTime·p0.99:   2.068 ms/op
//                 testSampleTime·p0.999:  2.187 ms/op
//                 testSampleTime·p0.9999: 2.744 ms/op
//                 testSampleTime·p1.00:   2.744 ms/op
//
//Iteration   2: 1.831 ±(99.9%) 0.016 ms/op
//                 testSampleTime·p0.00:   0.925 ms/op
//                 testSampleTime·p0.50:   1.987 ms/op
//                 testSampleTime·p0.90:   2.040 ms/op
//                 testSampleTime·p0.95:   2.056 ms/op
//                 testSampleTime·p0.99:   2.073 ms/op
//                 testSampleTime·p0.999:  2.149 ms/op
//                 testSampleTime·p0.9999: 2.253 ms/op
//                 testSampleTime·p1.00:   2.253 ms/op
//
//Iteration   3: 1.823 ±(99.9%) 0.016 ms/op
//                 testSampleTime·p0.00:   0.614 ms/op
//                 testSampleTime·p0.50:   1.985 ms/op
//                 testSampleTime·p0.90:   2.044 ms/op
//                 testSampleTime·p0.95:   2.056 ms/op
//                 testSampleTime·p0.99:   2.075 ms/op
//                 testSampleTime·p0.999:  2.204 ms/op
//                 testSampleTime·p0.9999: 2.265 ms/op
//                 testSampleTime·p1.00:   2.265 ms/op
//
//Iteration   4: 1.839 ±(99.9%) 0.015 ms/op
//                 testSampleTime·p0.00:   0.935 ms/op
//                 testSampleTime·p0.50:   1.987 ms/op
//                 testSampleTime·p0.90:   2.044 ms/op
//                 testSampleTime·p0.95:   2.058 ms/op
//                 testSampleTime·p0.99:   2.077 ms/op
//                 testSampleTime·p0.999:  2.153 ms/op
//                 testSampleTime·p0.9999: 2.249 ms/op
//                 testSampleTime·p1.00:   2.249 ms/op
//
//Iteration   5: 1.828 ±(99.9%) 0.016 ms/op
//                 testSampleTime·p0.00:   0.906 ms/op
//                 testSampleTime·p0.50:   1.985 ms/op
//                 testSampleTime·p0.90:   2.044 ms/op
//                 testSampleTime·p0.95:   2.056 ms/op
//                 testSampleTime·p0.99:   2.075 ms/op
//                 testSampleTime·p0.999:  2.124 ms/op
//                 testSampleTime·p0.9999: 2.478 ms/op
//                 testSampleTime·p1.00:   2.478 ms/op
//
//
//
//Result "com.lbq.concurrent.jmh.JMHExample04.testSampleTime":
//  N = 27243
//  mean =      1.832 ±(99.9%) 0.007 ms/op
//
//  Histogram, ms/op:
//    [0.000, 0.250) = 0 
//    [0.250, 0.500) = 0 
//    [0.500, 0.750) = 1 
//    [0.750, 1.000) = 904 
//    [1.000, 1.250) = 3208 
//    [1.250, 1.500) = 555 
//    [1.500, 1.750) = 84 
//    [1.750, 2.000) = 15883 
//    [2.000, 2.250) = 6602 
//    [2.250, 2.500) = 5 
//    [2.500, 2.750) = 1 
//
//  Percentiles, ms/op:
//      p(0.0000) =      0.614 ms/op
//     p(50.0000) =      1.985 ms/op
//     p(90.0000) =      2.042 ms/op
//     p(95.0000) =      2.056 ms/op
//     p(99.0000) =      2.075 ms/op
//     p(99.9000) =      2.166 ms/op
//     p(99.9900) =      2.330 ms/op
//     p(99.9990) =      2.744 ms/op
//     p(99.9999) =      2.744 ms/op
//    p(100.0000) =      2.744 ms/op
//
//
//# JMH version: 1.25
//# VM version: JDK 1.8.0_202, Java HotSpot(TM) 64-Bit Server VM, 25.202-b08
//# VM invoker: D:\DevInstall\Java\jdk1.8.0_202\jre\bin\java.exe
//# VM options: -Dfile.encoding=UTF-8
//# Warmup: <none>
//# Measurement: 1 iterations, single-shot each
//# Timeout: 10 min per iteration
//# Threads: 1 thread
//# Benchmark mode: Single shot invocation time
//# Benchmark: com.lbq.concurrent.jmh.JMHExample04.testAll
//
//# Run progress: 100.00% complete, ETA 00:00:00
//# Fork: 1 of 1
//Iteration   1: 2.154 ms/op
//
//
//
//# JMH version: 1.25
//# VM version: JDK 1.8.0_202, Java HotSpot(TM) 64-Bit Server VM, 25.202-b08
//# VM invoker: D:\DevInstall\Java\jdk1.8.0_202\jre\bin\java.exe
//# VM options: -Dfile.encoding=UTF-8
//# Warmup: <none>
//# Measurement: 1 iterations, single-shot each
//# Timeout: 10 min per iteration
//# Threads: 1 thread
//# Benchmark mode: Single shot invocation time
//# Benchmark: com.lbq.concurrent.jmh.JMHExample04.testSingleShotTime
//
//# Run progress: 100.00% complete, ETA 00:00:00
//# Fork: 1 of 1
//Iteration   1: 1.137 ms/op
//
//
//
//# Run complete. Total time: 00:13:27
//
//REMEMBER: The numbers below are just data. To gain reusable insights, you need to follow up on
//why the numbers are the way they are. Use profilers (see -prof, -lprof), design factorial
//experiments, perform baseline and negative tests that provide experimental control, make sure
//the benchmarking environment is safe on JVM/OS/HW level, ask for reviews from the domain experts.
//Do not assume the numbers tell you what you want them to tell.
//
//Benchmark                                             Mode    Cnt  Score   Error   Units
//JMHExample04.testAll                                 thrpt      5  0.555 ± 0.024  ops/ms
//JMHExample04.testThroughput                          thrpt      5  0.563 ± 0.008  ops/ms
//JMHExample04.testThroughputAndAverageTime            thrpt      5  0.561 ± 0.012  ops/ms
//JMHExample04.testAll                                  avgt      5  1.836 ± 0.113   ms/op
//JMHExample04.testAverageTime                          avgt      5  1.787 ± 0.077   ms/op
//JMHExample04.testThroughputAndAverageTime             avgt      5  1.782 ± 0.057   ms/op
//JMHExample04.testAll                                sample  27480  1.817 ± 0.007   ms/op
//JMHExample04.testAll:testAll·p0.00                  sample         0.815           ms/op
//JMHExample04.testAll:testAll·p0.50                  sample         1.985           ms/op
//JMHExample04.testAll:testAll·p0.90                  sample         2.040           ms/op
//JMHExample04.testAll:testAll·p0.95                  sample         2.058           ms/op
//JMHExample04.testAll:testAll·p0.99                  sample         2.075           ms/op
//JMHExample04.testAll:testAll·p0.999                 sample         2.191           ms/op
//JMHExample04.testAll:testAll·p0.9999                sample         2.475           ms/op
//JMHExample04.testAll:testAll·p1.00                  sample         6.078           ms/op
//JMHExample04.testSampleTime                         sample  27243  1.832 ± 0.007   ms/op
//JMHExample04.testSampleTime:testSampleTime·p0.00    sample         0.614           ms/op
//JMHExample04.testSampleTime:testSampleTime·p0.50    sample         1.985           ms/op
//JMHExample04.testSampleTime:testSampleTime·p0.90    sample         2.042           ms/op
//JMHExample04.testSampleTime:testSampleTime·p0.95    sample         2.056           ms/op
//JMHExample04.testSampleTime:testSampleTime·p0.99    sample         2.075           ms/op
//JMHExample04.testSampleTime:testSampleTime·p0.999   sample         2.166           ms/op
//JMHExample04.testSampleTime:testSampleTime·p0.9999  sample         2.330           ms/op
//JMHExample04.testSampleTime:testSampleTime·p1.00    sample         2.744           ms/op
//JMHExample04.testAll                                    ss         2.154           ms/op
//JMHExample04.testSingleShotTime                         ss         1.137           ms/op

