package com.wuba.wsilk.producer.factory;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 工厂模式 和方法工厂模式
 * 
 * @author mindashuang
 */
@Documented
@Retention(RetentionPolicy.SOURCE)
@Target({ ElementType.TYPE, ElementType.ANNOTATION_TYPE })
public @interface Factory {

	OptionType value() default OptionType.STRING;// option 数据类型

	Option[] options();

	// 选择器放在构造器中

	boolean constructorOptionValue() default false;

	public @interface Option {

		Class<?> bean();

		String optionValue(); // option 数据类型

	}

	/**
	 * 工厂类型
	 */
	static enum FactoryType {

		/** 简单工厂 */
		SIMPLE,
		/** 方法工厂 */
		METHOD

	}

	/**
	 * 
	 * 选项
	 * 
	 */
	static enum OptionType {
		/** 字符串类型 */
		STRING,
		/** 整数类型 **/
		INTEGER,
		/** long */
		LONG,
		/** double */
		DOUBLE,
		/** float */
		FLOAT,
		/** bool */
		BOOLEAN,
		/** byte */
		BYTE,
		/** sort */
		SHORT,
		/** char */
		CHAR,
		/** 枚举类型 */
		ENUM

	}

}
