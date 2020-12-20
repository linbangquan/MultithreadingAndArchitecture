package com.lbq.concurrent.chapter10;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
/**
 * 10.2.2 双亲委托机制详细介绍
 * 了解了如何自定义一个类加载器之后，我们来研究一下类加载器最重要的机制————双亲委托机制，有时候也称为父委托机制。
 * 当一个类加载器被调用了loadClass之后，它并不会直接将其加载，而是先交给当前类加载器的父加载器尝试加载直到最顶层的符加载器，
 * 然后再依次向下进行加载，这也是为什么在10.2.1节中要求将HelloWorld.java和HelloWorld.class删除的原因。
 * 在开始分析loadClass源码之前，请大家思考一个问题，由于我们担心HelloWorld.class被系统类加载器加载，所以删除了HelloWorld的相关文件，
 * 那么有什么办法可以不用删除又可以使用MyClassLoader对HelloWorld进行加载的吗？来看个代码片段：
 * public Class<?> loadClass(String name) throws ClassNotFoundException {
 * 		return loadClass(name, false);
 * }
 * protected Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
 * 		synchronized (getClassLoadingLock(name)) {
 * 			//First, check if the class has already been loaded
 * 			Class<?> c = findLoadedClass(name);
 * 			if(c == null) {
 * 				long t0 = System.nanoTime();
 * 				try{
 * 					if(parent != null) {
 * 						c = parent.loadClass(name, false);
 * 					} else {
 * 						c = findBootstrapClassOrNull(name);
 * 					}
 * 				} catch (ClassNotFoundException e){
 * 					//ClassNotFoundException thrown if class not found
 * 					//from the non-null parent class loader
 * 				}
 * 				if (c == null) {
 * 					//if still not found, then invoke findClass in order to find the class.
 * 					long t1 = System.nanoTime();
 * 					c = findClass(name);
 * 					//this is the defining class loader; record the stats
 * 					sun.misc.PerfCounter.getParentDelegationTime().addTime(t1 - t0);
 * 					sun.misc.PerfCounter.getFindClassTime().addElapsedTimeFrom(t1);
 * 					sun.misc.PerfCounter.getFindClasses().increment();
 * 				}
 * 			}
 * 			if (resolve) {
 * 				resolveClass(c);
 * 			}
 * 			return c;
 * 		}
 * }
 * 上面的代码片段是java.lang.ClassLoader的loadClass(name)和loadClass(name, resolve)方法，
 * 由于loadClass(name)调用的是loadClass(name, false)，因此我们重点解释后者即可。
 * 	1.从当前类加载器的已加载类缓存中根据类的全路径名查询是否存在该类，如果存在则直接返回。
 * 	2.如果当前类存在父类加载器，则调用父类加载器的loadClass(name, false) 方法对其进行加载。
 * 	3.如果当前类加载器不存在父类加载器，则直接调用根类加载器对该类进行加载。
 * 	4.如果当前类的所有父类加载器都没有成功加载器class，则尝试调用当前加载器的findClass方法对其进行加载，该方法就是我们自定义加载器需要重写的方法。
 * 	5.最后如果类被成功加载，则做一些性能数据的统计。
 * 	6.由于loadClass指定了resolve为false，所以不会进行连接阶段的继续执行，这也就解释了为什么通过类加载器加载类并不会导致类的初始化。
 * 分析完了loadClass方法的执行过程，再结合父委托机制的类加载流程图，相信读者应该对父委托机制有一个比较清晰的认识，
 * 回到本节开始时的问题，如何在不删除HelloWorld.class文件的情况下使用MyClassLoader而不是系统类加载器进行HelloWorld的加载，
 * 有如下两种方法可以做到：
 * 	1.第一种方式是绕过系统类加载器，直接将扩展类加载器作为MyClassLoader的父加载器，示例代码如下：
 * 	首先我们通过MyClassLoaderTest.class获取系统类加载器，然后在获取系统类加载器的父类加载器中扩展类加载器，使其成为MyClassLoader的父类加载器，
 * 	这样一来，根加载器和扩展类加载器都无法对G:\\classloader1中的类文件进行加载，自然而然就交给了MyClassLoader对HelloWorld进行加载了，
 * 	这种方式也是充分利用了类加载器父委托机制的特性。
 * 	2.第二种方式是在构造MyClassLoader的时候指定其父类加载器为null,示例代码如下：
 * 	根据对loadClass方法的源码分析，当前类在没有父类加载器的情况下，会直接使用根加载器对该类进行加载，
 * 	很显然，HelloWorld在根加载器的加载路径下是无法找到的，那么它自然而然地就交给当前类加载器进行加载了。
 * 
 * 通过对loadClass源码的分析，我们发现类加载器的父委托机制的逻辑主要是由loadClass来控制的，有些时候我们需要打破这种双亲委托的机制，
 * 比如HelloWorld这个类就是不希望通过系统类加载器对其进行加载。虽然上面给出了两种解决方案，但是采取的都是绕过ApplicationClassLoader的方式去实现的，
 * 并没有避免一层一层的委托，那么有没有办法可以绕过这种双亲委托的模型呢？
 * 值得庆幸的一点是，JDK提供的双亲委托机制并非一个强制性的模型，程序开发人员是可以对其进行灵活发挥破坏这种委托机制的，
 * 比如我们想要在程序运行时进行某个模块功能的升级，甚至是在不停止服务的前提下增加新的功能，这就是我们常说的热部署。
 * 热部署首先要卸载掉加载该模块的所有Class的类加载器，卸载类加载器会导致所有类的卸载，很显然我们无法对JVM三大内置加载器进行卸载，
 * 我们只有通过控制自定义类加载器才能做到这一点。
 * 
 * 我们可以通过破坏父委托机制的方式来实现对HelloWorld类的加载，而不需要在工程中删除该文件，只不过增加了一个loadClass方法的重写过程，
 * 读者可以直接继承MyClassLoader然后重写loadClass方法。
 * @author 14378
 *
 */
public class BrokerDelegateClassLoader extends ClassLoader {
	//定义默认的class存放路径
	private final static Path DEFAULT_CLASS_DIR = Paths.get("G:", "classloader1");
	
	private final Path classDir;
	//使用默认的class路径
	public BrokerDelegateClassLoader() {
		super();
		this.classDir = DEFAULT_CLASS_DIR;
	}
	//允许传入指定的路径的class路径
	public BrokerDelegateClassLoader(String classDir) {
		super();
		this.classDir = Paths.get(classDir);
	}
	
	public BrokerDelegateClassLoader(String classDir, ClassLoader parent) {
		super();
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
		return "BrokerDelegateClassLoader";
	}
	/**
	 * 1.根据类的全路径名称进行加锁，确保每一个类在多线程的情况下只被加载一次。
	 * 2.到已加载类的缓存中查看该类是否已经被加载，如果已加载则直接返回。
	 * 3、4.若缓存中没有被加载的类，则需要对其进行首次加载，如果类的全路径以java和javax开头，则直接委托给系统类加载器对其进行加载。
	 * 5.如果类不是以Java和javax开头，则尝试用我们自定义的类加载器进行加载。
	 * 6.若自定义类加载仍旧没有完成对类的加载，则委托给父类加载器进行加载或者系统类加载器进行加载。
	 * 7.经过若干次的尝试之后，如果还是无法对类进行加载，则抛出无法找到类的异常。
	 */
	@Override
	protected Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException{
		synchronized(getClassLoadingLock(name)) {
			Class<?> klass = findLoadedClass(name);
			if(klass == null) {
				if(name.startsWith("java.") || name.startsWith("javax")) {
					try {
						klass = getSystemClassLoader().loadClass(name);
					}catch(Exception e) {
						//ignore
					}
				}else {
					try {
						klass = this.findClass(name);
					} catch (ClassNotFoundException e) {
						e.printStackTrace();
					}
					if(klass == null) {
						if(getParent() != null) {
							klass = getParent().loadClass(name);
						}else {
							klass = getSystemClassLoader().loadClass(name);
						}
					}
				}
			}
			if(null == klass) {
				throw  new ClassNotFoundException("The class " + name + " not found.");
			}
			if(resolve) {
				resolveClass(klass);
			}
			return klass;
		}
	}
}
