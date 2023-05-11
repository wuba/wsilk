package com.wuba.wsilk.producer.singleton;

import java.lang.annotation.Annotation;
import java.util.List;
import java.util.Set;

import javax.lang.model.element.Modifier;

import com.google.common.collect.Lists;
import com.wuba.wsilk.codegen.AnnotationMapValue;
import com.wuba.wsilk.core.SourceEntityMeta;
import com.wuba.wsilk.core.Support;
import com.wuba.wsilk.core.WsilkConfiguration;
import com.wuba.wsilk.core.serializer.java.AbstractSingleJavaSerializer;

/**
 * 
 * Singleton 的生成器
 * 
 * @author mindashuang
 */
@Support(value = Singleton.List.class, order = 5, suffix = "Singleton", pkgInlcudeSuffix = false)
public class SingletonsSerializer extends AbstractSingleJavaSerializer<SourceEntityMeta> {

	public SingletonsSerializer(WsilkConfiguration conf, Class<? extends Annotation> annClass) {
		super(conf, annClass);
	}

	protected List<AnnotationMapValue> annotationMapValues(SourceEntityMeta em) {
		return Lists.newArrayList(em.findAnnotation(Singleton.List.class).annotations());
	}

	@Override
	public void serialize(SourceEntityMeta em) {
		// 所属对象一定是抽象和私有的，否则不允许生成，以免产生非单实例的情况
		Set<Modifier> modifiers = em.getElement().getModifiers();
		if (!modifiers.contains(Modifier.ABSTRACT) || modifiers.contains(Modifier.PUBLIC)
				|| modifiers.contains(Modifier.PROTECTED)) {
			warning(em.getFullName() + " is not abstract and private,it can instance more entity");
		}
		List<CreateSerializer> createSerializers = Lists.newArrayList();
		// 基础信息
		List<AnnotationMapValue> annotationMapValues = annotationMapValues(em);

		for (AnnotationMapValue annotationMapValue : annotationMapValues) {
			CreateSerializer createSerializer = null;
			String value = annotationMapValue.enumString();
			if (value.equals(Singleton.Type.LAZY.name())) {
				createSerializer = new LazySerializer(getConfiguration(), getAnnotationClass(), this,
						annotationMapValue);
			} else if (value.equals(Singleton.Type.EAGER.name())) {
				createSerializer = new EagerSerializer(getConfiguration(), getAnnotationClass(), this,
						annotationMapValue);
			}
			createSerializers.add(createSerializer);
		}

		int size = createSerializers.size();
		for (int i = 0; i < size; i++) {
			createSerializers.get(i).serialize(context(em, i, size));
		}
	}

}
