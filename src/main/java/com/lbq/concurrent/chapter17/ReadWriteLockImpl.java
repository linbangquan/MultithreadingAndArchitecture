package com.lbq.concurrent.chapter17;
/**
 * 相对于Lock，ReadWriteLockImpl更像是一个工厂类，可以通过它创建不同类型的锁，我们将ReadWriteLockImpl设计为包可见的类，
 * 其主要目的是不想对外暴露更多的细节，在ReadWriteLockImpl中还定义了非常多的包可见方法。
 * 虽然我们在开发一个读写锁，但是在实现的内部也需要一个锁进行数据同步以及线程之间的通信，其中Mutex的作用就在于此，而preferWriter的作用在于控制倾向性，
 * 一般来说读写锁非常适用于读多写少的场景，如果preferWriter为false，很多读线程都在读数据，那么写线程将会很难得到写的机会。
 * @author 14378
 *
 */
//包可见，创建时使用ReadWriterLock的create方法
class ReadWriteLockImpl implements ReadWriteLock {

	//定义对象锁
	private final Object MUTEX = new Object();
	//当前有多少个线程正在写入
	private int writingWriters = 0;
	//当前有多少个线程正在等待写入
	private int waitingWriters = 0;
	//当前有多少个线程正在read
	private int readingReaders = 0;
	//read和write的偏好 设置
	private boolean preferWriter;
	//默认情况下perferWriter为true
	public ReadWriteLockImpl() {
		this(true);
	}
	//构造ReadWriteLockImpl并且传入preferWriter
	public ReadWriteLockImpl(boolean preferWriter) {
		this.preferWriter = preferWriter;
	}
	//创建read lock
	@Override
	public Lock readLock() {
		return new ReadLock(this);
	}
	//创建write lock
	@Override
	public Lock writeLock() {
		return new WriteLock(this);
	}
	//使写线程的数量增加
	void incrementWritingWriters() {
		writingWriters++;
	}
	//使写线程的数量减少
	void decrementWritingWriters() {
		writingWriters--;
	}
	//使等待获取写入锁的线程数量增加
	void incrementWaitingWriters() {
		waitingWriters++;
	}
	//使等待获取写入锁的线程数量减少
	void decrementWaitingWriters() {
		waitingWriters--;
	}
	//使读线程的数量增加
	void incrementReadingReaders() {
		readingReaders++;
	}
	//使读线程的数量减少
	void decrementReadingReaders() {
		readingReaders--;
	}
	//获取当前有多少个线程正在进行写操作
	@Override
	public int getWritingWriters() {
		return writingWriters;
	}
	//获取当前有多少个线程正在等待获取写入锁
	@Override
	public int getWaitingWriters() {
		return waitingWriters;
	}
	//获取当前多少个线程正在进行读操作
	@Override
	public int getReadingReaders() {
		return readingReaders;
	}
	//获取对象锁
	Object getMutex() {
		return MUTEX;
	}
	//获取当前是否偏向写锁
	boolean getPreferWriter() {
		return preferWriter;
	}
	//设置写锁偏好
	void changePrefer(boolean preferWriter) {
		this.preferWriter = preferWriter;
	}
}
