package com.lbq.concurrent.chapter07;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.PosixFilePermission;
import java.nio.file.attribute.PosixFilePermissions;
import java.util.Set;
import java.util.concurrent.TimeUnit;
/**
 * 在我们的开发中经常会遇到Hook线程，比如为了防止某个程序被重复启动，在进程启动时会创建一个lock文件，进程收到中断信号的时候会删除这个lock文件，
 * 我们的mysql服务器、zookeeper、Kafka等系统中都能看到lock文件的存在。
 * 
 * Hook线程应用场景以及注意事项
 * 1.Hook线程只有在收到退出信号的时候会被执行，如果在kill的时候使用了参数-9，那么Hook线程不会得到执行，进程将会立即退出，因此.lock文件将得不到清理。
 * 2.Hook线程中也可以执行一些资源释放的工作，比如关闭文件句柄、socket链接、数据库connection等。
 * 3.尽量不要在Hook线程中执行一些耗时非常长的操作，因为其会导致程序迟迟不能退出。
 * @author 14378
 *
 */
public class PreventDuplicated {

	private final static String LOCK_PATH = "home/wanwenjun/locks/";
	
	private final static String LOCK_FILE = ".lock";
	
	private final static String PERMISSIONS = "rw-------";
	public static void main(String[] args) throws IOException {
		// ①注入Hook线程，在程序退出时删除lock文件
		Runtime.getRuntime().addShutdownHook(new Thread(() -> {
			System.out.println("The program received kill SIGNAL.");
			getLockFile().toFile().delete();
		}));
		
		checkRunning();
		
		for(;;) {
			try {
				TimeUnit.MILLISECONDS.sleep(1);
				System.out.println("program is running.");
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	private static Path getLockFile() {
		return Paths.get(LOCK_PATH, LOCK_FILE);
	}
	private static void checkRunning() throws IOException {
		Path path = getLockFile();
		if(path.toFile().exists()) {
			throw new RuntimeException("The program already running.");
		}
		Set<PosixFilePermission> perms = PosixFilePermissions.fromString(PERMISSIONS);
		Files.createFile(path, PosixFilePermissions.asFileAttribute(perms));
	}

}
