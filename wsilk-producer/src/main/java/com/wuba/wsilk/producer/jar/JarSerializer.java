package com.wuba.wsilk.producer.jar;

import java.lang.annotation.Annotation;

import com.wuba.wsilk.common.Dependency;
import com.wuba.wsilk.core.SourceEntityMeta;
import com.wuba.wsilk.core.Support;
import com.wuba.wsilk.core.WsilkConfiguration;
import com.wuba.wsilk.core.serializer.java.AbstractSingleJavaSerializer;

/**
 * 
 * 组合模式
 * 
 * @author mindashuang
 */
@Support(value = Jar.class, order = 1, pkgInlcudeSuffix = false, override = true, suffix = "Jar")
@Dependency(groupId = "commons-fileupload", artifactId = "commons-fileupload", version = "1.5")
public class JarSerializer extends AbstractSingleJavaSerializer<SourceEntityMeta> {

	public JarSerializer(WsilkConfiguration conf, Class<? extends Annotation> annClass) {
		super(conf, annClass);
	}

}
