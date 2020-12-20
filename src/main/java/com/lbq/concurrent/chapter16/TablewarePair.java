package com.lbq.concurrent.chapter16;
/**
 * 16.2.2 解决吃面引起的死锁问题
 * 虽然我们使用了Single Thread Execution 对eat加以控制，但还是出现了死锁，其主要原因是交叉锁导致两个线程之间相互等待彼此释放持有的锁，关于这一点我们在4.5节中有过比较详细的介绍。
 * 为了解决交叉锁的情况，我们需要将刀叉进行封装，使刀叉同属于一类中，改进代码如下。
 * @author 14378
 *
 */
public class TablewarePair {

	private final Tableware leftTool;
	
	private final Tableware rightTool;

	public TablewarePair(Tableware leftTool, Tableware rightTool) {
		this.leftTool = leftTool;
		this.rightTool = rightTool;
	}

	public Tableware getLeftTool() {
		return leftTool;
	}

	public Tableware getRightTool() {
		return rightTool;
	}

}
