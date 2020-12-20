package com.lbq.concurrent.chapter17;
/**
 * ReadWriteLock虽然名字中有lock，但是它并不是lock，它主要是用于创建read lock和write lock的，
 * 并且提供了查询功能用于查询当前有多少个Reader和Writer以及waiting中的writer，根据我们在前文中的分析，
 * 如果reader的个数大于0，那就意味着writer的个数等于0，反之writer的个数大于0(事实上writer最多只能为1)，
 * 则reader的个数等于0，由于读和写，写和写之间都存在着冲突，因此这样的数字关系也就不奇怪了。
 * 1.readLock():该方法主要用来获得一个ReadLock。
 * 2.writeLock():同readLock类似，该方法用来获得WriteLock。
 * 3.getWritingWriters():获取当前有多少个线程正在进行写的操作，最多是一个。
 * 4.getWaitingWriters():获取当前有多少个线程由于获得写锁而导致阻塞。
 * 5.getReadingReaders():获取当前有多少个线程正在进行读的操作。
 * @author 14378
 *
 */
public interface ReadWriteLock {
	//创建reader锁
	Lock readLock();
	//创建write锁
	Lock writeLock();
	//获取当前有多少线程正在执行写操作
	int getWritingWriters();
	//获取当前有多少线程正在等待获取写入锁
	int getWaitingWriters();
	//获取当前有多少线程正在执行读操作
	int getReadingReaders();
	//工厂方法，创建ReadWriteLock
	static ReadWriteLock readWriteLock() {
		return new ReadWriteLockImpl();
	}
	//工厂方法，创建ReadWriteLock，并且传入preferWriter
	static ReadWriteLock readWriteLock(boolean preferWriter) {
		return new ReadWriteLockImpl(preferWriter);
	}
}
