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
import com.wuba.wsilk.codegen.model.Parameter;
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
public class MethodElement extends Element {

	private final Modifier.Field modifier;

	private final Type returnType;

	private final Parameter[] args;

	@Setter
	private Type[] exceptions;

	public MethodElement(Modifier.Field modifier, Type returnType, String name, Parameter... args) {
		super(name);
		this.modifier = modifier;
		this.returnType = returnType;
		this.args = args;

	}

	public final static MethodElement builder(Modifier.Field modifier, Type returnType, String name,
			Parameter... args) {
		return new MethodElement(modifier, returnType, name, args);
	}

	public final static MethodElement builder(Type returnType, String name, Parameter... args) {
		return builder(Modifier.Field.PUBLIC, returnType, name, args);
	}

}
