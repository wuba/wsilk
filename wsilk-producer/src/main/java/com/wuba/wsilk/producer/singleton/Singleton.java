package com.wuba.wsilk.producer.singleton;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 单列模式
 * 
 * @author mindashuang
 */
@Documented
@Retention(RetentionPolicy.SOURCE)
@Repeatable(com.wuba.wsilk.producer.singleton.Singleton.List.class)
@Target(ElementType.TYPE)
public @interface Singleton {

	Type value() default Type.LAZY;

	// 名字区分
	String name() default "";

	@Target(ElementType.TYPE)
	@Retention(RetentionPolicy.SOURCE)
	@Documented
	public @interface List {
		Singleton[] value();
	}

	/**
	 * 
	 * 单例模式的类型
	 * 
	 * 
	 */
	static enum Type {
		/** 懒汉模式 */
		LAZY,
		/** 饿汉模式 */
		EAGER

	}

}
