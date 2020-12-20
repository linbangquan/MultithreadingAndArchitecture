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
import org.openjdk.jmh.profile.CompilerProfiler;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import org.openjdk.jmh.runner.options.VerboseMode;

/**
 * 1.5.4 CompilerProfiler
 * CompilerProfiler将会告诉你在代码的执行过程中JIT编译器所花费的优化时间，我们可以打开verbose模式观察更详细的输出。
 * 
 * 运行上面的基准测试，我们将得出JIT在整个过程中的优化耗时，输出结果具体如下。
 * 
 * 我们可以看到，在整个方法的执行过程中，profiled的优化耗时为20毫秒，total的优化耗时为1237毫秒。
 * 
 * 1.6 本章总结
 * 在实际工作，笔者主要是将JMH用在对某些API的性能调研(investigation)上，比如，在实现某个功能时需要某线程安全的类，但是该类却有不同的实现方式，难以取舍之中，唯有请JMH提供一个比较精准的参考。
 * 想象一下笔者在日常的开发工作中，同时打开两个项目工程，一个是开发中的项目工程，另一个是JMH工程情景，这样做主要用于帮助笔者更好地了解所编写的代码。
 * 在本书的其他章节中，有关性能测试比较的地方都会使用JMH这个工具，可以说JMH会贯穿整本书的内容。
 * @author 14378
 *
 */
@BenchmarkMode(Mode.AverageTime)
@Fork(1)
@Warmup(iterations = 50)
@Measurement(iterations = 5)
@OutputTimeUnit(TimeUnit.MICROSECONDS)
@State(Scope.Thread)
public class JMHExample24 {

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
				.include(JMHExample24.class.getSimpleName())
				.addProfiler(CompilerProfiler.class)
				.verbosity(VerboseMode.EXTRA)
				.build();
		new Runner(opts).run();

	}

}
