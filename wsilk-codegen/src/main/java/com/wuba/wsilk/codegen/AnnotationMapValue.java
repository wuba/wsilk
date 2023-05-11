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

import java.util.HashMap;
import java.util.function.Function;

import javax.lang.model.type.TypeMirror;

import com.wuba.wsilk.codegen.model.Type;

/**
 * 
 * 注解信息
 * 
 * @author mindashuang
 */
public abstract class AnnotationMapValue extends HashMap<String, Object> {

	private static final long serialVersionUID = 3965068787666078020L;

	public final static String VALUE = "value";

	public <M> M value(String key, Function<Object, M> function, M def) {
		init();
		Object obj = get(key);
		return obj != null ? function.apply(obj) : def;
	}

	public abstract String string(String key, String def);

	public abstract Boolean bool(String key, Boolean def);

	public abstract Integer integer(String key, Integer def);

	public abstract Long toLong(String key, Long def);

	public String string() {
		return string(VALUE, null);
	}

	public String string(String key) {
		return string(key, null);
	}

	public String stringDef(String def) {
		return string(VALUE, def);
	}

	public abstract String[] strings(String key);

	public String[] strings() {
		return strings(VALUE);
	}

	public Boolean bool() {
		return bool(VALUE, null);
	}

	public Boolean bool(String key) {
		return bool(key, null);
	}

	public Boolean bool(Boolean def) {
		return bool(VALUE, def);
	}

	public abstract Boolean[] bools(String key);

	public Boolean[] bools() {
		return bools(VALUE);
	}

	public Integer integer(String key) {
		return integer(key, null);
	}

	public Integer integer() {
		return integer(VALUE, null);
	}

	public Integer integer(Integer def) {
		return integer(VALUE, def);
	}

	public abstract Integer[] integers(String key);

	public Integer[] integers() {
		return integers(VALUE);
	}

	public Long toLong(String key) {
		return toLong(key, null);
	}

	public Long toLong() {
		return toLong(VALUE, null);
	}

	public Long toLong(Long def) {
		return toLong(VALUE, def);
	}

	public abstract Long[] toLongs(String key);

	public Long[] toLongs() {
		return toLongs(VALUE);
	}

	public String enumString() {
		return enumString(VALUE);
	}

	public abstract String[] enumStrings(String key);

	public String[] enumStrings() {
		return enumStrings(VALUE);
	}

	public abstract void init();

	public abstract String enumString(String key);

	public abstract TypeMirror getTypeMirror(String key);

	public abstract AnnotationMapValue[] annotations(String key);

	public abstract AnnotationMapValue annotation(String key);

	public abstract Type type(String key);

	public abstract Type[] types(String key);

	public abstract <T> T[] array(String key, Function<Object, T> function, Function<Integer, T[]> size);

	public TypeMirror getTypeMirror() {
		return getTypeMirror(VALUE);
	}

	public AnnotationMapValue[] annotations() {
		return annotations(VALUE);
	}

	public Type type() {
		return type(VALUE);
	}

	public Type[] types() {
		return types(VALUE);
	}

	public abstract boolean isNull();

	@Override
	public String toString() {
		return "AnnotationMapValue [values=" + super.toString() + ", isNull()=" + isNull() + "]";
	}

}
