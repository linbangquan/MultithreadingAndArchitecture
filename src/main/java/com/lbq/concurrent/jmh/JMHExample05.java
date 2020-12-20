package com.lbq.concurrent.jmh;

import java.util.concurrent.TimeUnit;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
/**
 * 1.3.4 OuputTimeUnit
 * OutputTimeUnit提供了统计结果输出时的单位，比如，调用一次该方法将会耗费多少个单位时间，或者在单位时间内对该方法进行了多少次的调用，
 * 同样OutputTimeUnit既可以设置在class上，也可以设置在method上，还可以在Options中进行设置，它们的覆盖次序与BenchmarkMode一致。
 * @author 14378
 *
 */
//在class上设置
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@State(Scope.Thread)
@Measurement(iterations = 5)
@Warmup(iterations = 2)
public class JMHExample05 {
	//在基准方法上设置
	@OutputTimeUnit(TimeUnit.MICROSECONDS)
	@Benchmark
	public void test() throws InterruptedException {
		TimeUnit.SECONDS.sleep(1);
	}
	public static void main(String[] args) throws RunnerException {
		final Options opts = new OptionsBuilder()
				.include(JMHExample05.class.getSimpleName())
				//在Options上设置
				.timeUnit(TimeUnit.NANOSECONDS)
				.forks(1)
				.build();
		new Runner(opts).run();				

	}

}
