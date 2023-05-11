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

package com.wuba.wsilk.core.serializer;

import java.lang.annotation.Annotation;

import com.wuba.wsilk.codegen.AnnotationMapValue;
import com.wuba.wsilk.codegen.Meta;
import com.wuba.wsilk.core.AbstractConfigAble;
import com.wuba.wsilk.core.ConfigAble;
import com.wuba.wsilk.core.SourceEntityMeta;
import com.wuba.wsilk.core.WsilkConfiguration;

import lombok.Getter;
import lombok.Setter;

/**
 * 写代码的处理器
 * 
 * @author mindashuang
 */
public abstract class AbstractMetaSerializer<T extends SourceEntityMeta> extends AbstractConfigAble
		implements Serializer<T>, ConfigAble {

	@Setter
	@Getter
	private Meta originalMeta;

	/**
	 * 触发这个生成器的注解
	 */
	@Setter
	private AnnotationMapValue triggerAnnotation;

	/**
	 * 注解类
	 */
	@Setter
	private Class<? extends Annotation> triggerannotationClass;

	public AbstractMetaSerializer(WsilkConfiguration conf) {
		super(conf);
	}

}
