package com.wuba.wsilk.producer.singleton;

import java.io.IOException;
import java.lang.annotation.Annotation;

import org.apache.commons.lang3.StringUtils;

import com.wuba.wsilk.codegen.AnnotationMapValue;
import com.wuba.wsilk.codegen.JavaWriter;
import com.wuba.wsilk.codegen.Modifier;
import com.wuba.wsilk.codegen.model.Type;
import com.wuba.wsilk.core.NoGenericException;
import com.wuba.wsilk.core.SourceEntityMeta;
import com.wuba.wsilk.core.WsilkConfiguration;
import com.wuba.wsilk.core.serializer.java.AbstractJavaSerializerDecorator;

import lombok.Getter;

/**
 * 
 * 创建规则
 * 
 * @author mindashuang
 */
public class CreateSerializer
		extends AbstractJavaSerializerDecorator<SourceEntityMeta, JavaWriter, SingletonsSerializer> {

	@Getter
	private AnnotationMapValue annotationMapValue;

	public final static String NAME = "INSTANCE";

	public final static String GET_NAME = "getInstance";

	public CreateSerializer(WsilkConfiguration conf, Class<? extends Annotation> annClass, SingletonsSerializer parent,
			AnnotationMapValue annotationMapValue) {
		super(conf, annClass, parent);
		this.annotationMapValue = annotationMapValue;
	}

	@Override
	public SourceEntityMeta init(SourceEntityMeta em) throws NoGenericException {
		String name = getName();
		// 更新javaName的名字
		em.setJavaName(em.getSimpleName() + (name == null ? "" : name) + getSupport().suffix());
		return super.init(em);
	}

	public String getName() {
		String name = annotationMapValue.string("name");
		if (StringUtils.isEmpty(name)) {
			name = defaultName();
		}
		return name;
	}

	public String defaultName() {
		return "";
	}

	@Override
	public Type getSuperClass(SourceEntityMeta t) throws IOException {
		return t.getOriginal();
	}

	@Override
	public void constructors(JavaWriter writer, SourceEntityMeta em) throws IOException {
		writer.beginConstructor(Modifier.Field.PRIVATE);
		writer.end();
	}

}
