package com.lbq.concurrent.chapter10;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
/**
 * JDK除了提供上述三大类内置类加载器之外，还允许开发人员进行类加载器的扩展，也就是自定义类加载器。
 * 所有的自定义类加载器都是ClassLoader的直接子类或者间接子类，
 * java.lang.ClassLoader是一个抽象类，它里面并没有抽象方法，
 * 但是有findClass方法，务必实现该方法，否则将会抛出Class找不到的异常，
 * 示例代码如下：
 * protected Class<?> findClass(String name) throws ClassNotFoundException {
 * 		throw new ClassNotFoundException(name);
 * }
 * 
 * 在我们定义的类加载器中，通过将类的全名称转换成文件的全路径重写findClass方法，然后读取class文件的字节流数据，
 * 最后使用ClassLoader的defineClass方法对class完成了定义。
 * 在开始使用我们定义的ClassLoader之前，有几个地方需要特别强调一下。
 * 第一，关于类的全路径格式，一般情况下我们的类都是类似于java.lang.String这样的格式，
 * 但是有时候不排除内部类，匿名内部类等；全路径格式有如下几种情况。
 * 	1.java.lang.String：包名.类名
 * 	2.javax.swing.JSpingner$DefaultEditor：包名.类名$内部类
 * 	3.java.security.KeyStore$Builder$FileBuilder$1：包名.类名$内部类$内部类$匿名内部类
 * 	4.java.net.URLClassLoader$3$1：包名.类名$匿名内部类$匿名内部类
 * 第二个需要强调的是defineClass方法，该方法的完整方法描述是defineClass(String name，byte[] b，int off，int len)，
 * 其中，第一个是要定义类的名字，一般于findClass方法中的类名保持一致即可；
 * 第二个是class文件的二进制字节数组，这个也不难理解；
 * 第三个是字节数组的偏移量；
 * 第四个是从偏移量开始读取多长的byte数据。
 * 大家思考一下，在类的加载过程中，第一个阶段的加载器主要是获取class的字节流信息，那么我们将整个字节流信息交给defineClass方法不就行了吗，
 * 为什么还要画蛇添足地指定偏移量和读取长度呢？
 * 原因是因为class字节数组不一定是从一个class文件中获得的，有可能是来自网络的，也有可能是用编程的方式写入的，
 * 由此可见，一个字节数组中很有可能存储多个class的字节信息。
 * @author 14378
 *
 */
//自定义类加载器必须是ClassLoader的直接或者间接子类
public class MyClassLoader extends ClassLoader {
	//定义默认的class存放路径
	private final static Path DEFAULT_CLASS_DIR = Paths.get("G:", "classloader1");
	
	private final Path classDir;
	//使用默认的class路径
	public MyClassLoader() {
		super();
		this.classDir = DEFAULT_CLASS_DIR;
	}
	//允许传入指定的路径的class路径
	public MyClassLoader(String classDir) {
		super();
		this.classDir = Paths.get(classDir);
	}
	//除了可以指定磁盘目录以外还可以指定该类的加载器的父加载器
	public MyClassLoader(String classDir, ClassLoader parent) {
		super(parent);
		this.classDir = Paths.get(classDir);
	}
	//重写父类的findClass方法，这是至关重要的步骤
	@Override
	protected Class<?> findClass(String name) throws ClassNotFoundException {
		//读取class的二进制数据
		byte[] classBytes = this.readClassBytes(name);
		//如果数据为null，或者没有读到任何信息，则抛出ClassNotFoundException异常
		if(null == classBytes || classBytes.length == 0) {
			throw new ClassNotFoundException("Can not load the class " + name);
		}
		//调用defineClass方法定义class
		return this.defineClass(name, classBytes, 0, classBytes.length);
	}
	//将class文件读入内存
	private byte[] readClassBytes(String name) throws ClassNotFoundException {
		//将包名分隔符转换为文件路径分隔符
		String classPath = name.replace(".", "/");
		Path classFullPath = classDir.resolve(Paths.get(classPath + ".class"));
		if(!classFullPath.toFile().exists()) {
			throw new ClassNotFoundException("The class " + name + " not found.");
		}
		try(ByteArrayOutputStream baos = new ByteArrayOutputStream()){
			Files.copy(classFullPath, baos);
			return baos.toByteArray();
		}catch(IOException e) {
			throw new ClassNotFoundException("load the class " + name + " occurerror.", e);
		}
	}
	
	@Override
	public String toString() {
		return "My ClassLoader";
	}
}
