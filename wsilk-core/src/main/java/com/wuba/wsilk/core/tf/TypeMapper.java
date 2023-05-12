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

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import javax.lang.model.type.TypeMirror;

import com.wuba.wsilk.codegen.model.Type;

/**
 * 类型 mapper
 * 
 * @author mindashuang
 */
public class TypeMapper {

	private final Map<List<String>, Type> typeCache = new HashMap<List<String>, Type>();

	private TypeMapper() {
	}

	private final static TypeMapper INSTANCE = new TypeMapper();

	public final static TypeMapper getInstance() {
		return INSTANCE;
	}

	/**
	 * 获得 Type
	 */
	public Type getType(TypeMirror typeMirror, Function<TypeMirror, List<String>> nameFunction,
			Function<TypeMirror, Type> typeFunction) {
		// 拿到类的key
		List<String> names = nameFunction.apply(typeMirror);
		Type type = typeCache.get(names);
		if (type == null) {
			type = typeFunction.apply(typeMirror);
			typeCache.put(names, type);
		}
		return type;
	}

	public Type getType(TypeMirror typeMirror, Function<TypeMirror, List<String>> nameFunction) {
		List<String> names = nameFunction.apply(typeMirror);
		return typeCache.get(names);
	}

}
