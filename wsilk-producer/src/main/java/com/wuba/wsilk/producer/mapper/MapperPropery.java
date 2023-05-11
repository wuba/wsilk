package com.wuba.wsilk.producer.mapper;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * 属性
 * 
 * @author mindashuang
 */
@Documented
@Target({ ElementType.FIELD })
@Retention(RUNTIME)
public @interface MapperPropery {

	String value();

}
