package com.lbq.concurrent.chapter27.activeobject;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import com.lbq.concurrent.chapter19.Future;
import com.lbq.concurrent.chapter27.ActiveFuture;

/**
 * ActiveServiceFactory是通用Active Objects的核心类，其负责生产Service的代理以及构建ActiveMessage。
 * 
 * 在下述代码中：
 * 1.静态方法active()会根据ActiveService实例生成一个动态代理实例，其中会用到ActiveInvocationHandler作为newProxyInstance的InvocationHandler。
 * 2.在ActiveInvocationHandler的invoke方法中，首先会判断该方法是否被@ActiveMethod标记，如果没有则被当作正常方法来使用。
 * 3.如果接口方法被@ActiveMethod标记，则需要判断方法是否符合规范：有返回类型，必须是Future类型。
 * 4.定义ActiveMessage.Builder分别使用method、方法参数数组以及Active Service实例，如果该方法是Future的返回类型，则还需要定义ActiveFuture。
 * 5.最后将ActiveMessage插入ActiveMessageQueue中，并且返回method方法invoke结果。
 * @author 14378
 *
 */
public class ActiveServiceFactory {
	//定义ActiveMessageQueue，用于存放ActiveMessage
	private final static ActiveMessageQueue queue = new ActiveMessageQueue();
	
	public static <T> T active(T instance){
		//生产Service的代理类
		Object proxy = Proxy.newProxyInstance(instance.getClass().getClassLoader(), instance.getClass().getInterfaces(), new ActiveInvocationHandler<>(instance));
		return (T) proxy;
	}
	/**
	 * ActiveInvocationHandler是InvocationHandler的子类，生成Proxy时需要使用到
	 * @author 14378
	 *
	 * @param <T>
	 */
	private static class ActiveInvocationHandler<T> implements InvocationHandler {

		private final T instance;
		
		ActiveInvocationHandler(T instance){
			this.instance = instance;
		}
		
		@Override
		public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
			//如果接口方法被@ActiveMethod标记，则会转换为ActiveMessage
			if(method.isAnnotationPresent(ActiveMethod.class)) {
				this.checkMethod(method);
				ActiveMessage.Builder builder = new ActiveMessage.Builder();
				builder.useMethod(method).withObjects(args).forService(instance);
				Object result = null;
				if(this.isReturnFutureType(method)) {
					result = new ActiveFuture<>();
					builder.returnFuture((ActiveFuture) result);
				}
				//将ActiveMessage加入至队列中
				queue.offer(builder.build());
				return result;
			}else {
				//如果是普通方法（没有使用@ActiveMethod标记），则会正常执行
				return method.invoke(instance, args);
			}
		}
		/**
		 * 检查有返回值的方法是否为Future，否则将会抛出IllegalActiveMethod异常
		 * @param method
		 * @throws IllegalActiveMethod
		 */
		private void checkMethod(Method method) throws IllegalActiveMethod {
			if(!isReturnVoidType(method) && !isReturnFutureType(method)) {
				throw new IllegalActiveMethod("the method [" + method.getName() + " return type must be void/Future");
			}
		}
		/**
		 * 判断方法是否为Future返回类型
		 * @param method
		 * @return
		 */
		private boolean isReturnFutureType(Method method) {
			return method.getReturnType().isAssignableFrom(Future.class);
		}
		/**
		 * 判断方法是否无返回类型
		 * @param method
		 * @return
		 */
		private boolean isReturnVoidType(Method method) {
			return method.getReturnType().equals(Void.TYPE);
		}
	}
}
