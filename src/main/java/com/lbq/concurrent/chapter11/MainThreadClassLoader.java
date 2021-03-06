package com.lbq.concurrent.chapter11;
/**
 * 介绍Java类加载器的知识，其主要是为了解释线程的上下文类加载器原理和使用场景。
 * 
 * 11.1 为什么需要线程上下文类加载器
 * 	根据Thread类的文档你会发现线程上下文方法是从JDK1.2开始引入的，getContextClassLoader()和setContextClassLoader(ClassLoader cl)
 * 	分别用于获取和设置当前线程的上下文类加载器，如果当前线程没有设置上下文类加载器，那么它将和父线程保持同样的类加载器。
 * 	站在开发者的角度，其他线程都是由Main线程，也就是main函数所在的线程派生的，它是其他线程的父线程或者祖先线程。
 * 
 * 	为什么要有线程上下文类加载器呢？这就与JVM类加载器双亲委托机制自身的缺陷是分不开的，JDK的核心库中提供了很多SPI(Service Provider Interface),
 *  常见的SPI包括JDBC、JCE、JNDI、JAXP和JBI等，JDK只规定了这些接口之间的逻辑关系，但不提供具体的实现，具体的实现需要由第三方厂商来提供，
 *  作为Java程序员或多或少地都写过JDBC的程序，在编写JDBC程序时几乎百分百的都在与java.sql包下的类打交道。
 *  如图11-1所示，Java使用JDBC这个SPI完全透明了应用程序和第三方厂商数据库驱动的具体实现，不管数据库类型如何切换，
 *  应用程序只需要替换JDBC的驱动jar包以及数据库的驱动名称即可，而不用进行任何更新。
 *  这样做的好处是JDBC提供了高度抽象，应用程序则只需要面向接口编程即可，不用关心各大数据库厂商提供的具体实现，
 *  但问题在于java.lang.sql中的所有接口都由JDK提供，加载这些接口的类加载器是根加载器，
 *  第三方厂商提供的类库驱动则是由系统类加载器加载的，
 *  由于JVM类加载器的双亲委托机制，比如Connections、Statement、RowSet等皆由根加载器加载，第三方的JDBC驱动包中的实现不会被加载，
 *  那么又是如何解决这个问题的呢?
 * 11.2 数据库驱动的初始化源码分析
 * 	在编写所有的JDBC程序时，首先都需要调用Class.forName("xxx.xxx.xxx.Driver")对数据库驱动进行加载，打开MySql驱动Driver源码，代码如清单11-2所示。
 * 	public class Driver extends NonRegisteringDriver implements java.sql.Driver {
 * 		public Driver() throw SQLException {
 * 
 * 		}
 * 		static{
 * 			try{
 * 				//在Mysql的静态方法中将Driver实例注册到DriverManager中
 * 				DriverManager.registerDriver(new Driver());
 * 			}catch(SQLException var1){
 * 				throw new RuntimeException("Can't register driver!");
 * 			}
 * 		}
 * 	}
 * 	Driver类的静态代码主要是将Mysql的Driver实例注册给DriverManager，
 * 	因此直接使用DriverManager.registerDriver(new com.mysql.jdbc.Driver())其作用与Class.forName("xxx.xxx.xxx.Driver")是完全等价的。
 * 	下面我们继续看DriverManager的源码，毕竟数据库的连接就是从它而来的，代码如下：
 * 	public class DriverManager {
 * 		private static Connection getConnection(String url, java.util.Properties info, Class<?> caller) throws SQLException {
 * 			ClassLoader callerCL = caller != null ? caller.getClassLoader() : null;
 * 			synchronized(DriverManager.class) {
 * 				if(callerCL == null){
 * 					callerCL = Thread.currentThread().getContextClassLoader();
 * 				}
 * 			}
 * 
 * 			for(DriverInfo aDriver : registeredDrivers){
 * 				if(isDriverAllowed(aDriver.driver, callerCL)) {
 * 					try{
 * 						println("trying " + aDriver.driver.getClass().getName());
 * 						Connection con = aDriver.driver.connect(url, info);
 * 						if(con != null) {
 * 							println("getConnection returning " + aDriver.driver.getClass().getName());
 * 							return (con);
 * 						}
 * 					}
 * 				}
 * 			}
 * 		}
 * 
 * 		private static boolean isDriverAllowed(Driver driver, ClassLoader classLoader) {
 * 			boolean result = false;
 * 			if(driver != null){
 * 				Class<?> aClass = null;
 * 				try{
 * 					aClass = Class.forName(driver.getClass().getName(), true, classLoader);
 * 				}catch (Exception ex) {
 * 					result = false;
 * 				}
 * 
 * 				result = (aClass == driver.getClass()) ? true : false;
 * 			}
 * 			return result;
 * 		}
 * 	}
 * 	上面代码片段我只截取了部分，主要是用于说明与线程上下文类加载器有关的内容。在注释①处获取当前线程的上下文类加载器，
 * 	该类就是调用Class.forName("X")所在线程的线程上下文类加载器，通常是系统类加载器。
 * 	注释②中通过递归DriverManager中已经注册的驱动类，然后验证该数据库驱动是否可以被指定的类加载器加器（线程上下文类加载器），
 * 	如果验证通过则返回Connection，此刻返回的Connection则是数据库厂商提供的实例。
 * 	注释③中的关键地方就在于Class.forName(driver.getClass().getName(), true, classLoader);
 * 	其使用线程上下文类加载器进行数据库驱动的加载以及初始化。
 * 	下面就来回顾数据库驱动加载的整个过程，由于JDK定义了SPI的标准接口，加之这些接口被作为JDK核心标准类库的一部分，
 * 	既想完全透明标准接口的实现，又想与JDK核心库进行捆绑，由于JVM类加载器双亲委托机制的限制，启动类加载器不可能加载得到第三方厂商提供的具体实现。
 * 	为了解决这个困境，JDK只好提供一种不太优雅的设计————线程上下文类加载器，有了线程上下文类加载器，启动类加载器(根加载器)反倒需要委托子类加载器去加载厂商提供的SPI具体实现。
 * 	
 * 	父委托变成了子委托的方式，这也打破了双亲委托机制的模型，而且是由JDK官方亲自打破的，自此几乎所有涉及SPI加载的动作采用的都是这种方式，
 * 	比如JNDI、JDBC、JCE、JAXB、和JBI，等等，当然这可能是由于早期Java开发者没有考虑那么周全的原因所导致的，
 * 	在现在的开源社区中也经常会遇到标准的接口和第三方实现独立设计的情况，比如slf4j只是log的标准接口库，而slf4j-log4j则是其中的一个实现，
 * 	在真实项目中，两者皆有同一个类加载器进行加载，就不至于像数据库驱动的加载一样，饶了一大圈。
 * 	
 * 	本章通过分析MySql驱动加载过程的源码，帮助读者清晰地理解线程上下文类加载器所发挥地作用了。
 * 	在Thread类中增加getContextClassLoader()和setContextClassLoader(ClassLoader cl)方法实属无奈之举，
 * 	它不仅破坏了类加载器地父委托机制，而且反其道而行之，允许“子委托机制”，关于线程上下文类加载器方法地设计在各大论坛地争议还是比较大的，
 * 	有人甚至认为它是Java设计中存在的一个缺陷。
 * @author 14378
 *
 */
public class MainThreadClassLoader {

	public static void main(String[] args) {
		System.out.println(Thread.currentThread().getContextClassLoader());//sun.misc.Launcher$AppClassLoader@73d16e93

	}

}
