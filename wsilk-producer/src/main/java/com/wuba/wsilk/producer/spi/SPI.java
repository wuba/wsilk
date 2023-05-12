package com.wuba.wsilk.producer.spi;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * spi
 * 
 * @author mindashuang
 */
@Documented
@Retention(RetentionPolicy.SOURCE)
@Target({ ElementType.TYPE })
public @interface SPI {

	Class<?> value();

}
