package com.lbq.concurrent.chapter28;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
/**
 * 28.1.4 Subscriber注册表Registry详解
 * 注册表维护了topic和subscriber之间的关系，当有Event被post之后，Dispatcher需要知道该消息应该发送给哪个Subscriber的实例和对应的方法，
 * Subscriber对象没有任何特殊要求，就是普通的类不需要继承任何父类或者实现任何接口，注册表Registry的代码如下。
 * 
 * 由于Registry是在Bus中使用的，不能暴露给外部，因此Registry被设计成了包可见的类，我们所设计的EventBus对Subscriber没有做任何限制，
 * 但是接受event的回调则需要将方法使用注解@Subscribe进行标记(可指定topic)，同一个Subscriber的不同方法通过@Subscribe注解之后
 * 可接受来自两个不同的消息。
 * @author 14378
 *
 */
class Registry {
	//存储Subscriber集合和topic之间关系的map
	private final ConcurrentHashMap<String, ConcurrentLinkedQueue<Subscriber>> subscriberContainer = new ConcurrentHashMap<>();
	
	public void bind(Object subscriber) {
		//获取Subscriber Object的方法集合然后进行绑定
		List<Method> subscribeMethods = getSubscribeMethods(subscriber);
		subscribeMethods.forEach(m -> tierSubscriber(subscriber, m));
	}
	
	public void unbind(Object subscriber) {
		//unbind为了提高速度，只对Subscriber进行失效操作
		subscriberContainer.forEach((key, queue) -> {
			queue.forEach(s -> {
				if(s.getSubscribeObject() == subscriber) {
					s.setDisable(true);
				}
			});
		});
	}
	
	public ConcurrentLinkedQueue<Subscriber> scanSubscriber(final String topic){
		return subscriberContainer.get(topic);
	}
	
	private void tierSubscriber(Object subscriber, Method method) {
		final Subscribe subscribe = method.getDeclaredAnnotation(Subscribe.class);
		String topic = subscribe.topic();
		//当某topic没有Subscriber Queue的时候创建一个
		subscriberContainer.computeIfAbsent(topic, key -> new ConcurrentLinkedQueue<>());
		//创建一个Subscriber并且加入Subscriber列表中
		subscriberContainer.get(topic).add(new Subscriber(subscriber, method));
	}
	
	private List<Method> getSubscribeMethods(Object subscriber){
		final List<Method> methods = new ArrayList<>();
		Class<?> temp = subscriber.getClass();
		//不断获取当前类和父类的所有@Subscribe方法标记的方法才符合回调方法
		while(temp != null) {
			//获取所有的方法
			Method[] declaredMethods = temp.getDeclaredMethods();
			//只有public方法&&有一个入参&&最重要的是被@Subscribe
			Arrays.stream(declaredMethods)
				.filter(m -> m.isAnnotationPresent(Subscribe.class) && m.getParameterCount() == 1 && m.getModifiers() == Modifier.PUBLIC)
				.forEach(methods::add);
			temp = temp.getSuperclass();
		}
		return methods;
	}
	
	public static void main(String[] args) {
		Map<String, List<Integer>> map = new HashMap<>();
		for(int i = 0; i < 10; i++) {
			List<Integer> v = map.computeIfAbsent("A", key -> new ArrayList<Integer>());
			System.out.println(v);
			v.add(i);
			//map.get("A").add(i);
		}
		for(int i = 0; i < 10; i++) {
			List<Integer> v = map.putIfAbsent("A", new ArrayList<Integer>());
			System.out.println(v);
			v.add(i);
			//map.get("A").add(i);
		}
		System.out.println(map);
	}
}
