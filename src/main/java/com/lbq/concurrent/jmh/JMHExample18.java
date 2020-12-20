package com.lbq.concurrent.jmh;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

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
/**
 * 1.4.2 一些高级的用法
 * 1.Asymmetric Benchmark
 * 除了1.3.5节之外，我们编写的所有基准测试都会被JMH框架根据方法名字的字典顺序排序之后串行执行，然而有些时候我们会想要对某个类的读写方法并行执行，
 * 比如，我们想要在修改某个原子变量的时候又有其他线程对其进行读取操作，下面来看一个示例代码。
 * 
 * 我们在对AtomicInteger进行自增操作的同时又会对其进行读取操作，这就是我们经常见到的高并发环境中某些API的操作方式，同样也是线程安全存在隐患的地方。
 * 下面我们来看看5个线程对AtomicInteger执行自增操作，5个线程对AtomicInteger执行读取时的性能是怎样的。
 * Benchmark           Mode  Cnt  Score   Error  Units
 * JMHExample18.q      avgt    5  0.160 ± 0.046  us/op
 * JMHExample18.q:get  avgt    5  0.107 ± 0.095  us/op
 * JMHExample18.q:inc  avgt    5  0.213 ± 0.013  us/op
 * 输出说明：
 * 1.group q (5个读线程，5个写线程)的平均响应时间为0.160us，误差为0.046。
 * 2.group q (5个读线程) 同时读取atomicinteger变量的速度为0.107us，误差为0.095。
 * 3.group q (5个写线程) 同时修改atomicinteger变量的速度为0.213us，误差为0.013。
 * @author 14378
 *
 */
@BenchmarkMode(Mode.AverageTime)
@Fork(1)
@Warmup(iterations = 5)
@Measurement(iterations = 5)
@OutputTimeUnit(TimeUnit.MICROSECONDS)
@State(Scope.Group)
public class JMHExample18 {

	private AtomicInteger counter;
	
	@Setup
	public void init() {
		this.counter = new AtomicInteger();
	}
	
	@GroupThreads(5)
	@Group("q")
	@Benchmark
	public void inc() {
		this.counter.incrementAndGet();
	}
	@GroupThreads(5)
	@Group("q")
	@Benchmark
	public int get() {
		return this.counter.get();
	}
	public static void main(String[] args) throws RunnerException {
		final Options opts = new OptionsBuilder()
				.include(JMHExample18.class.getSimpleName())
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
//# Threads: 10 threads (1 group; 5x "get", 5x "inc" in each group), will synchronize iterations
//# Benchmark mode: Average time, time/op
//# Benchmark: com.lbq.concurrent.jmh.JMHExample18.q
//
//# Run progress: 0.00% complete, ETA 00:00:10
//# Fork: 1 of 1
//# Warmup Iteration   1: 0.161 ±(99.9%) 0.095 us/op
//# Warmup Iteration   2: 0.168 ±(99.9%) 0.079 us/op
//# Warmup Iteration   3: 0.158 ±(99.9%) 0.106 us/op
//# Warmup Iteration   4: 0.149 ±(99.9%) 0.100 us/op
//# Warmup Iteration   5: 0.156 ±(99.9%) 0.082 us/op
//Iteration   1: 0.162 ±(99.9%) 0.107 us/op
//                 get: 0.106 ±(99.9%) 0.061 us/op
//                 inc: 0.218 ±(99.9%) 0.217 us/op
//
//Iteration   2: 0.152 ±(99.9%) 0.098 us/op
//                 get: 0.091 ±(99.9%) 0.063 us/op
//                 inc: 0.213 ±(99.9%) 0.019 us/op
//
//Iteration   3: 0.152 ±(99.9%) 0.100 us/op
//                 get: 0.090 ±(99.9%) 0.058 us/op
//                 inc: 0.214 ±(99.9%) 0.022 us/op
//
//Iteration   4: 0.180 ±(99.9%) 0.079 us/op
//                 get: 0.149 ±(99.9%) 0.173 us/op
//                 inc: 0.210 ±(99.9%) 0.162 us/op
//
//Iteration   5: 0.153 ±(99.9%) 0.090 us/op
//                 get: 0.097 ±(99.9%) 0.036 us/op
//                 inc: 0.210 ±(99.9%) 0.016 us/op
//
//
//
//Result "com.lbq.concurrent.jmh.JMHExample18.q":
//  0.160 ±(99.9%) 0.046 us/op [Average]
//  (min, avg, max) = (0.152, 0.160, 0.180), stdev = 0.012
//  CI (99.9%): [0.114, 0.206] (assumes normal distribution)
//
//Secondary result "com.lbq.concurrent.jmh.JMHExample18.q:get":
//  0.107 ±(99.9%) 0.095 us/op [Average]
//  (min, avg, max) = (0.090, 0.107, 0.149), stdev = 0.025
//  CI (99.9%): [0.012, 0.201] (assumes normal distribution)
//
//Secondary result "com.lbq.concurrent.jmh.JMHExample18.q:inc":
//  0.213 ±(99.9%) 0.013 us/op [Average]
//  (min, avg, max) = (0.210, 0.213, 0.218), stdev = 0.003
//  CI (99.9%): [0.200, 0.226] (assumes normal distribution)
//
//
//# Run complete. Total time: 00:00:11
//
//Benchmark           Mode  Cnt  Score   Error  Units
//JMHExample18.q      avgt    5  0.160 ± 0.046  us/op
//JMHExample18.q:get  avgt    5  0.107 ± 0.095  us/op
//JMHExample18.q:inc  avgt    5  0.213 ± 0.013  us/op
