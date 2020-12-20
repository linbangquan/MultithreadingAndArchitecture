package com.lbq.concurrent.chapter28;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
/**
 * 注册对象给EventBus的时候需要指定接收消息时的回调方法，我们采用注解的方式进行Event回调。
 * -@Subscribe要求注解在类中的方法，注解时可指定topic，不指定的情况下为默认的topic(default-topic)。
 * @author 14378
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Subscribe {
	String topic() default "default-topic";
}
