/*
 * Copyright (C) 2005-present, 58.com.  All rights reserved.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.wuba.wsilk.core.process;

import java.lang.annotation.Annotation;
import java.util.Set;

import javax.inject.Inject;
import javax.inject.Named;
import javax.lang.model.element.Element;

import com.wuba.wsilk.core.AbstractConfigAble;
import com.wuba.wsilk.core.Process;
import com.wuba.wsilk.core.SourceEntityMeta;
import com.wuba.wsilk.core.WsilkConfiguration;
import com.wuba.wsilk.core.serializer.Serializer;
import com.wuba.wsilk.core.tf.AbstractEntityTypeFactory;

import lombok.Getter;
import lombok.Setter;

/**
 * 处理器
 * 
 * @author mindashuang
 */
@SuppressWarnings("rawtypes")
public class DynamicProcess extends AbstractConfigAble implements Process, Comparable<DynamicProcess> {

	@Getter
	private AbstractEntityTypeFactory<? extends SourceEntityMeta> entityTypeFactory;

	@Getter
	private Serializer<? extends SourceEntityMeta> serializer;

	@Getter
	@Setter
	private Class<? extends Annotation> annotation;

	@Inject
	public DynamicProcess(@Named("conf") WsilkConfiguration conf) {
		super(conf);
		entityTypeFactory = conf.getEntityTypeFactory();
	}

	public void builder(Class<? extends Annotation> annotation, Class<? extends Serializer> serializerClass) {
		this.annotation = annotation;
		this.serializer = buildSerializer(getAnnotation(), serializerClass);
	}

	@Override
	public <E extends Element> void init(Set<E> elements) {
		elements.stream().forEach(e -> {
			/** 设置entityTypeFactory 的注解 */
			entityTypeFactory.setAnnotation(getAnnotation());
			entityTypeFactory.init(e);
		});
	}

	/**
	 * 初始化 EntityType
	 * 
	 */
	@Override
	public <E extends Element> void process(Set<E> elements) {
		/** 总存量 */
		int capacity = elements.size();
		int index = 0;
		if (serializer != null) {
			for (E e : elements) {
				index++;
				/** 设置entityTypeFactory 的注解 */
				entityTypeFactory.setAnnotation(getAnnotation());
				SourceEntityMeta entityType = entityTypeFactory.getEntityMeta(e);
				entityType.reInit();
				serializer.serialize(serializer.context(entityType, index, capacity));
			}
		}
	}

	public void over() {
		serializer.over();
	}

	public Serializer<? extends SourceEntityMeta> buildSerializer(Class<? extends Annotation> annotation,
			Class<? extends Serializer> serializerClass) {
		Class<? extends Serializer> serializer = serializerClass;
		return getConfiguration().getInstanceUtils().createInstance(serializer, getConfiguration(), annotation);
	}

	public int order() {
		return serializer.getOrder();
	}

	@Override
	public int compareTo(DynamicProcess o) {
		return this.order() - o.order();
	}

}
