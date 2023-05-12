package com.wuba.wsilk.producer.prototype;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 原型模式
 * 
 * @author mindashuang
 */

@Documented
@Retention(RetentionPolicy.SOURCE)
@Target({ ElementType.TYPE })
public @interface Prototype {

	/**
	 * true:deep copy
	 * 
	 * false:shallow copy
	 * 
	 */
	boolean value() default false;

}
