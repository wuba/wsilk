package com.wuba.wsilk.producer.meta;

import java.lang.annotation.Annotation;

import com.wuba.wsilk.core.Support;
import com.wuba.wsilk.core.WsilkConfiguration;
import com.wuba.wsilk.core.serializer.java.AbstractSingleJavaSerializer;

/**
 * 
 * Meta
 * 
 * @author mindashuang
 */
@Support(value = Meta.class, order = 1)
public class MetaJavaSerializer extends AbstractSingleJavaSerializer<MySourceEntityMeta> {

	public MetaJavaSerializer(WsilkConfiguration configuration, Class<? extends Annotation> annClass) {
		super(configuration, annClass);
	}

}
