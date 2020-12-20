package com.lbq.concurrent.chapter28;
/**
 * 28.2.3 监控目录变化
 * 好了，目录监控的程序我们已经实现了，下面就来写一个接受文件目录变化的Subscriber，也就是当目录发生变化时用来接受事件的方法。
 * onChange方法由@Subscribe标记，但没有指定topic，当有事件发送到了默认的topic上之后，该方法将被调用执行，
 * 接下来我们将FileChangeListener的实例注册给Event Bus并且启动Monitor程序。
 * @author 14378
 *
 */
public class FileChangeListener {
	@Subscribe
	public void onChange(FileChangeEvent event) {
		System.out.printf("%s-%s\n", event.getPath(), event.getKind());
	}
}
