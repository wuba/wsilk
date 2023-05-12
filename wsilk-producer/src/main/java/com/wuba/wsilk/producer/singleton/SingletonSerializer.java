package com.wuba.wsilk.producer.singleton;

import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.List;

import com.wuba.wsilk.codegen.AnnotationMapValue;
import com.wuba.wsilk.core.SourceEntityMeta;
import com.wuba.wsilk.core.Support;
import com.wuba.wsilk.core.WsilkConfiguration;

/**
 * 
 * Singleton 的问题
 * 
 * @author mindashuang
 */
@Support(value = Singleton.class, order = 5, suffix = "Singleton", pkgInlcudeSuffix = false)
public class SingletonSerializer extends SingletonsSerializer {

	public SingletonSerializer(WsilkConfiguration configuration, Class<? extends Annotation> annClass) {
		super(configuration, annClass);
	}

	@Override
	protected List<AnnotationMapValue> annotationMapValues(SourceEntityMeta em) {
		return Arrays.asList(em.findAnnotation(Singleton.class));
	}
}
