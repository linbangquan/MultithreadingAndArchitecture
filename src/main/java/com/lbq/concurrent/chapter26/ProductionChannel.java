package com.lbq.concurrent.chapter26;
/**
 * 26.2.2 流水线传送带
 * 流水线的传送带主要用于传送待加工的产品，上游的工作人员将完成的半成品放到传送带上，工作人员从传送带上取下产品进行再次加工，
 * 传送带ProductionChannel的代码如下：
 * 
 * 产品传送带除了负责产品加工的工人之外，还有在传送带上等待加工的产品
 * 
 * 传送带是搁置产品的地方，如果工人们处理比较慢则会导致无限制的产品积压，因此我们需要做的是让上游的流水线阻塞并且等待，直至流水线有位置可以用于放置新的产品为止，
 * MAX_PROD的作用就在于此，其用于控制传送带的最大容量，传送带被创建的同时，流水线上的工人们也已经就绪到位，等待着流水线产品的到来。
 * @author 14378
 *
 */
public class ProductionChannel {
	//传送带上最多可以有多少个待加工的产品
	private final static int MAX_PROD = 100;
	//主要用来存放待加工的产品，也就是传送带
	private final Production[] productionQueue;
	//队列尾
	private int tail;
	//队列头
	private int head;
	//当前在流水线上有多少个待加工的产品
	private int total;
	//在流水线上工作的工人
	private final Worker[] workers;
	/**
	 * 创建ProductionChannel时应指定需要多少个流水线工人
	 * @param workerSize
	 */
	public ProductionChannel(int workerSize) {
		this.workers = new Worker[workerSize];
		this.productionQueue = new Production[MAX_PROD];
		//实例化每一个工人(Worker线程)并且启动
		for(int i = 0; i < workerSize; i++) {
			workers[i] = new Worker("Worker-" + i, this);
			workers[i].start();
		}
	}
	/**
	 * 接受来自上游的半成品(待加工的产品)
	 * @param production
	 */
	public void offerProduction(Production production) {
		synchronized(this) {
			//当传送带上待加工的产品超过了最大值时需要阻塞上游再次传送产品
			while(total >= productionQueue.length) {
				try {
					this.wait();
				}catch(InterruptedException e) {
					
				}
			}
			//将产品放到传送带，并且通知工人线程工作
			productionQueue[tail] = production;
			tail = (tail + 1) % productionQueue.length;
			total++;
			this.notifyAll();
		}
	}
	/**
	 * 工人线程(worker)从传送带上获取产品，并且进行加工
	 * @return
	 */
	public Production takeProduction() {
		synchronized (this) {
			//当传送带上没有产品时，工人等待着产品从上游输送到传送带
			while(total <= 0) {
				try {
					this.wait();
				}catch(InterruptedException e) {
					
				}
			}
			//获取产品
			Production prod = productionQueue[head];
			head = (head + 1) % productionQueue.length;
			total--;
			this.notifyAll();
			return prod;
		}
	}
}
