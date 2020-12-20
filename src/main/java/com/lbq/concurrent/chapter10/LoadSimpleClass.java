package com.lbq.concurrent.chapter10;
/**
 * 2.运行时包
 * 我们在编写代码的时候通常会给一个类指定一个包名，包的作用是为了组织类，防止不同包下同样名称的class引起冲突，还能起到封装的作用，包名和类名构成了类的全限定名称。
 * 在JVM运行时class会有一个运行时包，运行时的包是由类加载器的命名空间和类的全限定名称共同组成的，比如Test的运行时包如下所示：
 * BootstrapClassLoader.ExtClassLoader.AppClassLoader.MyClassLoader.com.wangwenjun.concurrent.chapter10.Test
 * 这样做的好处同样是出于安全和封装的考虑，在java.lang.String中存在仅包可见的方法void getChars(char[] var1, int var2)，java.lang包以外的class是无法直接对其访问的。
 * 、假设用户想自己定义一个类java.lang.HackString，并且由自定义的类加载器进行加载，尝试访问getChars方法，
 * 由于java.lang.HackString和java.lang.String是由不同的类加载器进行加载的，它们拥有各自不同的运行时包，
 * 因此HackString是无法访问java.lang.String的包可见方法以及成员变量的。
 * 
 * 2.初始类加载器
 * 由于运行时包的存在，JVM规定了不同的运行时包下的类彼此之间是不可以进行访问的，那么问题来了，为什么我们在开发的程序中可以访问java.lang包下的类呢?
 * 在下面的程序中，SimpleClass是有我们自定义的ClassLoader加载的，但是其能够访问不同的运行时包下的类，比如String，下面我们来分析一下原因。
 * 每个类在经过ClassLoader的加载之后，在虚拟机中都会有对应的Class实例，如果某个类C被类加载器CL加载，那么CL就被称为C的初始化加载器。
 * JVM为每一个类加载器维护了一个列表，该列表中记录了将该类加载器作为初始类加载器的所有class，
 * 在加载一个类时，JVM使用这些列表来判断该类是否已经被加载过了，是否需要首次加载。
 * 根据JVM规范的规定，在类的加载过程中，所有参与的类加载器，即使没有亲自加载过该类，也都会被标识为该类的初始类加载器，
 * 比如java.lang.String首先经过了BrokerDelegateClassLoader类加载器，
 * 依次又经过了系统类加载器，扩展类加载器、根类加载器，这些类加载器都是java.lang.String的初始类加载器，
 * JVM会在每个类加载器维护的列表中添加该class类型。
 * 虽然SimpleClass和java.lang.String由于不同的类加载器加载，但是在BrokerDelegateClassLoader的class列表中维护了SimpleClass.class和String.class，
 * 因此在SimpleClass中时可以正常访问rt.jar中的class的。
 * 4.类的卸载
 * 在JVM的启动过程中，JVM会加载很多的类，在运行期间同样也会加载很多的类，比如用自定义的类加载器进行类的加载，
 * 或者像Apache Drools框架一样会在每个DSL文件解析成功之后生成相应的类文件。关于JVM在运行期间到底加载了多少class，
 * 可以在启动JVM时指定-verbose:class参数观察得到，我们知道某个对象在堆内存中如果没有其他对象引用则会在垃圾回收器线程进行GC的时候被回收掉，
 * 那么该对象在堆内存中的Class对象以及Class在方法区中的数据结构何时被回收呢?
 * 
 * JVM规定了一个Class只有在满足下面三个条件的时候才会被GC回收，也就是类被卸载。
 * 1.该类所有的实例都已经被GC，比如Simple.class的所有Simple实例都被回收掉。
 * 2.加载该类的ClassLoader实例被回收。
 * 3.该类的class实例没有在其他地方被引用。
 * @author 14378
 *
 */
public class LoadSimpleClass {

	public static void main(String[] args) throws ClassNotFoundException, InstantiationException, IllegalAccessException {
		BrokerDelegateClassLoader classLoader = new BrokerDelegateClassLoader("D:\\DevInstall\\eclipse-jee-2019-12-R-win32-x86_64\\workspace\\MultithreadingAndArchitecture\\src\\main\\java");
		Class<?> aClass = classLoader.loadClass("com.lbq.concurrent.chapter10.SimpleClass");
		System.out.println(classLoader.getParent());
		aClass.newInstance();
	}

}
