package com.lbq.concurrent.metrics.reporter;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

import com.codahale.metrics.Counter;
import com.codahale.metrics.CsvReporter;
import com.codahale.metrics.ExponentiallyDecayingReservoir;
import com.codahale.metrics.Histogram;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.RatioGauge;
import com.codahale.metrics.Timer;
/**
 * 7.3.4 CsvReporter
 * CsvReporter与logger reporter比较类似，也是将所有的度量信息输出到文件之中，但是它会以表格的形式展示，可读性更好一些，日后如果想要将这些CSV中的数据导入到数据库中也是非常方便的，
 * 甚至还可以基于Excel打开CSV文件进行二次统计、过滤等操作，如果想要将度量数据生成到CSV文件中，则可以考虑利用这种方式进行操作。
 * 
 * 1.CsvReporter实战
 * 在构造CsvReporter的时候需要注意的一点是，build方法的入参必须是一个存在的目录地址，以用于存放 Reporter输出的CSV文件，如果该目录不存在则会出现错误。
 * 
 * 2.CSV文件示例
 * CsvReporter不会将MetricRegistry中的所有度量指标输出到一个CSV文件中去，而是会为每一个Metric生成一个CSV文件，
 * 这也是非常容易理解的，毕竟不同的Metric所输出的度量指标数据存在差异。CsvReporter生成的报告文件如图7-9所示。
 * 
 * 用Excel/WPS随便打开其中一个CSV文件，我们会看到这些度量指标数据的表格输出，该Reporter还为我们生成了相应的表头，以便于阅读和理解，如图7-10所示。
 * @author 14378
 *
 */
public class CsvReporterExample {

	private final static MetricRegistry registry = new MetricRegistry();
	
	private final static Counter totalBusiness = new Counter();
	private final static Counter successBusiness = new Counter();
	private final static Counter failBusiness = new Counter();
	private final static Timer timer = new Timer();
	private final static Histogram volumeHisto = new Histogram(new ExponentiallyDecayingReservoir());
	//D:\DevInstall\eclipse-jee-2019-12-R-win32-x86_64\workspace\MultithreadingAndArchitecture
	private final static Path PATH = Paths.get("D:", "DevInstall", "eclipse-jee-2019-12-R-win32-x86_64", "workspace", "MultithreadingAndArchitecture");
	private final static CsvReporter reporter = CsvReporter.forRegistry(registry)
			.convertRatesTo(TimeUnit.SECONDS)
			.convertDurationsTo(TimeUnit.SECONDS)
			.build(PATH.toFile());
	
	private final static RatioGauge successGauge = new RatioGauge() {

		@Override
		protected Ratio getRatio() {
			return Ratio.of(successBusiness.getCount(), totalBusiness.getCount());
		}
		
	};
	
	static {
		registry.register("cloud-disk-upload-total", totalBusiness);
		registry.register("cloud-disk-upload-success", successBusiness);
		registry.register("cloud-disk-upload-failure", failBusiness);
		registry.register("cloud-disk-upload-frequency", timer);
		registry.register("cloud-disk-upload-volume", volumeHisto);
		registry.register("cloud-disk-upload-suc-rate", successGauge);
	}
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		reporter.start(10, TimeUnit.SECONDS);
		while(true) {
			upload(new byte[ThreadLocalRandom.current().nextInt(10_000)]);
		}
	}
	private static void upload(byte[] buffer) {
		// TODO Auto-generated method stub
		totalBusiness.inc();
		Timer.Context context = timer.time();
		try {
			int x = 1 / ThreadLocalRandom.current().nextInt(10);
			TimeUnit.MILLISECONDS.sleep(200);
			volumeHisto.update(buffer.length);
			successBusiness.inc();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			failBusiness.inc();
		}finally {
			context.close();
		}
	}

}
