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

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

import com.wuba.wsilk.codegen.model.ClassType;
import com.wuba.wsilk.codegen.model.SimpleType;
import com.wuba.wsilk.codegen.model.Type;
import com.wuba.wsilk.codegen.model.TypeCategory;
import com.wuba.wsilk.codegen.model.TypeExtends;

/**
 * 类型影射
 * 
 * @author mindashuang
 */
public abstract class AbstractTypeMappings {

	private final Map<String, Type> queryTypes = new HashMap<String, Type>();

	private final Map<TypeCategory, Type> pathTypes = new EnumMap<TypeCategory, Type>(TypeCategory.class);

	public Type getExprType(Type type, EntityMeta model, boolean raw) {
		return getExprType(type, model, raw, false, false);
	}

	public Type getExprType(Type type, EntityMeta model, boolean raw, boolean rawParameters, boolean extend) {
		return queryTypes.get(type.getFullName());
	}

	public Type getPathType(Type type, EntityMeta model, boolean raw) {
		return getPathType(type, model, raw, false, false);
	}

	public Type getPathType(Type type, EntityMeta model, boolean raw, boolean rawParameters, boolean extend) {
		if (queryTypes.containsKey(type.getFullName())) {
			return queryTypes.get(type.getFullName());
		} else {
			return getQueryType(pathTypes, type, model, raw, rawParameters, extend);
		}
	}

	private Type getQueryType(Map<TypeCategory, Type> types, Type type, EntityMeta model, boolean raw,
			boolean rawParameters, boolean extend) {
		Type exprType = types.get(type.getCategory());
		return getQueryType(type, model, exprType, raw, rawParameters, extend);
	}

	public Type getQueryType(Type type, EntityMeta model, Type exprType, boolean raw, boolean rawParameters,
			boolean extend) {
		TypeCategory category = type.getCategory();
		if (raw && category != TypeCategory.ENTITY && category != TypeCategory.CUSTOM) {
			return exprType;

		} else if (category == TypeCategory.STRING || category == TypeCategory.BOOLEAN) {
			return new SimpleType(exprType, new ClassType(model.getJavaClass()));

		} else {
			if (rawParameters) {
				type = new SimpleType(type);
			}
			if (!type.isFinal() && extend) {
				type = new TypeExtends(type);
			}
			return new SimpleType(exprType, type, new ClassType(model.getJavaClass()));

		}
	}

	public void register(Type type, Type queryType) {
		queryTypes.put(type.getFullName(), queryType);
	}

	public boolean isRegistered(Type type) {
		return queryTypes.containsKey(type.getFullName());
	}
}
