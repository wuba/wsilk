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
package com.wuba.wsilk.codegen.annoataion;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

import com.google.common.collect.Lists;
import com.wuba.wsilk.codegen.Pair;

/**
 * 
 * 
 * 创建 AnnotationBean
 * 
 * @author mindashuang
 */
public class AnnotationUtils {

	/**
	 * 
	 * 通过注解生成 AnnotationMapValue
	 * 
	 */
	public static AnnotationBean createAnnotationBean(Annotation annotation, Function<Class<?>, String> getType) {
		Class<? extends Annotation> annotationType = annotation.annotationType();
		Method[] methods = annotationType.getDeclaredMethods();
		List<Pair<?>> pairs = Lists.newArrayList();
		for (Method method : methods) {
			Object value;
			try {
				value = method.invoke(annotation);
				Pair<?> valuePair = Pair.createPair(method.getName(), annotationConstant(value, getType));
				pairs.add(valuePair);
			} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
				e.printStackTrace();
			}
		}
		return createAnnotationBean(annotationType, getType, pairs);
	}

	private static Object annotationConstant(Object value, Function<Class<?>, String> getType) {
		Object pairValue = null;
		/** 如果是枚举 */
		if (value instanceof Annotation) {
			pairValue = createAnnotationBean((Annotation) value, getType);
		} else {
			Class<?> valueClass = value.getClass();
			if (valueClass.isArray()) {
				Object[] array = (Object[]) value;
				for (int i = 0; i < array.length; i++) {
					array[i] = annotationConstant(array[i], getType);
				}
				pairValue = new AnnotationValue(array);
			} else {
				pairValue = new AnnotationValue(value);
			}
		}
		return pairValue;
	}

	public static AnnotationBean createAnnotationBean(Class<? extends Annotation> type,
			Function<Class<?>, String> getType, Pair<?>... value) {
		return createAnnotationBean(type, getType, Arrays.asList(value));
	}

	private static AnnotationBean createAnnotationBean(Class<? extends Annotation> type,
			Function<Class<?>, String> getType, List<Pair<?>> pairs) {
		AnnotationBean annotationBean = createAnnotationBean(getType.apply(type));
		annotationBean.setPairs(pairs);
		return annotationBean;
	}

	private static AnnotationBean createAnnotationBean(String type) {
		return new AnnotationBean(type);
	}

	/**
	 * 
	 * 通过 AnnotationMapValue 生成 注解
	 */
//	@SuppressWarnings("unchecked")
//	public static <A extends Annotation> A createAnnotationInstance(AnnotationMapValue annotationMapValue) {
//		Map<String, Object> values = new HashMap<>();
//		for (Method method : annotationMapValue.getTypeClass().getDeclaredMethods()) {
//			values.put(method.getName(), method.getDefaultValue());
//		}
//		values.putAll(annotationMapValue);
//		return (A) AnnotationParser.annotationForMap(annotationMapValue.getTypeClass(), values);
//	}

}
