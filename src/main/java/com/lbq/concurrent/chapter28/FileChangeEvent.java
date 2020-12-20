package com.lbq.concurrent.chapter28;

import java.nio.file.Path;
import java.nio.file.WatchEvent;
/**
 * 28.2.2 FileChangeEvent
 * FileChangeEvent比较简单，就是对WatchEvent.Kind和Path的包装，一旦目录发生任何改变，都会提交FileChangeEvent事件。
 * @author 14378
 *
 */
public class FileChangeEvent {

	private final Path path;
	private final WatchEvent.Kind<?> kind;
	
	public FileChangeEvent(Path path, WatchEvent.Kind<?> kind) {
		this.path = path;
		this.kind = kind;
	}

	public Path getPath() {
		return path;
	}

	public WatchEvent.Kind<?> getKind() {
		return kind;
	}
}
