package com.lbq.concurrent.chapter22;

import java.io.IOException;
import java.util.concurrent.TimeUnit;
/**
 * 与平日里编写word文档一样，word会定期自动保存我们编辑的文档，如果在电脑出现故障重启之时，
 * 没有来得及对文档保存，也不至于损失太多劳动成果，他甚至能够百分之百的恢复，AutoSaveThread类扮演的角色便在于此。
 * AutoSaveThread比较简单，其主要的工作就是每隔一秒的时间调用一次document的save方法。
 * @author 14378
 *
 */
public class AutoSaveThread extends Thread {

	private final Document document;
	
	public AutoSaveThread(Document document) {
		super("DocumentAutoSaveThread");
		this.document = document;
	}
	
	@Override
	public void run() {
		while(true) {
			try {
				//每隔一秒自动保存一次文档
				document.save();
				TimeUnit.SECONDS.sleep(1);
			}catch(IOException | InterruptedException e) {
				break;
			}
		}
	}
}
