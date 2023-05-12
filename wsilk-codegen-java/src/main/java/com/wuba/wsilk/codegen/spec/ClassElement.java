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

package com.wuba.wsilk.codegen.spec;

import com.wuba.wsilk.codegen.Modifier;
import com.wuba.wsilk.codegen.model.Type;

import lombok.Getter;
import lombok.Setter;

/**
 * 
 * 创建一个方法
 * 
 * @author mindashuang
 */

@Getter
public class ClassElement extends Element {

	private final Modifier.Class modifier;

	private final Type type;

	@Setter
	private Type[] interfaces;

	@Setter
	private Type superClass;

	@Setter
	private boolean isInterface;

	public ClassElement(Modifier.Class modifier, String name, Type type) {
		super(name);
		this.modifier = modifier;
		this.type = type;
	}

	public final static ClassElement builder(Modifier.Class modifier, String name, Type type) {
		return new ClassElement(modifier, name, type);
	}
}
