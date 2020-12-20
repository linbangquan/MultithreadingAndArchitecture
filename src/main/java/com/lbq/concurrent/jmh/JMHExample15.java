package com.lbq.concurrent.jmh;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.concurrent.TimeUnit;
/**
 * 3.避免常量折叠(Constant Folding)
 * 常量折叠是Java编译器早期的一种优化————编译优化。
 * 在javac对源文件进行编译的过程中，通过词法分析可以发现某些常量是可以被折叠的，
 * 也就是可以直接将计算结果存放到声明中，而不需要在执行阶段再次进行运算。比如：
 * private final int x = 10;
 * private final int y = x*20;
 * 在编译阶段，y的值将被直接赋予200，这就是所谓常量折叠，我们来看一下下面的基准测试代码示例。
 * 执行下面的基准测试代码，会得到如下的性能统计数据。
 * Benchmark                      Mode  Cnt   Score   Error  Units
 * JMHExample15.returnCaculate_1  avgt    5   3.052 ± 0.987  ns/op
 * JMHExample15.returnCaculate_2  avgt    5  55.798 ± 2.540  ns/op
 * JMHExample15.returnCaculate_3  avgt    5   2.880 ± 0.226  ns/op
 * JMHExample15.returnDirect      avgt    5   3.007 ± 0.990  ns/op
 * 我们可以看到，1、3、4三个方法的统计数据几乎相差无几，这也就意味着在编译器优化的的时候发生了常量折叠，这些方法在运行阶段根本不需要再进行计算，直接将结果返回即可，
 * 而第二个方法的统计数据就没那么好看了，因为早期的编译阶段不会对其进行任何的优化。
 * @author 14378
 *
 */
@BenchmarkMode(Mode.AverageTime)
@Fork(1)
@Warmup(iterations = 5)
@Measurement(iterations = 5)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@State(Scope.Thread)
public class JMHExample15 {
	//x1和x2是使用final修饰的常量
	private final double x1 = 124.456;
	private final double x2 = 342.456;
	
	//y1和y2则是普通的成员变量
	private double y1 = 124.456;
	private double y2 = 342.456;
	
	/**
	 * 直接返回124.456 X 342.456的计算结果，主要用它来作基准
	 * @return
	 */
	@Benchmark
	public double returnDirect() {
		return 42_620.703936d;
	}
	/**
	 * 两个常量相乘，我们需要验证在编译器的早期优化阶段是否直接计算出了x1乘以x2的值
	 * @return
	 */
	@Benchmark
	public double returnCaculate_1() {
		return x1 * x2;
	}
	/**
	 * 较为复杂的计算，计算两个未被final修饰的变量，主要也是用它来作为对比的基准
	 * @return
	 */
	@Benchmark
	public double returnCaculate_2() {
		return Math.log(y1) * Math.log(y2);
	}
	/**
	 * 较为复杂的计算，操作的同样是final修饰的常量，查看是否在编译器优化阶段进行了常量的折叠行为
	 * @return
	 */
	@Benchmark
	public double returnCaculate_3() {
		return Math.log(x1) * Math.log(x2);
	}
	
	public static void main(String[] args) throws RunnerException {
		final Options opts = new OptionsBuilder()
				.include(JMHExample15.class.getSimpleName())
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
//# Benchmark: com.lbq.concurrent.jmh.JMHExample15.returnCaculate_1
//
//# Run progress: 0.00% complete, ETA 00:00:40
//# Fork: 1 of 1
//# Warmup Iteration   1: 4.199 ns/op
//# Warmup Iteration   2: 4.066 ns/op
//# Warmup Iteration   3: 2.905 ns/op
//# Warmup Iteration   4: 3.008 ns/op
//# Warmup Iteration   5: 2.881 ns/op
//Iteration   1: 2.920 ns/op
//Iteration   2: 2.997 ns/op
//Iteration   3: 3.506 ns/op
//Iteration   4: 2.921 ns/op
//Iteration   5: 2.913 ns/op
//
//
//Result "com.lbq.concurrent.jmh.JMHExample15.returnCaculate_1":
//  3.052 ±(99.9%) 0.987 ns/op [Average]
//  (min, avg, max) = (2.913, 3.052, 3.506), stdev = 0.256
//  CI (99.9%): [2.064, 4.039] (assumes normal distribution)
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
//# Benchmark: com.lbq.concurrent.jmh.JMHExample15.returnCaculate_2
//
//# Run progress: 25.00% complete, ETA 00:00:32
//# Fork: 1 of 1
//# Warmup Iteration   1: 61.105 ns/op
//# Warmup Iteration   2: 58.975 ns/op
//# Warmup Iteration   3: 56.867 ns/op
//# Warmup Iteration   4: 57.824 ns/op
//# Warmup Iteration   5: 59.829 ns/op
//Iteration   1: 56.108 ns/op
//Iteration   2: 56.297 ns/op
//Iteration   3: 54.936 ns/op
//Iteration   4: 56.396 ns/op
//Iteration   5: 55.254 ns/op
//
//
//Result "com.lbq.concurrent.jmh.JMHExample15.returnCaculate_2":
//  55.798 ±(99.9%) 2.540 ns/op [Average]
//  (min, avg, max) = (54.936, 55.798, 56.396), stdev = 0.660
//  CI (99.9%): [53.258, 58.338] (assumes normal distribution)
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
//# Benchmark: com.lbq.concurrent.jmh.JMHExample15.returnCaculate_3
//
//# Run progress: 50.00% complete, ETA 00:00:21
//# Fork: 1 of 1
//# Warmup Iteration   1: 4.113 ns/op
//# Warmup Iteration   2: 3.909 ns/op
//# Warmup Iteration   3: 2.862 ns/op
//# Warmup Iteration   4: 2.877 ns/op
//# Warmup Iteration   5: 2.878 ns/op
//Iteration   1: 2.877 ns/op
//Iteration   2: 2.798 ns/op
//Iteration   3: 2.874 ns/op
//Iteration   4: 2.886 ns/op
//Iteration   5: 2.964 ns/op
//
//
//Result "com.lbq.concurrent.jmh.JMHExample15.returnCaculate_3":
//  2.880 ±(99.9%) 0.226 ns/op [Average]
//  (min, avg, max) = (2.798, 2.880, 2.964), stdev = 0.059
//  CI (99.9%): [2.654, 3.106] (assumes normal distribution)
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
//# Benchmark: com.lbq.concurrent.jmh.JMHExample15.returnDirect
//
//# Run progress: 75.00% complete, ETA 00:00:10
//# Fork: 1 of 1
//# Warmup Iteration   1: 4.275 ns/op
//# Warmup Iteration   2: 3.973 ns/op
//# Warmup Iteration   3: 2.934 ns/op
//# Warmup Iteration   4: 3.007 ns/op
//# Warmup Iteration   5: 2.903 ns/op
//Iteration   1: 2.892 ns/op
//Iteration   2: 2.890 ns/op
//Iteration   3: 3.467 ns/op
//Iteration   4: 2.891 ns/op
//Iteration   5: 2.895 ns/op
//
//
//Result "com.lbq.concurrent.jmh.JMHExample15.returnDirect":
//  3.007 ±(99.9%) 0.990 ns/op [Average]
//  (min, avg, max) = (2.890, 3.007, 3.467), stdev = 0.257
//  CI (99.9%): [2.017, 3.997] (assumes normal distribution)
//
//
//# Run complete. Total time: 00:00:43
//
//Benchmark                      Mode  Cnt   Score   Error  Units
//JMHExample15.returnCaculate_1  avgt    5   3.052 ± 0.987  ns/op
//JMHExample15.returnCaculate_2  avgt    5  55.798 ± 2.540  ns/op
//JMHExample15.returnCaculate_3  avgt    5   2.880 ± 0.226  ns/op
//JMHExample15.returnDirect      avgt    5   3.007 ± 0.990  ns/op
