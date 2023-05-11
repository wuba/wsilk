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

package com.wuba.wsilk.core.serializer.java;

import java.io.IOException;
import java.io.Writer;
import java.lang.annotation.Annotation;

import com.wuba.wsilk.codegen.EntityMeta;
import com.wuba.wsilk.codegen.JavaWriter;
import com.wuba.wsilk.codegen.Meta;
import com.wuba.wsilk.codegen.PropertyMeta;
import com.wuba.wsilk.codegen.model.Type;
import com.wuba.wsilk.core.NoGenericException;
import com.wuba.wsilk.core.SourceEntityMeta;
import com.wuba.wsilk.core.Support;
import com.wuba.wsilk.core.WsilkConfiguration;
import com.wuba.wsilk.core.serializer.Context;
import com.wuba.wsilk.core.tf.AbstractEntityTypeFactory;

import lombok.Getter;

/**
 * java代码输出器
 * 
 * @author mindashuang
 */
public abstract class AbstractJavaSerializerDecorator<T extends SourceEntityMeta, W extends JavaWriter, M extends AbstractJavaSerializer<T, W>>
		extends AbstractJavaSerializer<T, W> {

	@Getter
	private M parent;

	public AbstractJavaSerializerDecorator(WsilkConfiguration conf, Class<? extends Annotation> annClass, M parent) {
		super(conf, annClass);
		this.parent = parent;
	}

	@Override
	public Type getSuperClass(T t) throws IOException {
		return parent.getSuperClass(t);
	}

	@Override
	public int getOrder() {
		return parent.getOrder();
	}

	@Override
	public AbstractEntityTypeFactory<? extends SourceEntityMeta> getEntityTypeFactory() {
		return parent.getEntityTypeFactory();
	}

	@Override
	public Support getSupport() {
		return parent.getSupport();
	}

	@Override
	public Class<T> getSourceEntityMetaClass() throws NoGenericException {
		return parent.getSourceEntityMetaClass();
	}

	@Override
	public Class<? extends Annotation> getAnnotationClass() {
		return parent.getAnnotationClass();
	}

	@Override
	public String getPath() {
		return parent.getPath();
	}

	@Override
	public PropertyMeta primary(EntityMeta em) {
		return parent.primary(em);
	}

	@Override
	public W codeWriter(Writer w) {
		return parent.codeWriter(w);
	}

	@Override
	public void end(W w, T em) throws IOException {
		parent.end(w, em);
	}

	@Override
	public Meta getOriginalMeta() {
		return parent.getOriginalMeta();
	}

	@Override
	public Context context() {
		return parent.context();
	}

}
