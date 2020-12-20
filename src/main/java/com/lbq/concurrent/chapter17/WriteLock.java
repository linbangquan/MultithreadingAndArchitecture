package com.lbq.concurrent.chapter17;
/**
 * 写锁是Lock的实现，同样将其设计成包可见以透明其实现细节，让使用者只用专注于对接口的调用，由于写——写冲突的存在，同一时间只能由一个线程获得锁的拥有权。
 * 1.当有线程在进行读操作或者写操作的时候，若当前线程试图获得锁，则其将会进入Mutex的wait set中而阻塞，同时增加waitingWriter和writingWriter的数量，
 * 但是当线程从wait set中被激活的时候waitingWriter将很快被减少。
 * 2.写释放锁，意味着writer的数量减少，事实上变成了0，同时唤醒wait中的线程，并将preferWriter修改为false，以提高读线程获得锁的机会。
 * @author 14378
 *
 */
//WriteLock被设计为包可见
public class WriteLock implements Lock {

	private final ReadWriteLockImpl readWriteLock;
	
	public WriteLock(ReadWriteLockImpl readWriteLock) {
		this.readWriteLock = readWriteLock;
	}

	@Override
	public void lock() throws InterruptedException {
		synchronized(readWriteLock.getMutex()) {
			try {
				//首先使等待获取写入锁的数字加一
				readWriteLock.incrementWaitingWriters();
				//如果此时有其他线程正在进行读操作，或者写操作，那么当前线程将被挂起
				while(readWriteLock.getReadingReaders() > 0 || readWriteLock.getWritingWriters() > 0) {
					readWriteLock.getMutex().wait();
				}
			}finally {
				//成功获取到了写入锁，使得等待获取写入锁的计数器减一
				this.readWriteLock.decrementWaitingWriters();
			}
			//将正在写入的线程数量加一
			readWriteLock.incrementWritingWriters();
		}
		
	}

	@Override
	public void unlock() {
		synchronized(readWriteLock.getMutex()){
			//减少正在写入锁的线程计数器
			readWriteLock.decrementWritingWriters();
			//将偏好状态修改为false，可以使得读锁被最快速的获得
			readWriteLock.changePrefer(false);
			//通知唤醒其他在Mutex monitor waitset中的线程
			readWriteLock.getMutex().notifyAll();
		}
	}

}
