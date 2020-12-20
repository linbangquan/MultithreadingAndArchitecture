package com.lbq.concurrent.chapter09;
/**
 * 类的加载过程
 * ClassLoader的主要职责就是负责加载各种class文件到JVM中，ClassLoader是一个抽象的class，
 * 给定一个class的二进制文件名，ClassLoader会尝试加载并且在JVM中生成构成这个类的各种数据结构，然后使其分布在JVM对应的内存区域中。
 * 
 * 9.1 类的加载过程简介
 * 	类的加载过程一般分为三个比较大的阶段，分别是加载阶段、连接阶段和初始化阶段，如图所示：
 * 
 * 加载阶段 -->连接阶段(验证、准备、解析) --> 初始化阶段
 * 
 * 1.加载阶段：主要负责查找并且加载类的二进制数据文件，其实就是class文件。
 * 2.连接阶段：连接阶段所做的工作比较多，细分的话还可以分为如下三个阶段。
 * 		验证：主要是确保类文件的正确性，比如class的版本，class文件的魔术因子是否正确。
 * 		准备：为类的静态变量分配内存，并且为其初始化默认值。
 * 		解析：把类中的符号引用转换为直接引用。
 * 3.初始化阶段：为类的静态变量赋予正确的初始化值（代码编写阶段给定的值）。
 * 
 * 当一个JVM在我们通过执行Java命令启动之后，其中可能包含的类非常多，是不是每个类都会被初始化呢?答案是否定的，
 * JVM对类的初始化是一个延迟的机制，即使用的是lazy的方式，当一个类在首次使用的时候才会被初始化，在同一个运行时包下，
 * 一个Class只会被初始化一次(运行时包和类的包是有区别的)，那么什么是类的主动使用和被动使用呢？接下来我们通过一些实例来进行相应的总结。
 * 
 * 9.2 类的主动使用和被动使用
 * JVM虚拟机规范规定了，每个类或者接口被Java程序首次主动使用时才会对其进行初始化，当然随着JIT技术越来越成熟，JVM运行期间的编译也越来越智能，不排除JVM在运行期间提前预判并且初始化某个类。
 * JVM同时规范了以下6种主动使用类的场景，具体如下：
 * 1.通过new关键字会导致类的初始化：这种是大家经常采用的初始化一个类的方式，它肯定会导致类的加载并且最终初始化。
 * 2.访问类的静态变量，包括读取和更新会导致类的初始化。
 * 3.访问类的静态方法，会导致类的初始化。
 * 4.对某个类的进行反射操作，会导致类的初始化。
 * 5.初始化子类会导致父类的初始化。
 * 6.启动类：也就是执行main函数所在的类会导致该类的初始化。
 * 除了上述6种情况，其余的都称为被动使用，不会导致类的加载和初始化。
 * 关于类的主动引用和被动引用，下面有几个容易引起大家混淆的例子，我们看一看。
 * 1.构造某个类的数组时并不会导致该类的初始化。
 * 2.引用类的静态常量不会导致类的初始化。
 * 
 * 9.3 类的加载过程详解
 * 9.3.1 类的加载阶段
 * 简单来说，类的加载就是将class文件中的二进制数据读取到内存之中，然后将该字节流所代表的静态存储结构转换为方法区中运行时的数据结构，
 * 并且在堆内存中生成一个该类的java.lang.Class对象，作为访问方法区的数据结构的入口。
 * 
 * 类加载的最终产物就是堆内存中class对象，对同一个ClassLoader来讲，不管某个类被加载了多少次，对应到堆内存中的class对象始终是同一个。
 * 虚拟机规范中指出了类的加载是通过一个全限定名(包名+类名)来获取二进制数据流，但是并没有限定必须通过某种方式去获得，比如我们常见得是class二进制文件的形式，
 * 但是除此之外还会有如下的几种形式。
 * 1.运行时动态生成，比如通过开源的ASM包可以生成一些class，或者通过动态代理java.lang.Proxy也可以生成代理类的二进制字节流。
 * 2.通过网络获取，比如很早之前的Applet小程序，以及RMI动态发布等。
 * 3.通过读取zip文件获得类的二进制字节流，比如jar、war(其实，jar和war使用的是和zip同样的压缩算法)。
 * 4.将类的二进制数据存储在数据库的BLOB字段类型中。
 * 5.运行时生成class文件，并且动态加载，比如使用Thrift、AVRO等都是可以在运行时将某个Schema文件生成对应的若干个class文件，然后再进行加载。
 * 
 * 注意我们在这里所说的加载是类加载过程中的第一阶段，并不代表整个类已经加载完成了，在某个类完成加载阶段之后，
 * 虚拟机会将这些二进制字节流按照虚拟机所需要的格式存储在方法区中，然后形成特定的数据结构，随之又在堆内存中实例化一个java.lang.Class类对象，
 * 在类加载的整个生命周期中，加载过程还没有结束，连接阶段是可以交叉工作的，比如连接阶段验证字节流信息的合法性，但是总体来讲加载阶段肯定是出现在连接阶段之前的。
 * 
 * 9.3.2 类的连接阶段
 * 类的连接阶段可以细分为三个小的过程，分别是验证、准备和解析，在本节中，我们将详细介绍每个过程的具体细节。
 * 1.验证
 * 验证在连接阶段中的主要目的是确保class文件的字节流所包含的内容符合当前JVM的规范要求，并且不会出现危害JVM自身安全的代码，
 * 当字节流信息不符合要求时，则会抛出VerifyError这样的异常或者是其子异常。既然是验证，那么它到底验证了那些信息呢？
 * (1)验证文件格式
 * 	①在很多二进制文件中，文件头部都存在着魔术因子，该因子决定了这个文件到底是什么类型，class文件的魔术因子是0xCAFEBABE。
 *  ②主次版本号，Java的版本是在不断升级的，JVM规范同样也在不断升级，比如你用高版本的JDK编译的class就不能够被低版本的JVM所兼容，
 * 	 在验证的过程中，还需要查看当前的class文件版本是否符合当前JDK所处理的范围。
 * 	③构成class文件的字节流是否存在残缺或者其他附加信息，主要是看class的MD5指纹(每一个类在编译阶段经过MD5摘要算法计算之后，都会将结果一并附加给class字节流作为字节流的一部分)。
 * 	④常量池中的常量是否存在不被支持的变量类型，比如，int64。
 * 	⑤其他信息。
 * 	当然了，JVM对class字节流的验证远不止于此，由于获得class字节流的来源各种各样，甚至可以直接根据JVM规范编写出一个二进制字节流，
 *  对文件格式的验证可以在class被初始化之前将一些不符合规范的、恶意的字节流拒之门外，文件格式的验证充当了先锋关卡。
 * (2)元数据的验证
 * 	元数据的验证其实是对class的字节流进行语义分析的过程，整个语义分析就是为了确保class字节流符合JVM规范的要求。
 * 	①检查这个类是否存在父类，是否继承了某个接口，这些父类和接口是否合法，或者是否真实存在。
 *  ②检查该类是否继承了被final修饰的类，被final修饰的类是不允许被继承并且其中的方法是不允许被override的。
 *  ③检查该类是否为抽象类，如果不是抽象类，那么它是否实现了父类的抽象方法或者接口中的所有方法。
 *  ④检查方法重载的合法性，比如相同的方法名称、相同的参数但是返回类型不相同，这都是不被允许的。
 *  ⑤其他语义验证。
 * (3)字节码验证
 *  当经过了文件格式和元数据的语义分析过程之后，还要对字节码进行进一步的验证，该部分的验证是比较复杂的，主要是验证程序的控制流程，比如循环、分支等。
 *  ①保证当前线程在程序计数器中的指令不会跳转的不合法的字节码指令中去。
 *  ②保证类型的转换是合法的，比如用A声明的引用，不能用B进行强制类型转换。
 *  ③保证任意时刻，虚拟机栈中的操作栈类型与指令代码都能正确地被执行，比如在压栈的时候，传入的是一个A类型的引用，在使用的时候却将B类型载入了本地变量表。
 *  ④其他验证。
 * (4)符号引用验证
 * 	我们说过，在类的加载过程中，有些阶段是交叉进行的，比如在加载阶段尚未结束之前，连接阶段可能已经开始工作了，这样做的好处是能够提高类加载的整体效率，
 * 	同样符号引用的验证，其主要作用就是验证符号引用转换为直接引用时的合法性。
 * 	①通过符号引用描述的字符串全限定名称是否能够顺利地找到相关的类。
 * 	②符号引用中的类、字段、方法，是否对当前类可见，比如不能访问引用类的私有方法。
 * 	③其他
 *  符号引用的验证目的是为了保证解析动作的顺利执行，比如，如果某个类的字段不存在，则会抛出NoSuchFieldError，若方法不存在时则抛出NoSuchMethodError等，
 *  我们在使用反射的时候会遇到这样的异常信息。
 *  
 * 2.准备
 *  当一个class的字节流通过了所有的验证过程之后，就开始为该对象的类变量，也就是静态变量，分配内存并且设置初始值了，
 *  类变量的内存会被分配到方法区中，不同于实例变量会被分配到堆内存之中。
 *  所谓设置初始值，其实就是为相应的类变量给定一个相关类型在没有被设置值时的默认值，不同的数据类型及其初始值见表9-1。
 *  各种类型的默认初始值
 *  数据类型	初始值
 *  byte	(byte)0
 *  char	'\u0000'
 *  short	(short)0
 *  int 	0
 *  float	0.0F
 *  double 	0.0D
 *  long	0L
 *  boolean	false
 *  引用类型	null
 * 
 * 为类变量设置初始值的代码如下：
 * public class LinkedPrepare {
 * 		private static int a = 10;		//①
 * 		private final static int b = 10;//②
 * }
 * 其中static int a = 10 在准备阶段不是10，而是初始化值0，当然final static int b则还会是10，为什么呢？
 * 因为final修饰的静态变量(可直接计算得出结果)不会导致类的初始化，一种被动引用，因此就不存在连接阶段了。
 * 当然了更加严谨的解释是final static int b = 10在类的编译阶段javac 会将其value生成一个ConstantValue属性，直接赋予10。
 * 
 * 3.解析
 * 在连接阶段中经历了验证、准备之后，就可以顺利进入到解析过程了，当然在解析的过程中照样会交叉一些验证的过程，比如符号引用的验证，
 * 所谓解析就是在常量池中寻找类、接口、字段和方法的符号引用，并且将这些符号引用替换成直接引用的过程：
 * public class ClassResolve {
 * 		static Simple simple = new Simple();
 * 		public static void main(String[] args){
 * 			System.out.println(simple);
 * 		}
 * }
 * 在上面的代码中ClassResolve用到了Simple类，我们在编写程序的时候，可以直接使用simple这个引用去访问Simple类中可见的属性及方法，
 * 但是在class字节码中可不是这么简单，它会被编译成相应的助记符，这些助记符称为符号引用，在类的解析过程中，助记符还需要得到进一步的解析，
 * 才能正确地找到所对应的堆内存中的Simple数据结构，下面是一段ClassResolve字节码的信息片段：
 * public static void main(java.lang.String[]);
 * 		flags: ACC_PUBLIC, ACC_STATIC
 * 		Code:
 * 			stack=2, locals=1, args_size=1
 * 				0: getstatic #2 // Field java/lang/System.out:Ljava/io/printStream;
 * 				3: getstatic #3 // Field simple:Lcom/wangwenjun/concurrent/chapter09/Simple;
 * 				6: invokevirtual #4 // Method java/io/PrintStream.println:(Ljava/lang/Object;)V
 * 				9: return
 * 			LineNumberTable:
 * 				line 15: 0
 * 				line 16: 9
 * 			LocalVariableTable:
 * 				Start	Length	Slot	Name	Signature
 * 				0		10		0		args	[Ljava/lang/String;
 * 		
 * 		static {};
 * 			flags: ACC_STATIC
 * 			Code:
 * 				stack=2, locals=0, args_size=0
 * 					0: new #5 // class com/wangwenjun/concurrent/chapter09/Simple
 * 					3: dup
 * 					4: invokespecial #6 // Method com/wangwanjun/concurrent/chapter09/Simple."<init>":()V
 * 					7: putstatic #3 // Field simple:Lcom/wangwenjun/concurrent/chapter09/Simple;
 * 					10: return
 * 			LineNumberTable:
 * 				line 11: 0
 * 在常量池中通过getstatic这个指令获取PrintStream，同样getstatic也适用于获取Simple，
 * 然后通过invokevirtual指令将simple传递给PrintStream的println方法，在字节码的执行过程中，
 * getstatic被执行之前，就需要进行解析。
 * 
 * 虚拟机规定了，在anewarray、checkcast、getfield、getstatic、instanceof、invokeinterface、
 * invokespecial、invokestatic、invokevirtual、multianewarray、new、putfield、putstatic
 * 这13个操作符号引用的字节码指令之前，必须对所有的符号提前进行解析。
 * 
 * 解析过程主要是针对类接口、字段、类方法和接口方法这四类进行的，分别对应到常量池中的CONSTANT_class_info、
 * CONSTANT_Fieldref_info、CONSTANT_Methodref_info和CONSTANT_InterfaceMethodred_info这四种类型常量。
 * 
 * (1)类接口解析
 *  ①假设前文代码中的Simple，不是一个数组类型，则在加载的过程中，需要先完成对Simple类的加载，同样需要经历所有的类加载阶段。
 *  ②如果Simple是一个数组类型，则虚拟机不需要完成对Simple的加载，只需要在虚拟机中生成一个能够代表该类型的数组对象，
 *  并且在堆内存中开辟一片连续的地址空间即可。
 *  ③在类接口的解析完成之后，还需要进行符号引用的验证。
 * (2)字段的解析
 * 所谓字段的解析，就是解析你所访问类或者接口中的字段，在解析类或者变量的时候，如果该字段不存在，或者出现错误，就会抛出异常，不再进行下面的解析。
 * 	①如果Simple类本身就包含某个字段，则直接返回这个字段的引用，当然也要对该字段所属的类提前进行类加载。
 * 	②如果Simple类中不存在该字段，则会根据继承关系自下而上，查找父类或者接口的字段，找到即可返回，
 * 	同样需要提前对找到的字段进行类的加载过程。
 * 	③如果Simple类中没有字段，一直找到了最上层的java.lang.Object还是没有，则表示查找失败，也就不再进行任何解析，直接抛出NoSuchFieldError异常。
 * 这样也就解释了子类为什么重写了父类的字段之后能够生效的原因，因为找到子类的字段就直接初始化并返回了。
 * (3)类方法的解析
 * 类方法和接口方法有所不同，类方法可以直接使用该类进行调用，而接口方法必须要有相应的实现类继承才能够进行调用。
 *  ①若在类方法表中发现class_index中索引的Simple是一个接口而不是一个类，则直接返回错误。
 *  ②在Simple类中查找是否有方法描述和目标方法完全一致的方法，如果有，则直接返回这个方法的引用，否则直接继续向上查找。
 *  ③如果父类中仍然没有找到，则意味着查找失败，程序会抛出NoSuchMethodError异常。
 *  ④如果在当前类或者父类中找到了和目标方法一致的方法，但是它是一个抽象类，则会抛出AbstractMethodError这个异常。
 * 在查找的过程中也出现了大量的检查和验证。
 * (4)接口方法的解析
 * 接口不仅可以定义方法，还可以继承其他接口。
 * 	①在接口方法表中发现class_index中索引的Simple是一个类而不是一个接口，则会直接返回错误，因为方法接口表和类接口表所容纳的类型应该是不一样的，
 * 	这也是为什么在常量池中必须要有Constant_Methodref_info和Constant_InterfaceMethodred_info两个不同的类型。
 * 	②接下来的查找就和类方法的解析比较类似了，自下而上的查找，直到找到为止，或者没找到抛出NoSuchMethodError异常。
 * 
 * 9.3.3
 * 类的初始化阶段
 * 经历了层层关卡，终于来到了类的初始化阶段，类的初始化阶段是整个类加载过程中的最后一个阶段，在初始化阶段做的最主要的一件事情就是执行<clinit>()方法的过程。
 * (clinit是class initialize前面几个字母的简写)在<clinit>()方法中所有的类变量都会被赋予正确的值，也就是在程序编写的时候指定的值。
 * <clinit>()方法是在编译阶段生成的，也就是说它已经包含在了class文件中了，<clinit>()包含了所有类变量的赋值动作和静态语句块的执行代码，
 * 编译器收集的顺序是由执行语句在源文件中的出现顺序所决定的(<clinit>()是能够保证顺序性的，关于顺序性的话题会在本书的第三部分进行详细的介绍)。
 * 另外需要注意的一点是，静态语句只能对后面的静态变量进行赋值，但是不能对其进行访问。
 * 另外<clinit>()方法与类的构造器函数有所不同，它不需要显示的调用父类的构造器，虚拟机会保证父类的<clinit>()方法最先执行，
 * 因此父类的静态变量总是能够得到优先赋值。
 * 虽然说Java编译器会帮助class生成<clinit>()方法，但是该方法并不意味着总是会生成，比如某个类中既没有静态代码块，也没有静态变量，那么它就没有生成<clinit>()方法的必要了，
 * 接口中同样也是如此，由于接口天生不能定义静态代码块，因此只有当接口中有变量的初始化操作时才会生成<clinit>()方法。
 * @author 14378
 *
 */
public class ActiveLoadTest {

	public static void main(String[] args) {
		/**
		 * 代码中new方法新建了一个Simple类型的数组，但是它并不能导致Simple类的初始化，因此它是被动使用，
		 * 不要被前面的new关键字所误导，事实上该操作只不过是在堆内存中开辟了一段连续的地址空间4byte x 10.
		 */
//		Simple[] simples = new Simple[10];
//		System.out.println(simples.length);
//		
//		System.out.println(Simple.x);

//		System.out.println("Simple.test()");
//		Simple.test();
		
//		try {
//			Class.forName("com.lbq.concurrent.chapter09.Simple");
//		} catch (ClassNotFoundException e) {
//			e.printStackTrace();
//		}
		
//		System.out.println("Child.y:" + Child.y);
//		System.out.println("Child.x:" + Child.x);

//		System.out.println("GlobalConstants.MAX:" + GlobalConstants.MAX);
//		System.out.println("GlobalConstants.RANDOM:" + GlobalConstants.RANDOM);
	}

}
