package com.lbq.concurrent.jmh;

import java.util.Collections;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.TimeUnit;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
/**
 * 2.使用@Param
 * 正如本节开始时所说的那样，Java提供的具体线程安全的Map接口实现并非只有ConcurrentHashMap和SynchronizedMap，
 * 同样，ConcurrentSkipListMap和HashMap也可供我们选择，如果我们要对其进行测试，那么这里需要再增加两个不同类型的Map
 * 和两个针对这两个Map实现的基准测试方法。但是很显然，这种方式存在大量的代码冗余，因此JMH为我们提供了一个@Param的注解，
 * 它使得参数可配置，也就是说一个参数在每一次的基准测试时都会有不同的值与之对应。
 * 
 * 如下述代码所示，由于引进了@Param对变量的可配置化，因此我们只需要写一个基准测试方法即可，
 * JMH会根据@Param所提供的参数值，对test方法分别进行基准测试的运行与统计，
 * 这样我们就不需要为每一个map容器都写一个基准测试方法了。
 * 
 * 在setUp方法中，我们分别实例化了四种不同类型的Map实现类，分别对应于@Param的不同参数。
 * 
 * 运行下面的基准测试，我们会发现输出结果中多了type这样一列信息。
 * Benchmark          (type)  Mode  Cnt   Score     Error  Units
 * JMHExample10.test       1  avgt    5  10.319 ±  17.667  us/op
 * JMHExample10.test       2  avgt    5  12.164 ±  48.497  us/op
 * JMHExample10.test       3  avgt    5  12.176 ±  34.964  us/op
 * JMHExample10.test       4  avgt    5  42.655 ± 173.276  us/op
 * 
 * 在本节中我们对所有线程安全Map的基准测试都是基于put方法进行的，也就是说并没有同时进行读写、修改、删除等动作，
 * 因此单凭对一个方法的基准测试就下定论说哪个性能好，哪个性能不好这种说法是不够严谨的，希望读者能够注意到这一点。
 * 
 * @author 14378
 *
 */
@BenchmarkMode(Mode.AverageTime)
@Fork(1)
@Warmup(iterations = 5)
@Measurement(iterations = 5)
@OutputTimeUnit(TimeUnit.MICROSECONDS)
//5个线程通知对共享资源进行操作
@Threads(5)
//多个线程使用同一个实例
@State(Scope.Benchmark)
public class JMHExample10 {
	//为type提供了四种可配置的参数值
	@Param({"1", "2", "3", "4"})
	private int type;
	
	private Map<Long, Long> map;
	@Setup
	public void setUp() {
		switch(type) {
			case 1:
				this.map = new ConcurrentHashMap<>();
				break;
			case 2:
				this.map = new ConcurrentSkipListMap<>();
				break;
			case 3:
				this.map = new Hashtable<>();
				break;
			case 4:
				this.map = Collections.synchronizedMap(new HashMap<>());
				break;
			default:
				throw new IllegalArgumentException("Illegal map type.");
		}
	}
	//只需要一个基准测试方法即可
	@Benchmark
	public void test() {
		this.map.put(System.nanoTime(), System.nanoTime());
	}
	
	public static void main(String[] args) throws RunnerException {
		final Options opts = new OptionsBuilder()
				.include(JMHExample10.class.getSimpleName())
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
//# Threads: 5 threads, will synchronize iterations
//# Benchmark mode: Average time, time/op
//# Benchmark: com.lbq.concurrent.jmh.JMHExample10.test
//# Parameters: (type = 1)
//
//# Run progress: 0.00% complete, ETA 00:00:40
//# Fork: 1 of 1
//# Warmup Iteration   1: 3.034 ±(99.9%) 1.511 us/op
//# Warmup Iteration   2: 3.721 ±(99.9%) 0.519 us/op
//# Warmup Iteration   3: 1.528 ±(99.9%) 0.208 us/op
//# Warmup Iteration   4: 241.119 ±(99.9%) 15.576 us/op
//# Warmup Iteration   5: 1.206 ±(99.9%) 0.056 us/op
//Iteration   1: 6.375 ±(99.9%) 0.625 us/op
//Iteration   2: 6.629 ±(99.9%) 3.458 us/op
//Iteration   3: 17.313 ±(99.9%) 1.087 us/op
//Iteration   4: 8.921 ±(99.9%) 0.913 us/op
//Iteration   5: 12.358 ±(99.9%) 1.112 us/op
//
//
//Result "com.lbq.concurrent.jmh.JMHExample10.test":
//  10.319 ±(99.9%) 17.667 us/op [Average]
//  (min, avg, max) = (6.375, 10.319, 17.313), stdev = 4.588
//  CI (99.9%): [≈ 0, 27.986] (assumes normal distribution)
//
//
//# JMH version: 1.19
//# VM version: JDK 1.8.0_202, VM 25.202-b08
//# VM invoker: D:\DevInstall\Java\jdk1.8.0_202\jre\bin\java.exe
//# VM options: -Dfile.encoding=UTF-8
//# Warmup: 5 iterations, 1 s each
//# Measurement: 5 iterations, 1 s each
//# Timeout: 10 min per iteration
//# Threads: 5 threads, will synchronize iterations
//# Benchmark mode: Average time, time/op
//# Benchmark: com.lbq.concurrent.jmh.JMHExample10.test
//# Parameters: (type = 2)
//
//# Run progress: 25.00% complete, ETA 00:01:42
//# Fork: 1 of 1
//# Warmup Iteration   1: 5.083 ±(99.9%) 0.543 us/op
//# Warmup Iteration   2: 7.460 ±(99.9%) 0.467 us/op
//# Warmup Iteration   3: 5.147 ±(99.9%) 0.592 us/op
//# Warmup Iteration   4: 1.804 ±(99.9%) 0.136 us/op
//# Warmup Iteration   5: 35.035 ±(99.9%) 3.188 us/op
//Iteration   1: 8.071 ±(99.9%) 0.954 us/op
//Iteration   2: 1.245 ±(99.9%) 0.127 us/op
//Iteration   3: 11.732 ±(99.9%) 1.569 us/op
//Iteration   4: 6.118 ±(99.9%) 0.344 us/op
//Iteration   5: 33.653 ±(99.9%) 18.966 us/op
//
//
//Result "com.lbq.concurrent.jmh.JMHExample10.test":
//  12.164 ±(99.9%) 48.497 us/op [Average]
//  (min, avg, max) = (1.245, 12.164, 33.653), stdev = 12.595
//  CI (99.9%): [≈ 0, 60.661] (assumes normal distribution)
//
//
//# JMH version: 1.19
//# VM version: JDK 1.8.0_202, VM 25.202-b08
//# VM invoker: D:\DevInstall\Java\jdk1.8.0_202\jre\bin\java.exe
//# VM options: -Dfile.encoding=UTF-8
//# Warmup: 5 iterations, 1 s each
//# Measurement: 5 iterations, 1 s each
//# Timeout: 10 min per iteration
//# Threads: 5 threads, will synchronize iterations
//# Benchmark mode: Average time, time/op
//# Benchmark: com.lbq.concurrent.jmh.JMHExample10.test
//# Parameters: (type = 3)
//
//# Run progress: 50.00% complete, ETA 00:01:14
//# Fork: 1 of 1
//# Warmup Iteration   1: 3.792 ±(99.9%) 2.989 us/op
//# Warmup Iteration   2: 5.196 ±(99.9%) 3.935 us/op
//# Warmup Iteration   3: 29.787 ±(99.9%) 28.197 us/op
//# Warmup Iteration   4: 2.736 ±(99.9%) 2.114 us/op
//# Warmup Iteration   5: 5.575 ±(99.9%) 4.022 us/op
//Iteration   1: 6.074 ±(99.9%) 8.944 us/op
//Iteration   2: 23.390 ±(99.9%) 14.394 us/op
//Iteration   3: 6.240 ±(99.9%) 4.834 us/op
//Iteration   4: 20.690 ±(99.9%) 4.763 us/op
//Iteration   5: 4.489 ±(99.9%) 3.156 us/op
//
//
//Result "com.lbq.concurrent.jmh.JMHExample10.test":
//  12.176 ±(99.9%) 34.964 us/op [Average]
//  (min, avg, max) = (4.489, 12.176, 23.390), stdev = 9.080
//  CI (99.9%): [≈ 0, 47.140] (assumes normal distribution)
//
//
//# JMH version: 1.19
//# VM version: JDK 1.8.0_202, VM 25.202-b08
//# VM invoker: D:\DevInstall\Java\jdk1.8.0_202\jre\bin\java.exe
//# VM options: -Dfile.encoding=UTF-8
//# Warmup: 5 iterations, 1 s each
//# Measurement: 5 iterations, 1 s each
//# Timeout: 10 min per iteration
//# Threads: 5 threads, will synchronize iterations
//# Benchmark mode: Average time, time/op
//# Benchmark: com.lbq.concurrent.jmh.JMHExample10.test
//# Parameters: (type = 4)
//
//# Run progress: 75.00% complete, ETA 00:00:31
//# Fork: 1 of 1
//# Warmup Iteration   1: 5.828 ±(99.9%) 6.313 us/op
//# Warmup Iteration   2: 5.662 ±(99.9%) 6.658 us/op
//# Warmup Iteration   3: 9.359 ±(99.9%) 6.933 us/op
//# Warmup Iteration   4: 2.486 ±(99.9%) 0.818 us/op
//# Warmup Iteration   5: 5.239 ±(99.9%) 3.091 us/op
//Iteration   1: 106.789 ±(99.9%) 422.789 us/op
//Iteration   2: 70.481 ±(99.9%) 146.927 us/op
//Iteration   3: 4.843 ±(99.9%) 4.727 us/op
//Iteration   4: 28.166 ±(99.9%) 35.699 us/op
//Iteration   5: 2.998 ±(99.9%) 2.670 us/op
//
//
//Result "com.lbq.concurrent.jmh.JMHExample10.test":
//  42.655 ±(99.9%) 173.276 us/op [Average]
//  (min, avg, max) = (2.998, 42.655, 106.789), stdev = 44.999
//  CI (99.9%): [≈ 0, 215.931] (assumes normal distribution)
//
//
//# Run complete. Total time: 00:01:54
//
//Benchmark          (type)  Mode  Cnt   Score     Error  Units
//JMHExample10.test       1  avgt    5  10.319 ±  17.667  us/op
//JMHExample10.test       2  avgt    5  12.164 ±  48.497  us/op
//JMHExample10.test       3  avgt    5  12.176 ±  34.964  us/op
//JMHExample10.test       4  avgt    5  42.655 ± 173.276  us/op
