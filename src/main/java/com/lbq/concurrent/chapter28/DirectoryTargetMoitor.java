package com.lbq.concurrent.chapter28;

import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
/**
 * 28.2.1 WatchService遇到了EventBus
 * 在创建WatchService之后将文件的修改、删除、创建等注册给了WatchService，在指定目录下发生诸如此类的事件之后便会收到通知，
 * 我们将事件类型和发生变化的文件Path封装成FileChangeEvent提交给EventBus。
 * @author 14378
 *
 */
public class DirectoryTargetMoitor {

	private WatchService watchService;
	
	private final EventBus eventBus;
	
	private final Path path;
	
	private volatile boolean start = false;
	
	public DirectoryTargetMoitor(final EventBus eventBus, final String targetPath) {
		this(eventBus, targetPath, "");
	}
	/**
	 * 构造Monitor的时候需要传入EventBus以及需要监控的目录
	 * @param eventBus
	 * @param targetPath
	 * @param morePaths
	 */
	public DirectoryTargetMoitor(final EventBus eventBus, final String targetPath, final String ... morePaths) {
		this.eventBus = eventBus;
		this.path = Paths.get(targetPath, morePaths);
	}
	
	public void startMonitor() throws Exception {
		this.watchService = FileSystems.getDefault().newWatchService();
		//为路径注册感兴趣的事件
		this.path.register(watchService, StandardWatchEventKinds.ENTRY_MODIFY, StandardWatchEventKinds.ENTRY_DELETE, StandardWatchEventKinds.ENTRY_CREATE);
		
		System.out.printf("The directory [%s] is monitoring ... \n", path);
		this.start = true;
		while(start) {
			WatchKey watchKey = null;
			try {
				//当有事件发生时会返回对应的WatchKey
				watchKey = watchService.take();
				watchKey.pollEvents().forEach(event -> {
					WatchEvent.Kind<?> kind = event.kind();
					Path path = (Path) event.context();
					Path child = DirectoryTargetMoitor.this.path.resolve(path);
					//提交FileChangeEvent到EventBus
					eventBus.post(new FileChangeEvent(child, kind));
				});
			}catch(Exception e) {
				this.start = false;
			}finally {
				if(watchKey != null) {
					watchKey.reset();
				}
			}
		}
	}
	
	public void stopMonitor() throws Exception {
		System.out.printf("The directory [%s] monitor will be stop ...\n", path);
		Thread.currentThread().interrupt();
		this.start = false;
		this.watchService.close();
		System.out.printf("The directory [%s] monitor will be stop done.\n, path");
	}
}
