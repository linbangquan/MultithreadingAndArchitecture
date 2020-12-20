package com.lbq.concurrent.jmh;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
/**
 * 1.3.6 @Param的妙用
 * 假设你在编写代码的过程中需要用到一个Map容器，第一，需要保证使用过程中线程的安全性，第二，该容器需要有比较好的性能，比如，执行put方法最快，执行get方法最快等。
 * 作为Java程序员，JDK可提供我们选择的方案其实有不少，比如ConcurrentHashMap、Hashtable、ConcurrentSkipListMap以及SynchronizedMap等，
 * 虽然它们都能够保证在多线程操作下的数据一致性，但是各自的性能表现又是怎样的呢?这就需要我们对其进行微基准测试(我们的测试相对来说比较片面，
 * 只在多线程的情况下对其进行put操作，也就是说并未涉及读取以及删除的操作)。
 * 
 * 1.对比ConcurrentHashMap和SynchronizedMap的性能
 * 根据前面所学的知识，我们只需要写两个基准测试方法，其中第一个针对ConcurrentHashMap，第二个针对SynchronizedMap即可，代码如下所示。
 * 
 * @author 14378
 *
 */
@BenchmarkMode(Mode.AverageTime)
@Fork(1)
@Warmup(iterations = 5)
@Measurement(iterations = 10)
@OutputTimeUnit(TimeUnit.MICROSECONDS)
//5个线程同时对共享资源进行操作
@Threads(5)
//设置为线程间共享的资源
@State(Scope.Benchmark)
public class JMHExample09 {

	private Map<Long, Long> concurrentMap;
	private Map<Long, Long> synchronizedMap;
	
	//关于Setup详见1.3.7节
	@Setup
	public void setUp() {
		concurrentMap = new ConcurrentHashMap<>();
		synchronizedMap = Collections.synchronizedMap(new HashMap<>());
	}
	
	@Benchmark
	public void testConcurrencyMap() {
		this.concurrentMap.put(System.nanoTime(), System.nanoTime());
	}
	
	@Benchmark
	public void testSynchronizedMap() {
		this.synchronizedMap.put(System.nanoTime(), System.nanoTime());
	}
	public static void main(String[] args) throws RunnerException {
		final Options opts = new OptionsBuilder()
				.include(JMHExample09.class.getSimpleName())
				.build();
		new Runner(opts).run();
	}

}

//# JMH version: 1.19
//# VM version: JDK 1.8.0_202, VM 25.202-b08
//# VM invoker: D:\DevInstall\Java\jdk1.8.0_202\jre\bin\java.exe
//# VM options: -Dfile.encoding=UTF-8
//# Warmup: 5 iterations, 1 s each
//# Measurement: 10 iterations, 1 s each
//# Timeout: 10 min per iteration
//# Threads: 5 threads, will synchronize iterations
//# Benchmark mode: Average time, time/op
//# Benchmark: com.lbq.concurrent.jmh.JMHExample09.testConcurrencyMap
//
//# Run progress: 0.00% complete, ETA 00:00:30
//# Fork: 1 of 1
//# Warmup Iteration   1: 2.531 ±(99.9%) 0.275 us/op
//# Warmup Iteration   2: 4.191 ±(99.9%) 4.040 us/op
//# Warmup Iteration   3: 1.528 ±(99.9%) 1.232 us/op
//# Warmup Iteration   4: 22.110 ±(99.9%) 1.727 us/op
//# Warmup Iteration   5: 8.557 ±(99.9%) 2.824 us/op
//Iteration   1: 3.365 ±(99.9%) 0.451 us/op
//Iteration   2: 11.366 ±(99.9%) 0.820 us/op
//Iteration   3: 1.442 ±(99.9%) 0.677 us/op
//Iteration   4: 41.760 ±(99.9%) 7.974 us/op
//Iteration   5: 8.377 ±(99.9%) 0.598 us/op
//Iteration   6: 10.079 ±(99.9%) 0.391 us/op
//Iteration   7: 39.921 ±(99.9%) 4.722 us/op
//Iteration   8: 106.362 ±(99.9%) 18.817 us/op
//Iteration   9: 323.956 ±(99.9%) 96.021 us/op
//Iteration  10: 1029.821 ±(99.9%) 500.055 us/op
//
//
//Result "com.lbq.concurrent.jmh.JMHExample09.testConcurrencyMap":
//  157.645 ±(99.9%) 486.497 us/op [Average]
//  (min, avg, max) = (1.442, 157.645, 1029.821), stdev = 321.787
//  CI (99.9%): [≈ 0, 644.141] (assumes normal distribution)
//
//
//# JMH version: 1.19
//# VM version: JDK 1.8.0_202, VM 25.202-b08
//# VM invoker: D:\DevInstall\Java\jdk1.8.0_202\jre\bin\java.exe
//# VM options: -Dfile.encoding=UTF-8
//# Warmup: 5 iterations, 1 s each
//# Measurement: 10 iterations, 1 s each
//# Timeout: 10 min per iteration
//# Threads: 5 threads, will synchronize iterations
//# Benchmark mode: Average time, time/op
//# Benchmark: com.lbq.concurrent.jmh.JMHExample09.testSynchronizedMap
//
//# Run progress: 50.00% complete, ETA 00:00:56
//# Fork: 1 of 1
//# Warmup Iteration   1: 5.760 ±(99.9%) 5.460 us/op
//# Warmup Iteration   2: 7.682 ±(99.9%) 4.546 us/op
//# Warmup Iteration   3: 2.571 ±(99.9%) 3.223 us/op
//# Warmup Iteration   4: 13.962 ±(99.9%) 17.224 us/op
//# Warmup Iteration   5: 2.852 ±(99.9%) 2.041 us/op
//Iteration   1: 13.412 ±(99.9%) 56.028 us/op
//Iteration   2: 30.932 ±(99.9%) 91.022 us/op
//Iteration   3: 7.728 ±(99.9%) 13.754 us/op
//Iteration   4: 32.649 ±(99.9%) 23.006 us/op
//Iteration   5: 28.697 ±(99.9%) 43.585 us/op
//Iteration   6: 2.173 ±(99.9%) 3.579 us/op
//Iteration   7: 103.666 ±(99.9%) 268.995 us/op
//Iteration   8: 63.755 ±(99.9%) 109.847 us/op
//Iteration   9: 3143678.232 ±(99.9%) 15346158.694 us/op
//Iteration  10: 236987.643 ±(99.9%) 725879.341 us/op
//
//
//Result "com.lbq.concurrent.jmh.JMHExample09.testSynchronizedMap":
//  338094.889 ±(99.9%) 1494607.249 us/op [Average]
//  (min, avg, max) = (2.173, 338094.889, 3143678.232), stdev = 988590.155
//  CI (99.9%): [≈ 0, 1832702.138] (assumes normal distribution)
//
//
//# Run complete. Total time: 00:03:36
//
//Benchmark                         Mode  Cnt       Score         Error  Units
//JMHExample09.testConcurrencyMap   avgt   10     157.645 ±     486.497  us/op
//JMHExample09.testSynchronizedMap  avgt   10  338094.889 ± 1494607.249  us/op