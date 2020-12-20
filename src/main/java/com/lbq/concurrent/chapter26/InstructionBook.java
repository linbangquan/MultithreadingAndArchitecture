package com.lbq.concurrent.chapter26;
/**
 * 26.2.1 产品及组装说明书
 * 抽象类InstructionBook，代表着组装产品的说明书，其中经过流水线传送带的产品将通过create()方法进行加工，
 * 而firstProcess()和secondProcess()则代表着加工每个产品的步骤，这就是说明书的作用。
 * 
 * 在流水线上需要被加工的产品，create作为一个模板方法，提供了加工产品的说明书。
 * @author 14378
 *
 */
public abstract class InstructionBook {

	public final void create() {
		this.firstProcess();
		this.secondProcess();
	}

	protected abstract void firstProcess();

	protected abstract void secondProcess();
	
}
