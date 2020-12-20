package com.lbq.concurrent.chapter15;
/**
 * 15.2.2 ObservableThread实现
 * ObservableThread是任务监控的关键，它继承自Thread类和Observable接口，并且在构造期间需要传入Task的具体实现。
 * 		重写父类的run方法，并且将其修饰为final类型，不允许子类再次对其进行重写，run方法在线程的运行期间，
 * 可监控任务在执行过程中的各个生命周期阶段，任务每经过一个阶段相当于发生了一次事件。
 * 		update方法用于通知时间的监听者，此时任务在执行过程中发生了什么，最主要的通知是异常的处理。
 * 如果监听也就是TaskLifecycle，在响应某个事件的过程中出现了意外，则会导致任务的正常执行受到影响，
 * 因此需要进行异常捕获，并忽略这些异常信息以保证TaskLifecycle的实现不影响任务的正确执行，
 * 但是如果任务执行过程中出现错误并且抛出了异常，那么update方法就不能忽略该异常，需要继续抛出异常，保持与call方法同样的意图。
 * @author 14378
 *
 * @param <T>
 */
public class ObservableThread<T> extends Thread implements Observable {

	private final TaskLifecycle<T> lifecycle;
	
	private final Task<T> task;
	
	private Cycle cycle;
	
	//指定Task的实现，默认情况下使用EmptyLifecycle
	public ObservableThread(Task<T> task) {
		this(new TaskLifecycle.EmptyLifecycle<>(), task);
	}
	
	//指定TaskLifecycle的同时指定Task
	public ObservableThread(TaskLifecycle<T> lifecycle, Task<T> task) {
		super();
		//Task不允许为null
		if(task == null)
			throw new IllegalArgumentException("The task is required.");
		this.lifecycle = lifecycle;
		this.task = task;
	}
	@Override
	public final void run() {
		//在执行线程逻辑单元的时候，分别触发相应的事件
		this.update(Cycle.STARTED, null, null);
		try {
			this.update(Cycle.RUNNING, null, null);
			T result = this.task.call();
			this.update(Cycle.DONE, result, null);
		}catch(Exception e) {
			this.update(Cycle.ERROR, null, e);
		}
	}
	private void update(Cycle cycle, T result, Exception e) {
		this.cycle = cycle;
		if(lifecycle == null) {
			return;
		}
		try {
			switch(cycle) {
			case STARTED:
				this.lifecycle.onStart(currentThread());
				break;
			case RUNNING:
				this.lifecycle.onRunning(currentThread());
				break;
			case DONE:
				this.lifecycle.onFinish(currentThread(), result);
				break;
			case ERROR:
				this.lifecycle.onError(currentThread(), e);
				break;
			}
		}catch(Exception ex) {
			if(cycle == Cycle.ERROR) {
				throw ex;
			}
		}
	}

	@Override
	public Cycle getCycle() {
		return this.cycle;
	}

}
