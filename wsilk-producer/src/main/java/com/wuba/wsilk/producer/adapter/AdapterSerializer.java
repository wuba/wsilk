package com.wuba.wsilk.producer.adapter;

import java.io.IOException;
import java.lang.annotation.Annotation;

import javax.lang.model.type.TypeMirror;

import com.wuba.wsilk.codegen.AnnotationMapValue;
import com.wuba.wsilk.codegen.EntityMeta;
import com.wuba.wsilk.codegen.JavaWriter;
import com.wuba.wsilk.core.NoGenericException;
import com.wuba.wsilk.core.SourceEntityMeta;
import com.wuba.wsilk.core.Support;
import com.wuba.wsilk.core.WsilkConfiguration;
import com.wuba.wsilk.core.serializer.java.AbstractSingleJavaSerializer;

/**
 * 
 * 原型模式
 * 
 * @author mindashuang
 */
@Support(value = Adapter.class, order = 1, pkgInlcudeSuffix = false, override = true, suffix = "Adapter")
public class AdapterSerializer extends AbstractSingleJavaSerializer<SourceEntityMeta> {

	EntityMeta targEntityMeta;

	public AdapterSerializer(WsilkConfiguration configuration, Class<? extends Annotation> annClass) {
		super(configuration, annClass);
	}

	@Override
	public SourceEntityMeta init(SourceEntityMeta em) throws NoGenericException {
		AnnotationMapValue info = em.findAnnotation(getAnnotationClass());
		TypeMirror target = info.getTypeMirror("target");
		targEntityMeta = getEntityTypeFactory().createEntityMeta(target);
		// 方法名
		return super.init(em);
	}

	@Override
	public void createJavaHead(JavaWriter writer, SourceEntityMeta t) throws IOException {
		writer.beginClass(t, getSuperClass(t), getSuperInterface(t));
	}

	@Override
	public void importPackage(JavaWriter writer, SourceEntityMeta em) throws IOException {
		writer.importClasses(em.getFullName());
	}

}
