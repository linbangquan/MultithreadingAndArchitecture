package com.lbq.concurrent.jmh;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.concurrent.TimeUnit;

import static java.lang.Math.PI;
import static java.lang.Math.log;
/**
 * 1.3.8 CompilerControl
 * 讲到这里，相信大家应该能够知道如何使用JMH对某些API方法进行微基准测试了吧，也许有些读者还会存在这样的疑惑：
 * JVM真的会对我们的代码进行相关的优化吗？下面通过一个简单的例子来验证一下优化是否存在。
 * 
 * JMHExample12包含两个基准测试方法test1和test2,但是test1方法中并未运行任何计算，而test2方法中进行了Math.log的运算，
 * 根据我们的常识很明显可以知道，Math.log方法的CPU耗时肯定要高于一个空方法，但是运行上面的基准测试方法之后得出的性能数据表明的结果是两者几乎不相上下。
 * Benchmark           Mode  Cnt   Score    Error  Units
 * JMHExample12.test1  avgt    5  ≈ 10⁻⁴           us/op
 * JMHExample12.test2  avgt    5  ≈ 10⁻⁴           us/op
 * 
 * 由于test2方法中存在Dead Code(关于Dead Code，我们会在1.4.1节的第1小节中进行详细的介绍)，
 * JVM在运行test2方法时对我们的程序进行了优化，具体来说就是将log运算的相关代码进行了运行期间擦除，
 * 下面我们通过CompilerControl禁止JVM运行时优化和编译之后再来执行一下基准测试方法，然后进行对比。
 * 
 * 运行下面的基准测试方法之后，结果表明两者的差别就很大了(2.5倍的差距了)。
 * Benchmark           Mode  Cnt  Score   Error  Units
 * JMHExample12.test1  avgt    5  0.012 ± 0.003  us/op
 * JMHExample12.test2  avgt    5  0.036 ± 0.006  us/op
 * 
 * 如果你想在自己的应用程序中杜绝JVM运行期的优化，那么我们可以通过如下的方式来实现(虽然这种情况我们并不推荐)。
 * 1.通过编写程序的方式禁止JVM运行期动态编译和优化java.lang.Compiler.disable();
 * 2.在启动JVM时增加参数-Djava.compiler=NONE。
 * 
 * @author 14378
 *
 */
@BenchmarkMode(Mode.AverageTime)
@Fork(1)
@Warmup(iterations = 5)
@Measurement(iterations = 5)
@OutputTimeUnit(TimeUnit.MICROSECONDS)
@State(Scope.Thread)
public class JMHExample12 {

	//禁止优化
	@CompilerControl(CompilerControl.Mode.EXCLUDE)
	@Benchmark
	public void test1() {
	
	}
	
	//禁止优化
	@CompilerControl(CompilerControl.Mode.EXCLUDE)
	@Benchmark
	public void test2() {
		log(PI);
	}
	
	public static void main(String[] args) throws RunnerException {
		final Options opts = new OptionsBuilder()
				.include(JMHExample12.class.getSimpleName())
				.build();
		new Runner(opts).run();
	}

}
