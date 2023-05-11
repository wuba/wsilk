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

package com.wuba.wsilk.core.tf;

import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.type.TypeMirror;

import javax.lang.model.util.SimpleAnnotationValueVisitor8;
//import javax.lang.model.util.SimpleAnnotationValueVisitor9;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.wuba.wsilk.codegen.AnnotationMapValue;
import com.wuba.wsilk.codegen.model.Type;
import com.wuba.wsilk.core.WsilkConfiguration;
import static com.wuba.wsilk.core.utils.AstReflectUtils.*;

import java.util.Collections;
import java.util.List;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.element.VariableElement;
import javax.lang.model.element.Name;

/**
 * 注解解析
 */
public class AnnotationTypeFactory extends TypeFactory {

	private final AbstractTypeFactory abstractTypeFactory;

	public AnnotationTypeFactory(WsilkConfiguration configuration, AbstractTypeFactory abstractTypeFactory) {
		super(configuration);
		this.abstractTypeFactory = abstractTypeFactory;
	}

	public Map<String, AnnotationMapValue> create(Element e) {
		Map<String, AnnotationMapValue> amap = Maps.newHashMap();
		List<? extends AnnotationMirror> annotationList = elementUtils.getAllAnnotationMirrors(e);
		// 遍历注解
		for (AnnotationMirror annotationMirror : annotationList) {
			String clsName = annotationMirror.getAnnotationType().toString();
			AnnotationMapValueHolder annotationElementValue = new AnnotationMapValueHolder(annotationMirror);
			amap.put(clsName, annotationElementValue);
		}
		return amap;
	}

	public class AnnotationMapValueHolder extends AnnotationMapValue {

		private static final long serialVersionUID = 1658594202944116066L;

		final AnnotationMirror annotationMirror;

		private boolean init = false;

		public AnnotationMapValueHolder(AnnotationMirror annotationMirror) {
			this.annotationMirror = annotationMirror;
		}

		@Override
		public TypeMirror getTypeMirror(String key) {
			return value(key, (e) -> {
				return getTypeMirror((AnnotationValue) e);
			}, null);
		}

		@Override
		public String string(String key, String def) {
			return simple(key, def);
		}

		@Override
		public String[] strings(String key) {
			return array(key, (e) -> {
				return (String) value(e);
			}, l -> new String[l]);
		}

		public Boolean bool(String key, Boolean def) {
			return simple(key, def);
		}

		@Override
		public Boolean[] bools(String key) {
			return array(key, (e) -> {
				return (Boolean) value(e);
			}, l -> new Boolean[l]);
		}

		public Integer integer(String key, Integer def) {
			return simple(key, def);
		}

		@Override
		public Integer[] integers(String key) {
			return array(key, (e) -> {
				return (Integer) value(e);
			}, l -> new Integer[l]);
		}

		public Long toLong(String key, Long def) {
			return simple(key, def);
		}

		@Override
		public Long[] toLongs(String key) {
			return array(key, (e) -> {
				return (Long) value(e);
			}, l -> new Long[l]);
		}

		@Override
		public String enumString(String key) {
			return simple(key, null);
		}

		@Override
		public String[] enumStrings(String key) {
			return array(key, (e) -> {
				return (String) value(e);
			}, l -> new String[l]);
		}

		@SuppressWarnings("unchecked")
		public <T> T simple(String key, T def) {
			return value(key, (e) -> {
				Object v = value(e);
				return (T) v;
			}, def);
		}

		@SuppressWarnings("unchecked")
		@Override
		public <T> T[] array(String key, Function<Object, T> function, Function<Integer, T[]> size) {
			return value(key, (e) -> {
				Object v = value(e);
				T[] values = null;
				List<Object> list = null;
				if (v instanceof List) {
					list = (List<Object>) v;
				} else if (v.getClass().isArray()) {
					Object[] array = (Object[]) v;
					list = Lists.newArrayList();
					for (Object o : array) {
						list.add(o);
					}
				}
				if (list != null) {
					values = list.stream().map((k) -> {
						return function.apply(k);
					}).toArray((l) -> {
						return size.apply(l);
					});
				}
				return values;
			}, null);
		}

		@Override
		public AnnotationMapValue[] annotations(String key) {
			return array(key, (e) -> {
				return new AnnotationMapValueHolder((AnnotationMirror) e);
			}, l -> new AnnotationMapValue[l]);
		}

		@Override
		public AnnotationMapValue annotation(String key) {
			return value(key, (e) -> {
				return new AnnotationMapValueHolder(getAnnotationType(e));
			}, null);
		}

		@Override
		public Type type(String key) {
			return value(key, (e) -> {
				Object v = value(e);
				return abstractTypeFactory.type(asElement(v));
			}, null);
		}

		@Override
		public Type[] types(String key) {
			return value(key, (e) -> {
				Type[] types = null;
				Set<TypeMirror> typeMirrors = getTypeMirrors((AnnotationValue) e);
				if (typeMirrors != null && typeMirrors.size() > 0) {
					types = typeMirrors.stream().map((k) -> {
						return abstractTypeFactory.type(asElement((DeclaredType) k));
					}).toArray((l) -> {
						return new Type[l];
					});
				}
				return types;
			}, null);
		}

		/**
		 * 初始化
		 */
		public void init() {
			if (!init) {
				Map<? extends ExecutableElement, ? extends AnnotationValue> map = elementUtils
						.getElementValuesWithDefaults(annotationMirror);
				map.entrySet().stream().forEach(k -> {
					put(k.getKey().getSimpleName().toString(), k.getValue());
				});
				init = true;
			}
		}

		public Object value(Object e) {
			String value = e.getClass().getName();
			if (value.endsWith("Attribute$Enum")) {
				Object v = getValue(e);
				VariableElement k = (VariableElement) v;
				Name obj = k.getSimpleName();
				return obj.toString();
			} else if (value.endsWith("Attribute$Constant") || value.endsWith("Attribute$Array")) {
				// 是对象上的值
				return getValue(e);
			} else if (e instanceof AnnotationValue) {
				return ((AnnotationValue) e).getValue();
			} else
				return e;
		}

		public Set<TypeMirror> getTypeMirrors(AnnotationValue annotationValue) {
			Set<TypeMirror> typeMirrors = annotationValue
					.accept(new SimpleAnnotationValueVisitor8<Set<TypeMirror>, Void>() {
						@Override
						public Set<TypeMirror> visitType(TypeMirror typeMirror, Void v) {
							return Collections.singleton(typeMirror);
						}

						@Override
						public Set<TypeMirror> visitArray(java.util.List<? extends AnnotationValue> values, Void p) {
							return values.stream().flatMap(value -> value.accept(this, null).stream())
									.collect(Collectors.toSet());
						}
					}, null);
			return typeMirrors;
		}

		public TypeMirror getTypeMirror(AnnotationValue annotationValue) {
			Set<TypeMirror> typeMirrors = getTypeMirrors(annotationValue);
			if (typeMirrors.size() > 0) {
				return typeMirrors.iterator().next();
			}
			return null;
		}

		@Override
		public boolean isNull() {
			return false;
		}

	}
}
