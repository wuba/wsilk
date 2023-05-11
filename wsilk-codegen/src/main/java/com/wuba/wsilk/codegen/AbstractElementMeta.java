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

import static com.wuba.wsilk.common.Symbols.*;

import java.util.HashMap;
import java.util.Map;

import javax.lang.model.SourceVersion;

import lombok.Getter;
import lombok.Setter;

/**
 * 元数据
 * 
 * @author mindashuang
 */
public abstract class AbstractElementMeta implements Meta {

	/** 所属实体 */
	@Getter
	private final EntityMeta entityMeta;

	@Getter
	private final String name;

	@Getter
	private final String escapedName;

	@Getter
	@Setter
	private Map<String, ? extends AnnotationMapValue> annotations = new HashMap<>();

	public AbstractElementMeta(EntityMeta entityMeta, String name) {
		this.entityMeta = entityMeta;
		this.name = name;
		this.escapedName = escapeName(name);
	}

	private static String escapeName(String name) {
		if (SourceVersion.isKeyword(name)) {
			name = name + DOLLAR;
		} else if (!Character.isJavaIdentifierStart(name.charAt(0))) {
			name = UNDERLINE + name;
		}
		return name;
	}

	public boolean hasEntityFields() {
		return entityMeta.hasEntityFields();
	}

}
