package com.lbq.concurrent.metrics.metricset;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

import com.codahale.metrics.Counter;
import com.codahale.metrics.ExponentiallyDecayingReservoir;
import com.codahale.metrics.Histogram;
import com.codahale.metrics.Metric;
import com.codahale.metrics.MetricSet;
import com.codahale.metrics.RatioGauge;
import com.codahale.metrics.Timer;
/**
 * 7.5.3 拾遗补漏
 * 关于Metrics的内容就写到这里了，事实上这已经基本上涵盖了绝大多数细节，但是有两个不是很常用的类还是有必要交代一下的。
 * 1.MetricSet
 * MetricSet不仅是一个特殊的Metric，还是一个存放Metric的集合，之前的代码通过若干次的register方法调用将对应的Metric实例注册到Metric Registry中，
 * 这种方式会稍显麻烦，如果是通过MetricSet接口，那么注册的过程就会简洁很多，另外MetricSet的出现，可以便于对某些不同类型的Metric进行归类，
 * 下面来看一个比较有综合性的练习，同样还是以模拟度量网络磁盘的上传服务为例进行说明，示例代码如下。
 * 
 * 1.我们的业务类不仅实现了MetricSet接口，而且还实现了getMetrics方法。
 * 2.在该业务类中，将所有需要度量的Metric加入metric中，并作为getMetrics方法的返回值。
 * 3.BusinessService是一个Metric，可以直接加入MetricRegistry中。
 * @author 14378
 *
 */
//业务类，除了继承Thread之外，还实现了MetricSet
public class BusinessService extends Thread implements MetricSet {

	private final Map<String, Metric> metrics = new HashMap<>();
	//定义了若干个Metric
	private final Counter totalBusiness = new Counter();
	private final Counter successBusiness = new Counter();
	private final Counter failBusiness = new Counter();
	private final Timer timer  = new Timer();
	private final Histogram volumeHisto = new Histogram(new ExponentiallyDecayingReservoir());
	private final RatioGauge successGauge = new RatioGauge() {

		@Override
		protected Ratio getRatio() {
			return Ratio.of(successBusiness.getCount(), totalBusiness.getCount());
		}
		
	};
	
	public BusinessService() {
		//将所有的Metric存入metrics中
		metrics.put("cloud-disk-upload-total", totalBusiness);
		metrics.put("cloud-disk-upload-success", successBusiness);
		metrics.put("cloud-disk-upload-failure", failBusiness);
		metrics.put("cloud-disk-upload-frequency", timer);
		metrics.put("cloud-disk-upload-volume", volumeHisto);
		metrics.put("cloud-disk-upload-suc-rate", successGauge);
	}
	@Override
	public void run() {
		while(true) {
			upload(new byte[ThreadLocalRandom.current().nextInt(10_000)]);
		}
	}
	private void upload(byte[] buffer) {
		totalBusiness.inc();
		Timer.Context context = timer.time();
		try {
			int x = 1 / ThreadLocalRandom.current().nextInt(10);
			TimeUnit.MILLISECONDS.sleep(200);
			volumeHisto.update(buffer.length);
			successBusiness.inc();
		}catch(Exception e) {
			failBusiness.inc();
		}finally {
			context.close();
		}
		
	}
	//实现MetricSet方法，返回所有的metrics集合（Map）
	@Override
	public Map<String, Metric> getMetrics() {
		return metrics;
	}

}
