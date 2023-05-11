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

package com.wuba.wsilk.codegen.model;

import static com.wuba.wsilk.common.Symbols.*;

import java.util.Collections;
import java.util.Set;

import lombok.Setter;

/**
 * 泛型的自己引用自己
 * 
 * @author mindashuang
 */
public class TypeThis extends TypeAdapter {

	@Setter
	private Type type;

	public TypeThis() {
		super(Types.OBJECT);
	}

	@Override
	public String getGenericName(boolean asArgType) {
		return getGenericName(asArgType, Collections.<String>emptySet(), Collections.<String>emptySet());
	}

	@Override
	public String getGenericName(boolean asArgType, Set<String> packages, Set<String> classes) {
		if (!asArgType) {
			if (type instanceof TypeExtends) {
				return HOOK;
			} else {
				return HOOK + SPACE + SUPER + SPACE + type.getGenericName(true, packages, classes);
			}

		} else {
			return super.getGenericName(asArgType, packages, classes);
		}
	}

}
