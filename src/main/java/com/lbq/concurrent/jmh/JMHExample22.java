package com.lbq.concurrent.jmh;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.concurrent.TimeUnit;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Warmup;
import org.openjdk.jmh.profile.GCProfiler;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
/**
 * GCProfiler可用于分析出在测试方法中垃圾回收器在JVM每个内存空间上所花费的时间，本节将使用自定义的类加载器进行类的加载。
 * 
 * 接下来的程序将使用AlexClassLoader这个类加载器进行相关类的加载，比如下面的Alex类，非常简单。
 * 
 * 将Alex.java源文件编译Alex.class字节码文件之后，我们将使用前面定义的类加载器对该字节码进行加载，然后用JMH对该类的加载进行基准测试，并且增加GCProfiler查看GC的情况。
 * 
 * 运行下面的基准测试方法，我们除了得到testLoadClass()方法的基准数据之外，还会得到GC相关的 信息。
 * 
 * 根据GCProfiler的输出信息可以看到，在这个基准方法执行的过程之中，gc总共出现过209次，这209次总共耗时1139毫秒，
 * 在此期间也发生了多次的堆内存的申请，比如，每秒钟大约会有1132.095MB的数据被创建，
 * 若换算成对testLoadClass方法的每次调用，那么我们会发现大约有10280.003Byte内存使用。
 * @author 14378
 *
 */
@BenchmarkMode(Mode.AverageTime)
@Fork(1)
@Warmup(iterations = 5)
@Measurement(iterations = 5)
@OutputTimeUnit(TimeUnit.MICROSECONDS)
@State(Scope.Thread)
public class JMHExample22 {

	private byte[] alexBytes;
	
	private AlexClassLoader classLoader;
	
	@Setup
	public void init() throws IOException {
		this.alexBytes = Files.readAllBytes(Paths.get("D:\\DevInstall\\eclipse-jee-2019-12-R-win32-x86_64\\workspace\\MultithreadingAndArchitecture\\src\\main\\java\\com\\lbq\\concurrent\\jmh\\Alex.class"));
		this.classLoader = new AlexClassLoader(alexBytes);
	}

	@Benchmark
	public Object testLoadClass() throws ClassNotFoundException, InstantiationException, IllegalAccessException {
		Class<?> alexClass = Class.forName("Alex", true, classLoader);
		return alexClass.newInstance();
	}
	
	public static void main(String[] args) throws RunnerException {
		final Options opts = new OptionsBuilder()
				.include(JMHExample22.class.getSimpleName())
				//add GcProfiler输出基准方法执行过程的GC信息
				.addProfiler(GCProfiler.class)
				//将最大堆内存设置为128MB，会有多次的GC发生
				.jvmArgsAppend("-Xmx128M")
				.build();
		new Runner(opts).run();

	}

}
