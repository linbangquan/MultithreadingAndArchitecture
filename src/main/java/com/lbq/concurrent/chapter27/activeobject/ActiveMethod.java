package com.lbq.concurrent.chapter27.activeobject;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
/**
 * 通用的Active Objects更加灵活，它允许你将某个接口的任意方法转换为ActiveMethod，
 * 如果不需要转换，则需要按照普通方法来执行，而不会被单独的线程执行，要做到这一点，就需要使用
 * ActiveMethod注解来进行标记
 * @author 14378
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface ActiveMethod {

}
