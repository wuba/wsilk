package com.wuba.wsilk.producer.config;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 配置信息
 * 
 * @author mindashuang
 */
@Documented
@Retention(RetentionPolicy.SOURCE)
@Target({ ElementType.PACKAGE })
public @interface Config {

	/** 默认的config */
	String value();

	String name() default "Bundle";

}