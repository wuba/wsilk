package com.wuba.wsilk.producer.convert;

import java.lang.annotation.Annotation;

import com.wuba.wsilk.core.SourceEntityMeta;
import com.wuba.wsilk.core.Support;
import com.wuba.wsilk.core.WsilkConfiguration;
import com.wuba.wsilk.core.serializer.java.AbstractSingleJavaSerializer;

/**
 * 
 * spi
 * 
 * @author mindashuang
 */
@Support(value = Convert.class, order = 1, pkgInlcudeSuffix = false, parentPkg = false, override = true)
public class ConvertJavaSerializer extends AbstractSingleJavaSerializer<SourceEntityMeta> {

	public ConvertJavaSerializer(WsilkConfiguration configuration, Class<? extends Annotation> annClass) {
		super(configuration, annClass);
	}

}
