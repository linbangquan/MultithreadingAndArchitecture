package com.lbq.concurrent.chapter16;
/**
 * 16.2 吃面问题
 * 在本节中，我们将模拟两个人吃意大利面的场景，来演示交叉所导致的程序死锁情况。
 * 
 * 16.2.1 吃面引起的死锁
 * 虽然使用synchronized关键字可以保证Single Thread Execution，但是如果使用不得当则会导致死锁的情况发生，
 * 比如A手持刀等待B放下叉，而B手持叉等待A放下刀，示例代码如下。
 * @author 14378
 *
 */
public class Tableware {

	//餐具名称
	private final String toolName;
	
	public Tableware(String toolName) {
		this.toolName = toolName;
	}
	
	@Override
	public String toString() {
		return "Tool: " + toolName;
	}
}
