package com.wuba.wsilk.producer.adapter;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 适配器模式
 * 
 * @author mindashuang
 */

@Documented
@Retention(RetentionPolicy.SOURCE)
@Target({ ElementType.TYPE })
public @interface Adapter {

	/**
	 * 适配的目标接口
	 */
	Class<?> target();

	/**
	 * 在适配的方法上的注解
	 */
	@Documented
	@Retention(RetentionPolicy.SOURCE)
	@Target({ ElementType.METHOD })
	@interface Method {

		// 适配的方法
		String value();

		// 扩展标识
		String extend() default "";

	}

}
