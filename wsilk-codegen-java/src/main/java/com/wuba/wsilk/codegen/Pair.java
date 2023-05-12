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

import java.util.Collection;

import com.wuba.wsilk.codegen.annoataion.AnnotationBean;
import com.wuba.wsilk.codegen.annoataion.AnnotationValue;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

/**
 * 
 * 注解的值
 * 
 * @author mindashuang
 */

@Getter
@Setter
@EqualsAndHashCode
public class Pair<T> {

	public final static String VALUE = "value";

	private String name;

	private T value;

	public Pair(String name, T value) {
		this.name = name;
		this.value = value;
	}

	public Pair(T value) {
		this.value = value;
	}

	@Override
	public String toString() {
		return "ValuePair [name=" + name + ", value=" + value + "]";

	}

	public static Pair<?> createPair(String name, Object value) {
		if (value.getClass().isArray()) {
			return createArrayPair(name, (Object[]) value);
		} else if (value instanceof Collection) {
			return createArrayPair(name, ((Collection<?>) value).toArray());
		} else if (value instanceof AnnotationValue) {
			return createValuePair(name, (AnnotationValue) value);
		} else {
			return createValuePair(name, value);
		}
	}

	public static Pair<?> createPair(Object value) {
		return createPair(VALUE, value);
	}

	/**
	 * ValuePair
	 */
	public static ValuePair createValuePair(AnnotationValue value) {
		return new ValuePair(VALUE, value);
	}

	public static ValuePair createValuePair(String name, AnnotationValue value) {
		return new ValuePair(name, value);
	}

	public static ValuePair createValuePair(String name, Object value) {
		return createValuePair(name, new AnnotationValue(value));
	}

	public static ValuePair createValuePair(Object value) {
		return createValuePair(new AnnotationValue(value));
	}

	/**
	 * BeanPair
	 */

	public static BeanPair createBeanPair(AnnotationBean value) {
		return new BeanPair(VALUE, value);
	}

	public static BeanPair createBeanPair(String name, AnnotationBean value) {
		return new BeanPair(name, value);
	}

	/**
	 * ArrayPair
	 */
	public static ArrayPair createArrayPair(String name, Object[] value) {
		return new ArrayPair(name, value);
	}

	public static ArrayPair createArrayPair(Object[] value) {
		return new ArrayPair(value);
	}

	public static class BeanPair extends Pair<AnnotationBean> {

		public BeanPair(AnnotationBean value) {
			super(value);
		}

		public BeanPair(String name, AnnotationBean value) {
			super(name, value);
		}

	}

	public static class ValuePair extends Pair<AnnotationValue> {

		public ValuePair(AnnotationValue value) {
			super(value);
		}

		public ValuePair(String name, AnnotationValue value) {
			super(name, value);
		}
	}

	public static class ArrayPair extends Pair<Object[]> {

		public ArrayPair(Object[] value) {
			super(value);
		}

		public ArrayPair(String name, Object[] value) {
			super(name, value);
		}
	}

}
