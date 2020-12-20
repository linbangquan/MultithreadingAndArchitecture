package com.lbq.concurrent.chapter22;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
/**
 * 22.2 Balking模式之文档编辑
 * 22.2.1 Document
 * 设计了Document类代表文档本身，在Document中有两个主要方法save和edit分别用于保存文档和编辑文档。
 * 在下述代码中：
 * 1.edit方法和save方法进行方法同步，其目的在于防止当文档在保存的过程中如果遇到新的内容被编辑时引起的共享资源冲突问题。
 * 2.changed在默认情况下为false，当有新的内容被编辑的时候将被修改为true。
 * 3.在进行文档保存的时候，首先查看changed是否为true，如果文档发生过编辑则在文档保存新的内容，否则就会放弃此次保存动作，
 * changed是balking pattern关注的状态，当changed为false的时候就像远处的服务员看到客户的请求被另外一个服务员接管了一样，
 * 于是放弃了任务的执行。
 * 4.在创建Document的时候，顺便还会启动自动保存文档的线程，该线程的主要目的在于在固定的时间里执行一次文档保存动作。
 * @author 14378
 *
 */
/** 代表正在编辑的文档类 **/
public class Document {
	/** 如果文档发生改变，changed会被设置为true **/
	private boolean changed = false;
	
	/** 一次需要保存的内容，可以将其理解为内容缓存 **/
	private List<String> content = new ArrayList<>();
	
	private final FileWriter writer;
	
	/** 自动保存文档的线程 **/
	private static AutoSaveThread autoSaveThread;
	
	/** 构造函数需要传入文档保存的路径和文档名称 **/
	private Document(String documentPath, String documentName) throws IOException {
		this.writer = new FileWriter(new File(documentPath, documentName), true);
	}
	
	/** 静态方法，主要用于创建文档，顺便启动 自动保存文档的线程 **/
	public static Document create(String documentPath, String documentName) throws IOException {
		Document document = new Document(documentPath, documentName);
		autoSaveThread = new AutoSaveThread(document);
		autoSaveThread.start();
		return document;
	}
	
	/** 文档编辑，其实就是往content队列中提交字符串 **/
	public void edit(String content) {
		synchronized(this) {
			this.content.add(content);
			//文档改变，changed会变为true
			this.changed = true;
		}
	}
	
	/** 文档关闭的时候首先中断自动保存线程，然后关闭writer释放资源 **/
	public void close() throws IOException {
		autoSaveThread.interrupt();
		writer.close();
	}
	
	/** save方法用于为外部显式进行文档保存 **/
	public void save() throws IOException {
		synchronized (this) {
			//balking，如果文档已经被保存了，则直接返回
			if(!changed) {
				return;
			}
			System.out.println(Thread.currentThread() + " execute the save action");
			//将内容写入文档中
			for(String cacheLine : content) {
				this.writer.write(cacheLine);
				this.writer.write("\r\n");
			}
			this.writer.flush();
			//将changed修改为false，表明此刻再没有新的内容编辑
			this.changed = false;
			this.content.clear();
		}
	}
}
