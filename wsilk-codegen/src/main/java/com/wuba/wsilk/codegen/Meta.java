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

package com.wuba.wsilk.codegen;

import java.lang.annotation.Annotation;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import javax.lang.model.type.TypeMirror;

import com.google.common.collect.Lists;
import com.wuba.wsilk.codegen.model.Type;
import com.wuba.wsilk.common.ClassUtils;

/**
 * 基础元数据
 * 
 * @author mindashuang
 */
public interface Meta {

	/**
	 * 获得注解信息
	 * 
	 * @return 注解信息
	 * 
	 */
	public Map<String, ? extends AnnotationMapValue> getAnnotations();

	/**
	 * 设置注解信息
	 * 
	 * @param annotations 注解信息
	 * 
	 */
	public void setAnnotations(Map<String, ? extends AnnotationMapValue> annotations);

	/**
	 * 查找注解值
	 * 
	 * @param annotation 注解类型
	 * @return 获得注解值
	 * 
	 */
	default AnnotationMapValue findAnnotation(Class<? extends Annotation> annotation) {
		AnnotationMapValue annotationMapValue = null;
		if (getAnnotations() != null) {
			String name = ClassUtils.getFullName(annotation);
			annotationMapValue = getAnnotations().get(name);
		}
		return annotationMapValue == null ? EMPTY : annotationMapValue;
	}

	default List<AnnotationMapValue> findAnnotations(Class<? extends Annotation> list,
			Class<? extends Annotation> annotation) {
		return findAnnotations(list, annotation, null);
	}

	default List<AnnotationMapValue> findAnnotations(Class<? extends Annotation> list,
			Class<? extends Annotation> annotation, String key) {
		AnnotationMapValue l = findAnnotation(list);
		AnnotationMapValue s = findAnnotation(annotation);
		List<AnnotationMapValue> merge = Lists.newArrayList();
		if (!l.isNull()) {
			AnnotationMapValue[] arrays = key == null ? l.annotations() : l.annotations(key);
			if (arrays != null) {
				for (AnnotationMapValue a : arrays) {
					merge.add(a);
				}
			}
		}
		if (!s.isNull()) {
			merge.add(s);
		}
		return merge;

	}

	@SuppressWarnings("serial")
	public final static AnnotationMapValue EMPTY = new AnnotationMapValue() {

		@Override
		public String string(String key, String def) {
			return def;
		}

		@Override
		public Boolean bool(String key, Boolean def) {
			return def;
		}

		@Override
		public Integer integer(String key, Integer def) {
			return def;
		}

		@Override
		public Long toLong(String key, Long def) {
			return def;
		}

		@Override
		public void init() {
		}

		@Override
		public String enumString(String key) {
			return null;
		}

		@Override
		public TypeMirror getTypeMirror(String key) {
			return null;
		}

		@Override
		public AnnotationMapValue[] annotations(String key) {
			return null;
		}

		@Override
		public AnnotationMapValue annotation(String key) {
			return null;
		}

		@Override
		public Type type(String key) {
			return null;
		}

		@Override
		public Type[] types(String key) {
			return null;
		}

		@Override
		public String[] strings(String key) {
			return null;
		}

		@Override
		public Boolean[] bools(String key) {
			return null;
		}

		@Override
		public Integer[] integers(String key) {
			return null;
		}

		@Override
		public Long[] toLongs(String key) {
			return null;
		}

		@Override
		public String[] enumStrings(String key) {
			return null;
		}

		@Override
		public <T> T[] array(String key, Function<Object, T> function, Function<Integer, T[]> size) {
			return null;
		}

		@Override
		public boolean isNull() {
			return true;
		}

	};
}
