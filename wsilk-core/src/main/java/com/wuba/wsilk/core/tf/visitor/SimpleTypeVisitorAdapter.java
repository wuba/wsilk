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

package com.wuba.wsilk.core.tf.visitor;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.SimpleTypeVisitor8;

/**
 * 访问模式
 * 
 * @author mindashuang
 */
public class SimpleTypeVisitorAdapter<R, P> extends SimpleTypeVisitor8<R, P> {

	private static final Class<?> INTER_SECTION_TYPE_CLASS;

	private static final Method GET_BOUNDS_METHOD;

	static {
		Class<?> availableClass;
		Method availableMethod;
		try {
			availableClass = Class.forName("javax.lang.model.type.IntersectionType");
			availableMethod = availableClass.getMethod("getBounds");
		} catch (Exception e) {
			// Not using Java 8
			availableClass = null;
			availableMethod = null;
		}
		INTER_SECTION_TYPE_CLASS = availableClass;
		GET_BOUNDS_METHOD = availableMethod;
	}

	@SuppressWarnings("unchecked")
	@Override
	public R visitUnknown(TypeMirror t, P p) {
		if (INTER_SECTION_TYPE_CLASS != null && INTER_SECTION_TYPE_CLASS.isInstance(t)) {
			try {
				List<TypeMirror> bounds = (List<TypeMirror>) GET_BOUNDS_METHOD.invoke(t);
				return bounds.get(0).accept(this, p);
			} catch (IllegalAccessException e) {
				throw new RuntimeException(e.getMessage(), e);
			} catch (InvocationTargetException e) {
				throw new RuntimeException(e.getMessage(), e);
			}
		} else {
			return super.visitUnknown(t, p);
		}
	}

}
