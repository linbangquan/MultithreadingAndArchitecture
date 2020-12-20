package com.lbq.concurrent.chapter27.activeobject;
/**
 * 若方法不符合则其被转换为Active方法时会抛出该异常。
 * @author 14378
 *
 */
public class IllegalActiveMethod extends Exception {

	private static final long serialVersionUID = -2383497145309576836L;

	public IllegalActiveMethod(String message) {
		super(message);
	}
}
