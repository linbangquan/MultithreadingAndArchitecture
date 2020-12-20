package com.lbq.concurrent.chapter10;
/**
 * 将编译好的java.lang.String.class放置到G:\\classloader3中，然后使用自定义类加载器BrokerDelegateClassLoader对其进行加载，代码如下：
 * 想要BrokerDetegateClassLoader加载G:\\classloader3下的String.class，需要对BrokerDelegateClassLoader源码进行修改，去掉对java和javax前缀的判断，
 * 虽然能准确找到G:\\classloader3目录下的java.lang.String.class文件，但JVM是不允许你这样做的，
 * 运行上下面的程序会出现java.lang.SecurityException:Prohibited package name:java.lang错误，
 * 打开ClassLoader源码会发现JVM在defineClass的时候做了安全性检查，代码如下：
 * private ProtectionDomain preDefineClass(String name, ProtectionDomain pd) {
 * 		if(!checkName(name)){
 * 			throw new NoClassDefFoundError("IllegalName: " + name);
 * 		}
 * 		if((name != null) && name.startsWith("java.")){
 * 			throw new SecurityException("Prohibited package name: " + name.substring(0, name.lastIndexOf('.')));
 * 		}
 * 		if(pd == null) {
 * 			pd = defaultDomain;
 * 		}
 * 		if(name != null) {
 * 			checkCerts(name, pd.getCodeSource());
 * 		}
 * 		return pd;
 * }
 * 几乎没有人会定义于JDK核心类库完成相同限定名称的类，我们这么做的主要目的是为了让读者能够更进一步地了解JDK类加载地内部细节而已。
 * @author 14378
 *
 */
public class LoadString {

	public static void main(String[] args) throws ClassNotFoundException {
		BrokerDelegateClassLoader classLoader = new BrokerDelegateClassLoader("D:\\DevInstall\\eclipse-jee-2019-12-R-win32-x86_64\\workspace\\MultithreadingAndArchitecture\\src\\main\\java");
		Class<?> aClass = classLoader.loadClass("java.lang.String");
	}

}
