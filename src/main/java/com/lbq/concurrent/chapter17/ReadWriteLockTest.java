package com.lbq.concurrent.chapter17;
/**
 * ShareData中涉及了对数据的读写操作，因此它是需要进行线程同步控制的。首先，创建一个ReadWriteLock工厂类，然后用该工厂分别创建ReadLock和WriteLock的实例，
 * 在read方法中使用ReadLock对其进行加锁，而在write方法中则使用WriteLock。
 * 为了在读多于写的场景中体现读写锁，我们创建了10个读的线程和两个写的线程，上面的程序需要你借助于Ctrl+Break进行停止，
 * 虽然我们比较完整地完成了一个读写锁的设计并且可以投入使用，但是还是存在着一定的缺陷和一些需要补强的地方，比如可以结合BooleanLock的方式增加超时的功能，
 * 提供用于查询哪些线程被陷入阻塞的方法，判断当前线程是否被某个lock锁定等，读者可以根据阅读本书所掌握的知识自行拓展。
 * 
 * 17.4 本章总结
 * JDK版本的几乎每一次升级都会看到对锁的优化，就连synchronized关键字的性能都得到了很大得提升，
 * 在某些场合下直接使用synchronized关键字甚至要比java.util.concurrent.locks包下提供的锁性能更佳，
 * 在jdk的并发包下包含了读写锁的实现，可见Java官方对读写锁分离方案的重视。
 * 
 * RW锁(读写锁)允许多个线程在同一时间对共享资源进行读取操作，在读明显多于写的场合下，其对性能的提升是非常明显的，
 * 但是如果使用不当性能反倒会比较差，比如在写线程的数量和读线程的数量接近甚至多于读线程情况下，因此在jdk1.8中又增加了StampedLock的解决方案。
 * 
 * 笔者在自己的电脑上做了三组(每组5轮)测试，分别对比了在不同数量的读写线程情况下的性能，测试中StampedLock和ReadWriteLock都是Java并发包提供的显示锁，
 * 测试的主要目的是要读者掌握在不同的场景下该选择怎样的锁机制，测试对比结果分别如表17-2、表17-3、表17-4所示。
 * 1.5个reader vs 5个writer
 * 最后一行为五轮测试的平均值，五个线程进行读操作，五个线程进行写操作的情况下StampedLock的表现是最优秀的，读写锁次之，性能最差的就当属synchronized关键字了。
 * 2.10个reader vs 10个writer
 * synchronized关键字的表现很稳定，几组测试数据抖动不大，性能仅次于StampedLock，如果加大写的线程数量synchronized关键字的表现会是最优秀的，
 * 但是读写锁的性能骤降，和其他两个对照组相比简直就是数量级的降低。
 * 3.16个reader vs writer
 * 在读的情况比较多的情况下，读写分离锁的性能优势也体现出来了，如果读者使用的是JDK1.8的开发环境，
 * 那么强烈建议直接使用StampedLock，该锁提供了一种乐观的机制，性能在目前来说是最好的，因此被称为lock家族的“宠儿”。
 * @author 14378
 *
 */
public class ReadWriteLockTest {

	private final static String text = "Thisistheexampleforreadwritelock";
	
	public static void main(String[] args) {
		// 定义共享数据
		final ShareData shareData = new ShareData(50);
		//创建两个线程进行数据写操作
		for(int i = 0; i < 2; i++) {
			new Thread(() -> {
				for(int index = 0; index < text.length(); index++) {
					
					try {
						char c = text.charAt(index);
						shareData.write(c);
						System.out.println(Thread.currentThread() + " write " + c);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}).start();
		}
		//创建10个线程进行数据读操作
		for(int i = 0; i < 10; i++) {
			new Thread(() -> {
				while(true) {
					try {
						System.out.println(Thread.currentThread() + " read " + new String(shareData.read()));
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}).start();
		}
	}

}
