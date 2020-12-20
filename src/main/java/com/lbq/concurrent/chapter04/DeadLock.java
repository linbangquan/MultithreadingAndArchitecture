package com.lbq.concurrent.chapter04;
/**
 * 程序死锁
 * 1.交叉锁可导致程序出现死锁
 * 线程A持有R1的锁等待获取R2的锁，线程B持有R2的锁等待获取R1的锁，这种情况最容易导致程序发生死锁的问题。
 * 2.内存不足
 * 当并发请求系统可用内存时，如果此时系统内存不足，则可能会出现死锁的情况。
 * 举个例子，两个线程T1和T2，执行某个任务，其中T1已经获取了10MB内存，T2获取了20MB,
 * 如果每个线程的执行单元都需要30MB的内存，但是剩余可用的内存刚好为20MB，
 * 那么两个线程有可能都在等待彼此能够释放内存资源。
 * 3.一问一答式的数据交换
 * 服务端开启某个端口，等待客户端访问，客户端发送请求立即等待接收，由于某种原因服务器错过了客户端的请求，
 * 仍然在等待一问一答式的数据交换，此时服务器和客户端都在等待着双方发送数据。
 * 4.数据库锁
 * 无论是数据库表级别的锁，还是行级别的锁，比如某个线程执行for update语句退出了事务，其他线程访问该数据库时都将陷入死锁。
 * 5.文件锁
 * 同理，某个线程获得了文件锁意外退出，其他读取该文件的线程也将会进入死锁直到系统释放文件句柄资源。
 * 6.死循环引起的死锁
 * 程序由于代码原因或者对某些异常处理不得当，进入了死循环，虽然查看线程堆栈信息不会发现任何死锁的迹象，
 * 但是程序不工作， CPU占有率又居高不下，这种死锁一般称为系统假死，是一种最为致命也是最难排查的死锁现象，
 * 由于重现困难，进程对系统资源的使用量又达到了极限，想要做出dump有时候也是非常困难的。
 * @author 14378
 *
 */
public class DeadLock {

	private final Object MUTEX_READ = new Object();
	private final Object MUTEX_WRITE = new Object();
	
	public void read() {
		synchronized(MUTEX_READ) {
			System.out.println(Thread.currentThread().getName() + " get READ lock");
			synchronized(MUTEX_WRITE) {
				System.out.println(Thread.currentThread().getName() + "get WRITE lock");
			}
			System.out.println(Thread.currentThread().getName() + " release WRITE lock");
		}
		System.out.println(Thread.currentThread().getName() + " release READ lock");
	}
	
	public void write() {
		synchronized(MUTEX_WRITE) {
			System.out.println(Thread.currentThread().getName() + " get WRITE lock");
			synchronized(MUTEX_READ) {
				System.out.println(Thread.currentThread().getName() + "get READ lock");
			}
			System.out.println(Thread.currentThread().getName() + " release READ lock");
		}
		System.out.println(Thread.currentThread().getName() + " release WRITE lock");
	}
	public static void main(String[] args) {
		final DeadLock deadLock = new DeadLock();
		new Thread(() -> {
			while(true) {
				deadLock.read();
			}
		}, "READ-THREAD").start();

		new Thread(() -> {
			while(true) {
				deadLock.write();
			}
		}, "WRITE-THREAD").start();
	}

}

//		"WRITE-THREAD" #12 prio=5 os_prio=0 tid=0x0000000019ada800 nid=0xa04 waiting for monitor entry [0x000000001aedf000]
//		   java.lang.Thread.State: BLOCKED (on object monitor)
//			at com.lbq.concurrent.chapter04.DeadLock.write(DeadLock.java:23)
//			- waiting to lock <0x00000000d5e57948> (a java.lang.Object)
//			- locked <0x00000000d5e57958> (a java.lang.Object)
//			at com.lbq.concurrent.chapter04.DeadLock.lambda$1(DeadLock.java:39)
//			at com.lbq.concurrent.chapter04.DeadLock$$Lambda$2/1418481495.run(Unknown Source)
//			at java.lang.Thread.run(Thread.java:748)
//
//		"READ-THREAD" #11 prio=5 os_prio=0 tid=0x0000000019ac6000 nid=0x2808 waiting for monitor entry [0x000000001adde000]
//		   java.lang.Thread.State: BLOCKED (on object monitor)
//			at com.lbq.concurrent.chapter04.DeadLock.read(DeadLock.java:12)
//			- waiting to lock <0x00000000d5e57958> (a java.lang.Object)
//			- locked <0x00000000d5e57948> (a java.lang.Object)
//			at com.lbq.concurrent.chapter04.DeadLock.lambda$0(DeadLock.java:33)
//			at com.lbq.concurrent.chapter04.DeadLock$$Lambda$1/471910020.run(Unknown Source)
//			at java.lang.Thread.run(Thread.java:748)
			
			