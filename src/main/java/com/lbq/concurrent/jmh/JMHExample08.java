package com.lbq.concurrent.jmh;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.concurrent.TimeUnit;
/**
 * 3.线程组共享的State
 * 截至目前，我们所编写的基准测试方法都会被JMH框架根据方法名的字典顺序排序后按照顺序逐个地调用执行，因此不存在两个方法同时运行的情况，
 * 如果想要测试某个共享数据或者共享资源在多线程的情况下同时被读写的行为，是没有办法进行的，
 * 比如，在多线程高并发的环境中，多个线程同时对一个Concurrent HashMap进行读写。
 * 
 * 通过上面这段文字可以简单地归纳出我们的诉求，第一，是在多线程情况下的单个实例；第二，允许一个以上的基准测试方法并发地运行。
 * 所行的是，Scope.Group可以帮助我们实现这一点，先来看一个简单的例子，后文针对BlockingQueue进行测试时，还会使用Scope.Group来实现，
 * 所以在本节中，我们知道其大概的用法即可。
 * 
 * 执行下面的基准测试，我们会得到一些比较关键的信息输出。
 * @author 14378
 *
 */
@BenchmarkMode(Mode.AverageTime)
@Fork(1)
@Warmup(iterations = 5)
@Measurement(iterations = 10)
@OutputTimeUnit(TimeUnit.MICROSECONDS)
public class JMHExample08 {

	//将Test设置为线程组共享的
	@State(Scope.Group)
	public static class Test {
		public Test() {
			System.out.println("create instance");
		}
		
		public void write() {
			System.out.println("write");
		}
		
		public void read() {
			System.out.println("read");
		}
	}
	
	//在线程组"test"中，有三个线程将不断地对Test实例的write方法进行调用
	@GroupThreads(3)
	@Group("test")
	@Benchmark
	public void testWrite(Test test) {
		test.write();
	}
	
	//在线程组"test"中，有三个线程将不断地对Test实例的read方法进行调用
	@GroupThreads(3)
	@Group("test")
	@Benchmark
	public void testRead(Test test) {
		test.read();
	}
	
	public static void main(String[] args) throws RunnerException {
		final Options opts = new OptionsBuilder()
				.include(JMHExample08.class.getSimpleName())
				.build();
		new Runner(opts).run();

	}

}
//总共6个线程会执行基准测试方法，这6个线程都在同一个group中，其中，testRead方法会被3个线程执行，testWrite方法会被3个线程执行
//#Threads: 6 threads (1 group; 3x "testRead", 3x "testWrite" in each group), will synchronize iterations
//read和write分别交替输出，因此testRead和testWrite是交替执行的
//read
//read
//read
//read
//read
//write
//write
//write
//write
//write
//write
//read
//read
//read
//157.795 ±(99.9%) 113.830 us/op
//                 testRead:  158.946 ±(99.9%) 971.197 us/op
//                 testWrite: 156.645 ±(99.9%) 653.105 us/op
//
//
//
//Result "com.lbq.concurrent.jmh.JMHExample08.test":
//  173.100 ±(99.9%) 23.024 us/op [Average]
//  (min, avg, max) = (157.795, 173.100, 206.158), stdev = 15.229
//  CI (99.9%): [150.076, 196.124] (assumes normal distribution)
//
//Secondary result "com.lbq.concurrent.jmh.JMHExample08.test:testRead":
//  167.430 ±(99.9%) 47.646 us/op [Average]
//  (min, avg, max) = (121.635, 167.430, 223.737), stdev = 31.515
//  CI (99.9%): [119.784, 215.076] (assumes normal distribution)
//
//Secondary result "com.lbq.concurrent.jmh.JMHExample08.test:testWrite":
//  178.771 ±(99.9%) 68.506 us/op [Average]
//  (min, avg, max) = (119.430, 178.771, 256.703), stdev = 45.312
//  CI (99.9%): [110.265, 247.277] (assumes normal distribution)
//
//
//# Run complete. Total time: 00:02:31
//
//REMEMBER: The numbers below are just data. To gain reusable insights, you need to follow up on
//why the numbers are the way they are. Use profilers (see -prof, -lprof), design factorial
//experiments, perform baseline and negative tests that provide experimental control, make sure
//the benchmarking environment is safe on JVM/OS/HW level, ask for reviews from the domain experts.
//Do not assume the numbers tell you what you want them to tell.
//
//Benchmark                    Mode  Cnt    Score    Error  Units
//JMHExample08.test            avgt   10  173.100 ± 23.024  us/op
//JMHExample08.test:testRead   avgt   10  167.430 ± 47.646  us/op
//JMHExample08.test:testWrite  avgt   10  178.771 ± 68.506  us/op