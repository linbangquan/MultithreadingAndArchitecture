package com.lbq.concurrent.chapter17;
/**
 * 读锁是Lock的实现，同样将其设计成包可见以透明其实现细节，让使用者只用专注于对接口的调用。
 * 1.当没有任何线程对数据进行写操作的时候，读线程才有可能获得锁的拥有权，当然除此之外，为了公平起见，
 * 如果当前有很多线程正在等待获得写锁的拥有权，同样读线程将会进入Mutex的waitset中，readingReader的数量将增加。
 * 2.读线程释放锁，这意味着reader的数量将减少一个，同时唤醒wait中的线程，reader唤醒的基本上都是由于获取写锁而进入阻塞的线程，
 * 为了提高写锁获得锁的机会，需要将preferWriter修改为true。
 * @author 14378
 *
 */
//ReadLock被设计为包可见
public class ReadLock implements Lock {

	private final ReadWriteLockImpl readWriteLock;
	
	public ReadLock(ReadWriteLockImpl readWriteLock) {
		this.readWriteLock = readWriteLock;
	}

	@Override
	public void lock() throws InterruptedException {
		// 使用Mutex作为锁
		synchronized(readWriteLock.getMutex()) {
			//若此时有线程在进行写操作，或者有写线程在等待并且偏好写锁的标识为true时，就会无法获得读锁，只能被挂起
			while(readWriteLock.getWritingWriters() > 0 || (readWriteLock.getPreferWriter() && readWriteLock.getWaitingWriters() > 0)) {
				readWriteLock.getMutex().wait();
			}
		}
		//成功获得读锁，并且使readingReaders的数量增加
		readWriteLock.incrementReadingReaders();
	}

	@Override
	public void unlock() {
		// 使用Mutex作为锁，并且进行同步
		synchronized(readWriteLock.getMutex()) {
			//释放锁的过程就是使得当前reading的数量减一
			//将perferWriter设置为true，可以使得writer线程获得更多的机会
			//通知唤醒与Mutex关联monitor waitset中的线程
			readWriteLock.decrementReadingReaders();
			readWriteLock.changePrefer(true);
			readWriteLock.getMutex().notifyAll();
		}
		
	}

}
