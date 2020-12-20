package com.lbq.concurrent.jmh;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.concurrent.TimeUnit;
/**
 * 2.Thread共享的State
 * 有时候，我们需要测试在多线程的情况下某个类被不同线程操作时的性能，比如，多线程访问某个共享数据时，我们需要让多个线程使用同一个实例才可以。
 * 因此JMH提供了多线程共享的一种状态Scope.Benchmark，下面来看看具体的示例。
 * 
 * 运行上面的程序，我们会看到“create instance”字样只出现了1次。
 * @author 14378
 *
 */
@BenchmarkMode(Mode.AverageTime)
@Fork(1)
@Warmup(iterations = 5)
@Measurement(iterations = 10)
@OutputTimeUnit(TimeUnit.MICROSECONDS)
//设置5个线程运行基准测试方法
@Threads(5)
public class JMHExample07 {

	//Test的实例将会被多个线程共享，也就是说只有一份Test的实例
	@State(Scope.Benchmark)
	public static class Test {
		public Test() {
			System.out.println("create instance");
		}
		
		public void method() {
			
		}
	}
	
	//通过基准测试将State引用传入
	@Benchmark
	public void test(Test test) {
		test.method();
	}
	
	public static void main(String[] args) throws RunnerException {
		final Options opts = new OptionsBuilder()
				.include(JMHExample07.class.getSimpleName())
				.build();
		new Runner(opts).run();

	}

}

//# JMH version: 1.25
//# VM version: JDK 1.8.0_202, Java HotSpot(TM) 64-Bit Server VM, 25.202-b08
//# VM invoker: D:\DevInstall\Java\jdk1.8.0_202\jre\bin\java.exe
//# VM options: -Dfile.encoding=UTF-8
//# Warmup: 5 iterations, 10 s each
//# Measurement: 10 iterations, 10 s each
//# Timeout: 10 min per iteration
//# Threads: 5 threads, will synchronize iterations
//# Benchmark mode: Average time, time/op
//# Benchmark: com.lbq.concurrent.jmh.JMHExample07.test
//
//# Run progress: 0.00% complete, ETA 00:02:30
//# Fork: 1 of 1
//# Warmup Iteration   1: create instance
//0.001 ±(99.9%) 0.001 us/op
//# Warmup Iteration   2: 0.001 ±(99.9%) 0.001 us/op
//# Warmup Iteration   3: 0.001 ±(99.9%) 0.001 us/op
//# Warmup Iteration   4: 0.001 ±(99.9%) 0.001 us/op
//# Warmup Iteration   5: 0.001 ±(99.9%) 0.001 us/op
//Iteration   1: 0.001 ±(99.9%) 0.001 us/op
//Iteration   2: 0.001 ±(99.9%) 0.001 us/op
//Iteration   3: 0.001 ±(99.9%) 0.001 us/op
//Iteration   4: 0.001 ±(99.9%) 0.001 us/op
//Iteration   5: 0.001 ±(99.9%) 0.001 us/op
//Iteration   6: 0.001 ±(99.9%) 0.001 us/op
//Iteration   7: 0.001 ±(99.9%) 0.001 us/op
//Iteration   8: 0.001 ±(99.9%) 0.001 us/op
//Iteration   9: 0.001 ±(99.9%) 0.001 us/op
//Iteration  10: 0.001 ±(99.9%) 0.001 us/op
//
//
//Result "com.lbq.concurrent.jmh.JMHExample07.test":
//0.001 ±(99.9%) 0.001 us/op [Average]
//(min, avg, max) = (0.001, 0.001, 0.001), stdev = 0.001
//CI (99.9%): [0.001, 0.001] (assumes normal distribution)
//
//
//# Run complete. Total time: 00:02:30
//
//REMEMBER: The numbers below are just data. To gain reusable insights, you need to follow up on
//why the numbers are the way they are. Use profilers (see -prof, -lprof), design factorial
//experiments, perform baseline and negative tests that provide experimental control, make sure
//the benchmarking environment is safe on JVM/OS/HW level, ask for reviews from the domain experts.
//Do not assume the numbers tell you what you want them to tell.
//
//Benchmark          Mode  Cnt  Score    Error  Units
//JMHExample07.test  avgt   10  0.001 ±  0.001  us/op
