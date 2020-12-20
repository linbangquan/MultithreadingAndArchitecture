package com.lbq.concurrent.jmh;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

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
import org.openjdk.jmh.profile.StackProfiler;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import org.openjdk.jmh.runner.options.TimeValue;
/**
 * 1.5 JMH的Profiler
 * JMH提供了一些非常有用的Profiler可以帮助我们更加深入地了解基准测试，甚至还能帮助开发者分析所编写的代码，
 * JMH目前提供了如表1-2所示的一些Profiler以供使用。
 * Profiler名称		Profiler描述
 * CL			分析执行Benchmark方法时的类加载情况
 * COMP			通过Standard MBean进行Benchmark方法的JIT编译器分析
 * GC			通过Standard MBeans进行Benchmark方法的GC分析
 * HS_CL		HotSpot ™类加载器通过特定于实现的MBean进行分析
 * HS_COMP		HotSpot ™JIT通过特定于实现的MBean编译分析
 * HS_GC		HotSpot ™内存管理器（GC）通过特定于实现的MBean进行分析
 * HS_RT		通过Implementation-Specific MBean 进行HotSpot ™运行时分析
 * HS_THR		通过Implementation-Specific MBean 进行HotSpot ™线程分析
 * STACK		JVM线程栈信息分析
 * 
 * 关于Profiler，这里并没有逐一进行介绍，只是介绍Stack、GC、CL、COMP这几个Profiler，其他的Profiler读者可以自行尝试。
 * 
 * 1.5.1 StackProfiler
 * StackProfiler不仅可以输出线程堆栈的信息，还能统计程序在执行的过程中线程状态的数据，比如RUNNING状态、WAIT状态所占用的百分比等，
 * 下面对1.4.2节中所列举的例子稍加改造。
 * 我们在Options中增加了StackProfiler用于分析线程的堆栈情况，还可以输出线程状态的分布情况，下面是JMHExample20.java的JMH运行输出结果。
 * 
 * 通过上面的输出结果可以看到，线程状态的分布情况为WAITING：1.7%，RUNNABLE:89.3%，考虑到我们使用的是BlockingQueue，因此这种分布应该还算合理。
 * @author 14378
 *
 */
@BenchmarkMode(Mode.AverageTime)
@Fork(1)
@Warmup(iterations = 5)
@Measurement(iterations = 5)
@OutputTimeUnit(TimeUnit.MICROSECONDS)
@State(Scope.Group)
public class JMHExample21 {

	private BlockingQueue<Integer> queue;
	
	private final static int VALUE = Integer.MAX_VALUE;
	
	@Setup
	public void init() {
		this.queue = new ArrayBlockingQueue<>(10);
	}
	
	@GroupThreads(5)
	@Group("blockingQueue")
	@Benchmark
	public void put() throws InterruptedException {
		this.queue.put(VALUE);
	}
	@GroupThreads(5)
	@Group("blockingQueue")
	@Benchmark
	public int take() throws InterruptedException {
		return this.queue.take();
	}
	
	public static void main(String[] args) throws RunnerException {
		final Options opts = new OptionsBuilder()
				.include(JMHExample18.class.getSimpleName())
				.timeout(TimeValue.seconds(10))
				.addProfiler(StackProfiler.class)//增加StackProfiler
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
//# Timeout: 10 s per iteration
//# Threads: 10 threads (1 group; 5x "get", 5x "inc" in each group), will synchronize iterations
//# Benchmark mode: Average time, time/op
//# Benchmark: com.lbq.concurrent.jmh.JMHExample18.q
//
//# Run progress: 0.00% complete, ETA 00:00:10
//# Fork: 1 of 1
//# Warmup Iteration   1: 0.177 ±(99.9%) 0.094 us/op
//# Warmup Iteration   2: 0.161 ±(99.9%) 0.098 us/op
//# Warmup Iteration   3: 0.152 ±(99.9%) 0.095 us/op
//# Warmup Iteration   4: 0.149 ±(99.9%) 0.098 us/op
//# Warmup Iteration   5: 0.149 ±(99.9%) 0.097 us/op
//Iteration   1: 0.150 ±(99.9%) 0.093 us/op
//                 get:    0.092 ±(99.9%) 0.028 us/op
//                 inc:    0.208 ±(99.9%) 0.036 us/op
//                 ·stack: <delayed till summary>
//
//Iteration   2: 0.148 ±(99.9%) 0.098 us/op
//                 get:    0.087 ±(99.9%) 0.020 us/op
//                 inc:    0.209 ±(99.9%) 0.042 us/op
//                 ·stack: <delayed till summary>
//
//Iteration   3: 0.149 ±(99.9%) 0.100 us/op
//                 get:    0.087 ±(99.9%) 0.029 us/op
//                 inc:    0.211 ±(99.9%) 0.069 us/op
//                 ·stack: <delayed till summary>
//
//Iteration   4: 0.150 ±(99.9%) 0.100 us/op
//                 get:    0.087 ±(99.9%) 0.016 us/op
//                 inc:    0.212 ±(99.9%) 0.045 us/op
//                 ·stack: <delayed till summary>
//
//Iteration   5: 0.155 ±(99.9%) 0.091 us/op
//                 get:    0.099 ±(99.9%) 0.048 us/op
//                 inc:    0.211 ±(99.9%) 0.024 us/op
//                 ·stack: <delayed till summary>
//
//
//
//Result "com.lbq.concurrent.jmh.JMHExample18.q":
//  0.150 ±(99.9%) 0.011 us/op [Average]
//  (min, avg, max) = (0.148, 0.150, 0.155), stdev = 0.003
//  CI (99.9%): [0.140, 0.161] (assumes normal distribution)
//
//Secondary result "com.lbq.concurrent.jmh.JMHExample18.q:get":
//  0.091 ±(99.9%) 0.020 us/op [Average]
//  (min, avg, max) = (0.087, 0.091, 0.099), stdev = 0.005
//  CI (99.9%): [0.071, 0.110] (assumes normal distribution)
//
//Secondary result "com.lbq.concurrent.jmh.JMHExample18.q:inc":
//  0.210 ±(99.9%) 0.007 us/op [Average]
//  (min, avg, max) = (0.208, 0.210, 0.212), stdev = 0.002
//  CI (99.9%): [0.204, 0.217] (assumes normal distribution)
//
//Secondary result "com.lbq.concurrent.jmh.JMHExample18.q:·stack":
//Stack profiler:
//
//....[Thread state distributions]....................................................................
// 98.3%         RUNNABLE
//  1.7%         WAITING
//
//....[Thread state: RUNNABLE]........................................................................
// 47.8%  48.6% com.lbq.concurrent.jmh.generated.JMHExample18_q_jmhTest.inc_avgt_jmhStub
// 47.8%  48.6% com.lbq.concurrent.jmh.generated.JMHExample18_q_jmhTest.get_avgt_jmhStub
//  2.3%   2.3% com.lbq.concurrent.jmh.generated.JMHExample18_q_jmhTest.q_AverageTime
//  0.1%   0.1% java.lang.Thread.isInterrupted
//  0.1%   0.1% sun.misc.Unsafe.unpark
//  0.1%   0.1% sun.misc.Unsafe.compareAndSwapInt
//  0.0%   0.0% sun.misc.Unsafe.compareAndSwapObject
//  0.0%   0.0% java.lang.Thread.currentThread
//  0.0%   0.0% org.openjdk.jmh.runner.InfraControl.announceWarmdownReady
//  0.0%   0.0% sun.reflect.Reflection.getClassAccessFlags
//
//....[Thread state: WAITING].........................................................................
//  1.7% 100.0% sun.misc.Unsafe.park
//
//
//
//# Run complete. Total time: 00:00:11
//
//Benchmark              Mode  Cnt  Score   Error  Units
//JMHExample18.q         avgt    5  0.150 ± 0.011  us/op
//JMHExample18.q:get     avgt    5  0.091 ± 0.020  us/op
//JMHExample18.q:inc     avgt    5  0.210 ± 0.007  us/op
//JMHExample18.q:·stack  avgt         NaN            ---
