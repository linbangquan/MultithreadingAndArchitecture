package com.lbq.concurrent.jmh;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.concurrent.TimeUnit;
/**
 * 4.避免循环展开(Loop Unwinding)
 * 我们在编写JMH代码的时候，除了要避免Dead Code以及减少对常量的引用之外，还要尽可能地避免或者减少在基准测试方法中出现循环，
 * 因为循环代码在运行阶段(JVM后期优化)极可能被“痛下杀手”进行相关的优化，这种优化被称为循环展开，下面我们来看一下什么是循环展开(Loop Unwinding)。
 * int sum = 0;
 * for(int i = 0; i < 100; i++){
 * 		sum += i;
 * }
 * 上面的例子中，sum=sum+i这样的代码会被执行100次，也就是说，JVM会向CPU发送100次这样的计算指令，这看起来并没有什么，但是JVM的设计者们会认为这样的方式可以被优化成如下形式(可能)。
 * int sum = 0;
 * for(int i = 0; i < 20; i += 5){
 * 		sum += i;
 * 		sum += i + 1;
 * 		sum += i + 2;
 * 		sum += i + 3;
 * 		sum += i + 4;
 * }
 * 优化后将循环体中的计算指令批量发送给CPU，这种批量的方式可以提供计算的效率，假设1+2这样的运算执行一次需要1纳秒的CPU时间，那么在一个10次循环的计算中，我们觉得它可能是10纳秒的CPU时间，
 * 但是真实的计算情况可能不足10纳秒甚至更低，下面来看一下JMH的代码示例。
 * 
 * 在下面的代码中，measure()方法进行了x+y的计算，measureLoop_1()方法与measure()方法几乎是等价的，也是进行了x+y的计算，
 * 但是measureLoop_10()方法对result+=(x+y)进行了10次这样的操作，其实说白了就是调用了10次measure()或者loopCompute(times=1)。
 * 但是我们肯定不能直接拿10次的运算和1次运算所耗费的CPU时间去做比较，因此@OperationsPerInvocation(10)注解的作用
 * 就是在每一次对measureLoop_10()方法进行基准调用的时候将op操作记为10次。
 * 下面来看一下JMH执行后的性能数据。
 * Benchmark                      Mode  Cnt  Score   Error  Units
 * JMHExample16.measure           avgt   10  3.319 ± 0.279  ns/op
 * JMHExample16.measureLoop_1     avgt   10  3.197 ± 0.060  ns/op
 * JMHExample16.measureLoop_10    avgt   10  0.368 ± 0.009  ns/op
 * JMHExample16.measureLoop_100   avgt   10  0.041 ± 0.002  ns/op
 * JMHExample16.measureLoop_1000  avgt   10  0.037 ± 0.001  ns/op
 * 
 * 通过JMH的基准测试我们不难发现，在循环次数多的情况下，折叠的情况也比较多，因此性能会比较好，说明JVM在运行期对我们的代码进行了优化。
 * @author 14378
 *
 */
@BenchmarkMode(Mode.AverageTime)
@State(Scope.Thread)
@Warmup(iterations = 5)
@Measurement(iterations = 10)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
public class JMHExample16 {

	private int x = 1;
	private int y = 2;
	
	@Benchmark
	public int measure() {
		return (x + y);
	}

	private int loopCompute(int times) {
		int result = 0;
		for(int i = 0; i < times; i++) {
			result += (x + y);
		}
		return result;
	}
	@OperationsPerInvocation
	@Benchmark
	public int measureLoop_1() {
		return loopCompute(1);
	}
	@OperationsPerInvocation(10)
	@Benchmark
	public int measureLoop_10() {
		return loopCompute(10);
	}
	@OperationsPerInvocation(100)
	@Benchmark
	public int measureLoop_100() {
		return loopCompute(100);
	}
	@OperationsPerInvocation(1000)
	@Benchmark
	public int measureLoop_1000() {
		return loopCompute(1000);
	}
	
	public static void main(String[] args) throws RunnerException {
		final Options opts = new OptionsBuilder()
				.include(JMHExample16.class.getSimpleName())
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
//# Threads: 1 thread, will synchronize iterations
//# Benchmark mode: Average time, time/op
//# Benchmark: com.lbq.concurrent.jmh.JMHExample16.measure
//
//# Run progress: 0.00% complete, ETA 00:01:15
//# Fork: 1 of 1
//# Warmup Iteration   1: 3.678 ns/op
//# Warmup Iteration   2: 3.366 ns/op
//# Warmup Iteration   3: 3.272 ns/op
//# Warmup Iteration   4: 3.400 ns/op
//# Warmup Iteration   5: 3.399 ns/op
//Iteration   1: 3.487 ns/op
//Iteration   2: 3.203 ns/op
//Iteration   3: 3.314 ns/op
//Iteration   4: 3.192 ns/op
//Iteration   5: 3.201 ns/op
//Iteration   6: 3.313 ns/op
//Iteration   7: 3.766 ns/op
//Iteration   8: 3.196 ns/op
//Iteration   9: 3.331 ns/op
//Iteration  10: 3.182 ns/op
//
//
//Result "com.lbq.concurrent.jmh.JMHExample16.measure":
//  3.319 ±(99.9%) 0.279 ns/op [Average]
//  (min, avg, max) = (3.182, 3.319, 3.766), stdev = 0.184
//  CI (99.9%): [3.040, 3.597] (assumes normal distribution)
//
//
//# JMH version: 1.19
//# VM version: JDK 1.8.0_202, VM 25.202-b08
//# VM invoker: D:\DevInstall\Java\jdk1.8.0_202\jre\bin\java.exe
//# VM options: -Dfile.encoding=UTF-8
//# Warmup: 5 iterations, 1 s each
//# Measurement: 10 iterations, 1 s each
//# Timeout: 10 min per iteration
//# Threads: 1 thread, will synchronize iterations
//# Benchmark mode: Average time, time/op
//# Benchmark: com.lbq.concurrent.jmh.JMHExample16.measureLoop_1
//
//# Run progress: 20.00% complete, ETA 00:01:02
//# Fork: 1 of 1
//# Warmup Iteration   1: 3.541 ns/op
//# Warmup Iteration   2: 3.637 ns/op
//# Warmup Iteration   3: 3.167 ns/op
//# Warmup Iteration   4: 3.242 ns/op
//# Warmup Iteration   5: 3.197 ns/op
//Iteration   1: 3.172 ns/op
//Iteration   2: 3.148 ns/op
//Iteration   3: 3.187 ns/op
//Iteration   4: 3.301 ns/op
//Iteration   5: 3.194 ns/op
//Iteration   6: 3.201 ns/op
//Iteration   7: 3.195 ns/op
//Iteration   8: 3.194 ns/op
//Iteration   9: 3.191 ns/op
//Iteration  10: 3.186 ns/op
//
//
//Result "com.lbq.concurrent.jmh.JMHExample16.measureLoop_1":
//  3.197 ±(99.9%) 0.060 ns/op [Average]
//  (min, avg, max) = (3.148, 3.197, 3.301), stdev = 0.040
//  CI (99.9%): [3.137, 3.257] (assumes normal distribution)
//
//
//# JMH version: 1.19
//# VM version: JDK 1.8.0_202, VM 25.202-b08
//# VM invoker: D:\DevInstall\Java\jdk1.8.0_202\jre\bin\java.exe
//# VM options: -Dfile.encoding=UTF-8
//# Warmup: 5 iterations, 1 s each
//# Measurement: 10 iterations, 1 s each
//# Timeout: 10 min per iteration
//# Threads: 1 thread, will synchronize iterations
//# Benchmark mode: Average time, time/op
//# Benchmark: com.lbq.concurrent.jmh.JMHExample16.measureLoop_10
//
//# Run progress: 40.00% complete, ETA 00:00:46
//# Fork: 1 of 1
//# Warmup Iteration   1: 0.407 ns/op
//# Warmup Iteration   2: 0.393 ns/op
//# Warmup Iteration   3: 0.378 ns/op
//# Warmup Iteration   4: 0.396 ns/op
//# Warmup Iteration   5: 0.358 ns/op
//Iteration   1: 0.360 ns/op
//Iteration   2: 0.373 ns/op
//Iteration   3: 0.372 ns/op
//Iteration   4: 0.369 ns/op
//Iteration   5: 0.367 ns/op
//Iteration   6: 0.375 ns/op
//Iteration   7: 0.371 ns/op
//Iteration   8: 0.362 ns/op
//Iteration   9: 0.357 ns/op
//Iteration  10: 0.373 ns/op
//
//
//Result "com.lbq.concurrent.jmh.JMHExample16.measureLoop_10":
//  0.368 ±(99.9%) 0.009 ns/op [Average]
//  (min, avg, max) = (0.357, 0.368, 0.375), stdev = 0.006
//  CI (99.9%): [0.358, 0.377] (assumes normal distribution)
//
//
//# JMH version: 1.19
//# VM version: JDK 1.8.0_202, VM 25.202-b08
//# VM invoker: D:\DevInstall\Java\jdk1.8.0_202\jre\bin\java.exe
//# VM options: -Dfile.encoding=UTF-8
//# Warmup: 5 iterations, 1 s each
//# Measurement: 10 iterations, 1 s each
//# Timeout: 10 min per iteration
//# Threads: 1 thread, will synchronize iterations
//# Benchmark mode: Average time, time/op
//# Benchmark: com.lbq.concurrent.jmh.JMHExample16.measureLoop_100
//
//# Run progress: 60.00% complete, ETA 00:00:31
//# Fork: 1 of 1
//# Warmup Iteration   1: 0.045 ns/op
//# Warmup Iteration   2: 0.043 ns/op
//# Warmup Iteration   3: 0.040 ns/op
//# Warmup Iteration   4: 0.043 ns/op
//# Warmup Iteration   5: 0.041 ns/op
//Iteration   1: 0.040 ns/op
//Iteration   2: 0.041 ns/op
//Iteration   3: 0.040 ns/op
//Iteration   4: 0.043 ns/op
//Iteration   5: 0.041 ns/op
//Iteration   6: 0.041 ns/op
//Iteration   7: 0.040 ns/op
//Iteration   8: 0.040 ns/op
//Iteration   9: 0.042 ns/op
//Iteration  10: 0.042 ns/op
//
//
//Result "com.lbq.concurrent.jmh.JMHExample16.measureLoop_100":
//  0.041 ±(99.9%) 0.002 ns/op [Average]
//  (min, avg, max) = (0.040, 0.041, 0.043), stdev = 0.001
//  CI (99.9%): [0.040, 0.043] (assumes normal distribution)
//
//
//# JMH version: 1.19
//# VM version: JDK 1.8.0_202, VM 25.202-b08
//# VM invoker: D:\DevInstall\Java\jdk1.8.0_202\jre\bin\java.exe
//# VM options: -Dfile.encoding=UTF-8
//# Warmup: 5 iterations, 1 s each
//# Measurement: 10 iterations, 1 s each
//# Timeout: 10 min per iteration
//# Threads: 1 thread, will synchronize iterations
//# Benchmark mode: Average time, time/op
//# Benchmark: com.lbq.concurrent.jmh.JMHExample16.measureLoop_1000
//
//# Run progress: 80.00% complete, ETA 00:00:15
//# Fork: 1 of 1
//# Warmup Iteration   1: 0.040 ns/op
//# Warmup Iteration   2: 0.040 ns/op
//# Warmup Iteration   3: 0.041 ns/op
//# Warmup Iteration   4: 0.040 ns/op
//# Warmup Iteration   5: 0.038 ns/op
//Iteration   1: 0.037 ns/op
//Iteration   2: 0.039 ns/op
//Iteration   3: 0.035 ns/op
//Iteration   4: 0.036 ns/op
//Iteration   5: 0.037 ns/op
//Iteration   6: 0.036 ns/op
//Iteration   7: 0.036 ns/op
//Iteration   8: 0.037 ns/op
//Iteration   9: 0.036 ns/op
//Iteration  10: 0.037 ns/op
//
//
//Result "com.lbq.concurrent.jmh.JMHExample16.measureLoop_1000":
//  0.037 ±(99.9%) 0.001 ns/op [Average]
//  (min, avg, max) = (0.035, 0.037, 0.039), stdev = 0.001
//  CI (99.9%): [0.035, 0.038] (assumes normal distribution)
//
//
//# Run complete. Total time: 00:01:18
//
//Benchmark                      Mode  Cnt  Score   Error  Units
//JMHExample16.measure           avgt   10  3.319 ± 0.279  ns/op
//JMHExample16.measureLoop_1     avgt   10  3.197 ± 0.060  ns/op
//JMHExample16.measureLoop_10    avgt   10  0.368 ± 0.009  ns/op
//JMHExample16.measureLoop_100   avgt   10  0.041 ± 0.002  ns/op
//JMHExample16.measureLoop_1000  avgt   10  0.037 ± 0.001  ns/op
