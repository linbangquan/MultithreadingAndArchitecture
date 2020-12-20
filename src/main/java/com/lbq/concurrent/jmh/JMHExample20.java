package com.lbq.concurrent.jmh;

import java.util.Collections;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.TimeUnit;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Group;
import org.openjdk.jmh.annotations.GroupThreads;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Warmup;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
/**
 * 3.几大线程安全Map的性能对比
 * 好了，现在可以根据学习到的JMH的知识来重新对比一下1.3.6节中关于几大Map的多线程下的读写性能了。
 * 
 * 大家可以看到，在putMap和getMap方法中，通过随机值的方式将取值作为key和value存入map中，同样也是通过随机值的方式将取值作为key从map中进行数据读取(当然读取的值可能并不存在)。
 * 还有我们在基准方法中进行了随机值的运算，虽然随机值计算所耗费的CPU时间也会被纳入基准结果的统计中，但是每一个map都进行了相关的计算，因此，我们可以认为大家还是站在了同样的起跑线上，
 * 故而可以对其忽略不计。运行上面的基准测试将会得到如下结果。
 * Benchmark              (type)  Mode  Cnt   Score    Error  Units
 * JMHExample20.g              1  avgt    5   4.045 ±  1.791  us/op
 * JMHExample20.g:getMap       1  avgt    5   4.268 ±  3.505  us/op
 * JMHExample20.g:putMap       1  avgt    5   3.822 ±  0.793  us/op
 * JMHExample20.g              2  avgt    5   4.140 ±  0.248  us/op
 * JMHExample20.g:getMap       2  avgt    5   4.129 ±  0.625  us/op
 * JMHExample20.g:putMap       2  avgt    5   4.152 ±  0.683  us/op
 * JMHExample20.g              3  avgt    5  10.226 ±  6.612  us/op
 * JMHExample20.g:getMap       3  avgt    5  17.613 ± 12.489  us/op
 * JMHExample20.g:putMap       3  avgt    5   2.839 ±  1.297  us/op
 * JMHExample20.g              4  avgt    5  19.549 ± 20.063  us/op
 * JMHExample20.g:getMap       4  avgt    5  36.574 ± 39.780  us/op
 * JMHExample20.g:putMap       4  avgt    5   2.523 ±  1.129  us/op
 * 
 * 基准测试的数据可以表明，在5个线程同时进行map写操作，5个线程同时进行读操作时，参数type=1的性能是最佳的，也就是ConcurrentHashMap。
 * @author 14378
 *
 */
@BenchmarkMode(Mode.AverageTime)
@Fork(1)
@Warmup(iterations = 5)
@Measurement(iterations = 5)
@OutputTimeUnit(TimeUnit.MICROSECONDS)
@State(Scope.Group)
public class JMHExample20 {

	@Param({"1", "2", "3", "4"})
	private int type;
	
	private Map<Integer, Integer> map;
	
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
	/**
	 * 计算一个随机值用作Map中Key和Value
	 * @return
	 */
	private int randomIntValue() {
		return (int) Math.ceil(Math.random() * 600000);
	}
	
	@Group("g")
	@GroupThreads(5)
	@Benchmark
	public void putMap() {
		int random = randomIntValue();
		this.map.put(random, random);
	}
	@Group("g")
	@GroupThreads(5)
	@Benchmark
	public Integer getMap() {
		return this.map.get(randomIntValue());
	}
	
	public static void main(String[] args) throws RunnerException {
		final Options opts = new OptionsBuilder()
				.include(JMHExample20.class.getSimpleName())
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
//# Threads: 10 threads (1 group; 5x "getMap", 5x "putMap" in each group), will synchronize iterations
//# Benchmark mode: Average time, time/op
//# Benchmark: com.lbq.concurrent.jmh.JMHExample20.g
//# Parameters: (type = 1)
//
//# Run progress: 0.00% complete, ETA 00:00:40
//# Fork: 1 of 1
//# Warmup Iteration   1: 3.923 ±(99.9%) 1.929 us/op
//# Warmup Iteration   2: 4.048 ±(99.9%) 2.340 us/op
//# Warmup Iteration   3: 4.030 ±(99.9%) 2.217 us/op
//# Warmup Iteration   4: 3.473 ±(99.9%) 0.732 us/op
//# Warmup Iteration   5: 3.810 ±(99.9%) 1.332 us/op
//Iteration   1: 3.941 ±(99.9%) 2.105 us/op
//                 getMap: 3.719 ±(99.9%) 5.763 us/op
//                 putMap: 4.162 ±(99.9%) 5.445 us/op
//
//Iteration   2: 4.798 ±(99.9%) 3.636 us/op
//                 getMap: 5.780 ±(99.9%) 8.256 us/op
//                 putMap: 3.817 ±(99.9%) 9.435 us/op
//
//Iteration   3: 3.520 ±(99.9%) 0.708 us/op
//                 getMap: 3.422 ±(99.9%) 1.437 us/op
//                 putMap: 3.618 ±(99.9%) 2.211 us/op
//
//Iteration   4: 3.946 ±(99.9%) 2.096 us/op
//                 getMap: 4.177 ±(99.9%) 5.448 us/op
//                 putMap: 3.714 ±(99.9%) 5.697 us/op
//
//Iteration   5: 4.021 ±(99.9%) 2.188 us/op
//                 getMap: 4.244 ±(99.9%) 5.057 us/op
//                 putMap: 3.799 ±(99.9%) 6.518 us/op
//
//
//
//Result "com.lbq.concurrent.jmh.JMHExample20.g":
//  4.045 ±(99.9%) 1.791 us/op [Average]
//  (min, avg, max) = (3.520, 4.045, 4.798), stdev = 0.465
//  CI (99.9%): [2.255, 5.836] (assumes normal distribution)
//
//Secondary result "com.lbq.concurrent.jmh.JMHExample20.g:getMap":
//  4.268 ±(99.9%) 3.505 us/op [Average]
//  (min, avg, max) = (3.422, 4.268, 5.780), stdev = 0.910
//  CI (99.9%): [0.764, 7.773] (assumes normal distribution)
//
//Secondary result "com.lbq.concurrent.jmh.JMHExample20.g:putMap":
//  3.822 ±(99.9%) 0.793 us/op [Average]
//  (min, avg, max) = (3.618, 3.822, 4.162), stdev = 0.206
//  CI (99.9%): [3.029, 4.615] (assumes normal distribution)
//
//
//# JMH version: 1.19
//# VM version: JDK 1.8.0_202, VM 25.202-b08
//# VM invoker: D:\DevInstall\Java\jdk1.8.0_202\jre\bin\java.exe
//# VM options: -Dfile.encoding=UTF-8
//# Warmup: 5 iterations, 1 s each
//# Measurement: 5 iterations, 1 s each
//# Timeout: 10 min per iteration
//# Threads: 10 threads (1 group; 5x "getMap", 5x "putMap" in each group), will synchronize iterations
//# Benchmark mode: Average time, time/op
//# Benchmark: com.lbq.concurrent.jmh.JMHExample20.g
//# Parameters: (type = 2)
//
//# Run progress: 25.00% complete, ETA 00:00:33
//# Fork: 1 of 1
//# Warmup Iteration   1: 4.308 ±(99.9%) 1.179 us/op
//# Warmup Iteration   2: 4.517 ±(99.9%) 0.580 us/op
//# Warmup Iteration   3: 4.319 ±(99.9%) 0.618 us/op
//# Warmup Iteration   4: 3.996 ±(99.9%) 0.545 us/op
//# Warmup Iteration   5: 4.210 ±(99.9%) 0.293 us/op
//Iteration   1: 4.230 ±(99.9%) 1.233 us/op
//                 getMap: 4.276 ±(99.9%) 3.063 us/op
//                 putMap: 4.184 ±(99.9%) 3.568 us/op
//
//Iteration   2: 4.111 ±(99.9%) 0.773 us/op
//                 getMap: 4.184 ±(99.9%) 1.917 us/op
//                 putMap: 4.038 ±(99.9%) 2.205 us/op
//
//Iteration   3: 4.147 ±(99.9%) 1.073 us/op
//                 getMap: 3.855 ±(99.9%) 2.608 us/op
//                 putMap: 4.439 ±(99.9%) 2.616 us/op
//
//Iteration   4: 4.157 ±(99.9%) 1.120 us/op
//                 getMap: 4.205 ±(99.9%) 3.059 us/op
//                 putMap: 4.110 ±(99.9%) 2.981 us/op
//
//Iteration   5: 4.055 ±(99.9%) 0.250 us/op
//                 getMap: 4.124 ±(99.9%) 0.223 us/op
//                 putMap: 3.986 ±(99.9%) 0.829 us/op
//
//
//
//Result "com.lbq.concurrent.jmh.JMHExample20.g":
//  4.140 ±(99.9%) 0.248 us/op [Average]
//  (min, avg, max) = (4.055, 4.140, 4.230), stdev = 0.064
//  CI (99.9%): [3.892, 4.388] (assumes normal distribution)
//
//Secondary result "com.lbq.concurrent.jmh.JMHExample20.g:getMap":
//  4.129 ±(99.9%) 0.625 us/op [Average]
//  (min, avg, max) = (3.855, 4.129, 4.276), stdev = 0.162
//  CI (99.9%): [3.504, 4.754] (assumes normal distribution)
//
//Secondary result "com.lbq.concurrent.jmh.JMHExample20.g:putMap":
//  4.152 ±(99.9%) 0.683 us/op [Average]
//  (min, avg, max) = (3.986, 4.152, 4.439), stdev = 0.177
//  CI (99.9%): [3.469, 4.834] (assumes normal distribution)
//
//
//# JMH version: 1.19
//# VM version: JDK 1.8.0_202, VM 25.202-b08
//# VM invoker: D:\DevInstall\Java\jdk1.8.0_202\jre\bin\java.exe
//# VM options: -Dfile.encoding=UTF-8
//# Warmup: 5 iterations, 1 s each
//# Measurement: 5 iterations, 1 s each
//# Timeout: 10 min per iteration
//# Threads: 10 threads (1 group; 5x "getMap", 5x "putMap" in each group), will synchronize iterations
//# Benchmark mode: Average time, time/op
//# Benchmark: com.lbq.concurrent.jmh.JMHExample20.g
//# Parameters: (type = 3)
//
//# Run progress: 50.00% complete, ETA 00:00:22
//# Fork: 1 of 1
//# Warmup Iteration   1: 5.446 ±(99.9%) 3.840 us/op
//# Warmup Iteration   2: 9.379 ±(99.9%) 12.409 us/op
//# Warmup Iteration   3: 10.460 ±(99.9%) 13.443 us/op
//# Warmup Iteration   4: 10.860 ±(99.9%) 12.909 us/op
//# Warmup Iteration   5: 12.765 ±(99.9%) 16.393 us/op
//Iteration   1: 7.447 ±(99.9%) 8.791 us/op
//                 getMap: 12.249 ±(99.9%) 15.424 us/op
//                 putMap: 2.645 ±(99.9%) 5.947 us/op
//
//Iteration   2: 11.625 ±(99.9%) 15.284 us/op
//                 getMap: 20.331 ±(99.9%) 21.457 us/op
//                 putMap: 2.919 ±(99.9%) 11.829 us/op
//
//Iteration   3: 11.325 ±(99.9%) 14.034 us/op
//                 getMap: 19.665 ±(99.9%) 12.477 us/op
//                 putMap: 2.984 ±(99.9%) 11.868 us/op
//
//Iteration   4: 11.025 ±(99.9%) 13.662 us/op
//                 getMap: 18.786 ±(99.9%) 16.870 us/op
//                 putMap: 3.264 ±(99.9%) 14.384 us/op
//
//Iteration   5: 9.708 ±(99.9%) 13.162 us/op
//                 getMap: 17.033 ±(99.9%) 22.397 us/op
//                 putMap: 2.383 ±(99.9%) 6.167 us/op
//
//
//
//Result "com.lbq.concurrent.jmh.JMHExample20.g":
//  10.226 ±(99.9%) 6.612 us/op [Average]
//  (min, avg, max) = (7.447, 10.226, 11.625), stdev = 1.717
//  CI (99.9%): [3.614, 16.838] (assumes normal distribution)
//
//Secondary result "com.lbq.concurrent.jmh.JMHExample20.g:getMap":
//  17.613 ±(99.9%) 12.489 us/op [Average]
//  (min, avg, max) = (12.249, 17.613, 20.331), stdev = 3.243
//  CI (99.9%): [5.124, 30.102] (assumes normal distribution)
//
//Secondary result "com.lbq.concurrent.jmh.JMHExample20.g:putMap":
//  2.839 ±(99.9%) 1.297 us/op [Average]
//  (min, avg, max) = (2.383, 2.839, 3.264), stdev = 0.337
//  CI (99.9%): [1.543, 4.136] (assumes normal distribution)
//
//
//# JMH version: 1.19
//# VM version: JDK 1.8.0_202, VM 25.202-b08
//# VM invoker: D:\DevInstall\Java\jdk1.8.0_202\jre\bin\java.exe
//# VM options: -Dfile.encoding=UTF-8
//# Warmup: 5 iterations, 1 s each
//# Measurement: 5 iterations, 1 s each
//# Timeout: 10 min per iteration
//# Threads: 10 threads (1 group; 5x "getMap", 5x "putMap" in each group), will synchronize iterations
//# Benchmark mode: Average time, time/op
//# Benchmark: com.lbq.concurrent.jmh.JMHExample20.g
//# Parameters: (type = 4)
//
//# Run progress: 75.00% complete, ETA 00:00:11
//# Fork: 1 of 1
//# Warmup Iteration   1: 8.344 ±(99.9%) 13.019 us/op
//# Warmup Iteration   2: 15.944 ±(99.9%) 21.085 us/op
//# Warmup Iteration   3: 21.836 ±(99.9%) 31.758 us/op
//# Warmup Iteration   4: 16.438 ±(99.9%) 23.577 us/op
//# Warmup Iteration   5: 15.978 ±(99.9%) 24.387 us/op
//Iteration   1: 22.660 ±(99.9%) 35.705 us/op
//                 getMap: 42.511 ±(99.9%) 63.176 us/op
//                 putMap: 2.809 ±(99.9%) 2.966 us/op
//
//Iteration   2: 22.286 ±(99.9%) 33.791 us/op
//                 getMap: 41.905 ±(99.9%) 48.879 us/op
//                 putMap: 2.667 ±(99.9%) 3.006 us/op
//
//Iteration   3: 12.927 ±(99.9%) 17.979 us/op
//                 getMap: 23.199 ±(99.9%) 27.966 us/op
//                 putMap: 2.656 ±(99.9%) 5.031 us/op
//
//Iteration   4: 24.796 ±(99.9%) 35.831 us/op
//                 getMap: 47.169 ±(99.9%) 13.337 us/op
//                 putMap: 2.423 ±(99.9%) 2.437 us/op
//
//Iteration   5: 15.072 ±(99.9%) 23.234 us/op
//                 getMap: 28.084 ±(99.9%) 39.989 us/op
//                 putMap: 2.061 ±(99.9%) 2.145 us/op
//
//
//
//Result "com.lbq.concurrent.jmh.JMHExample20.g":
//  19.549 ±(99.9%) 20.063 us/op [Average]
//  (min, avg, max) = (12.927, 19.549, 24.796), stdev = 5.210
//  CI (99.9%): [≈ 0, 39.612] (assumes normal distribution)
//
//Secondary result "com.lbq.concurrent.jmh.JMHExample20.g:getMap":
//  36.574 ±(99.9%) 39.780 us/op [Average]
//  (min, avg, max) = (23.199, 36.574, 47.169), stdev = 10.331
//  CI (99.9%): [≈ 0, 76.354] (assumes normal distribution)
//
//Secondary result "com.lbq.concurrent.jmh.JMHExample20.g:putMap":
//  2.523 ±(99.9%) 1.129 us/op [Average]
//  (min, avg, max) = (2.061, 2.523, 2.809), stdev = 0.293
//  CI (99.9%): [1.394, 3.653] (assumes normal distribution)
//
//
//# Run complete. Total time: 00:00:43
//
//Benchmark              (type)  Mode  Cnt   Score    Error  Units
//JMHExample20.g              1  avgt    5   4.045 ±  1.791  us/op
//JMHExample20.g:getMap       1  avgt    5   4.268 ±  3.505  us/op
//JMHExample20.g:putMap       1  avgt    5   3.822 ±  0.793  us/op
//JMHExample20.g              2  avgt    5   4.140 ±  0.248  us/op
//JMHExample20.g:getMap       2  avgt    5   4.129 ±  0.625  us/op
//JMHExample20.g:putMap       2  avgt    5   4.152 ±  0.683  us/op
//JMHExample20.g              3  avgt    5  10.226 ±  6.612  us/op
//JMHExample20.g:getMap       3  avgt    5  17.613 ± 12.489  us/op
//JMHExample20.g:putMap       3  avgt    5   2.839 ±  1.297  us/op
//JMHExample20.g              4  avgt    5  19.549 ± 20.063  us/op
//JMHExample20.g:getMap       4  avgt    5  36.574 ± 39.780  us/op
//JMHExample20.g:putMap       4  avgt    5   2.523 ±  1.129  us/op
