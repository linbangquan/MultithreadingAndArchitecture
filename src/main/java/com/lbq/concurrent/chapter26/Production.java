package com.lbq.concurrent.chapter26;
/**
 * 传送带上的产品除了说明书以外还需要有产品自身，产品继承了说明书，每个产品都有产品编号，通用Production的代码如下。
 * @author 14378
 *
 */
public class Production extends InstructionBook {
	//产品编号
	private final int prodID;
	
	public Production(int prodID) {
		this.prodID = prodID;
	}
	@Override
	protected void firstProcess() {
		System.out.println("execute the " + prodID + " first process");
	}

	@Override
	protected void secondProcess() {
		System.out.println("execute the " + prodID + " second process");
	}

}
