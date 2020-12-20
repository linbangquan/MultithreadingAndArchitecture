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
import org.openjdk.jmh.profile.ClassloaderProfiler;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
/**
 * 1.5.3 ClassLoaderProfiler
 * ClassLoaderProfiler可以帮助我们看到在基准方法的执行过程中有多少类被加载和卸载，
 * 但是考虑到在一个类加载器中同一个类只会被加载一次的情况，因此我们需要将Warmup设置为0，
 * 以避免在热身阶段就已经加载了基准测试方法所需要的所有类。
 * 
 * 运行下面的基准测试方法，我们将会看到在第一个批次的度量时加载了大量的类，在余下的几次度量中将不会再进行类的加载了，这也符合JVM类加载器的基本逻辑。
 * 
 * 我们可以看到，在testLoadClass方法的执行过程中，每秒大约会有21个类的加载。
 * @author 14378
 *
 */
@BenchmarkMode(Mode.AverageTime)
@Fork(1)
//将热身批次设置为0
@Warmup(iterations = 0)
@Measurement(iterations = 5)
@OutputTimeUnit(TimeUnit.MICROSECONDS)
@State(Scope.Thread)
public class JMHExample23 {

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
				.include(JMHExample23.class.getSimpleName())
				//增加CL Profiler，输出类的加载、卸载信息
				.addProfiler(ClassloaderProfiler.class)
				.build();
		new Runner(opts).run();

	}

}
