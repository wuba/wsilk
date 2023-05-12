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

import java.util.Arrays;
import java.util.Objects;

import com.wuba.wsilk.codegen.model.Parameter;
import com.wuba.wsilk.codegen.model.Type;

import lombok.Getter;

/**
 * 方法元数据
 * 
 * @author mindashuang
 */
public class MethodMeta extends AbstractElementMeta implements Comparable<PropertyMeta>, Meta {

	/** 返回值类型 */
	@Getter
	private final Type returnType;

	/** 参数类型 */
	@Getter
	private final Parameter[] parameters;

	/** 异常类型 */
	@Getter
	private final Type[] thrownType;

	public MethodMeta(EntityMeta entityMeta, String name, Type returnType, Parameter[] parameters, Type[] thrownType) {
		super(entityMeta, name);
		this.returnType = returnType;
		this.parameters = parameters;
		this.thrownType = thrownType;
	}

	@Override
	public int compareTo(PropertyMeta o) {
		return 0;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Arrays.hashCode(parameters);
		result = prime * result + Arrays.hashCode(thrownType);
		result = prime * result + Objects.hash(returnType);
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		MethodMeta other = (MethodMeta) obj;
		return Arrays.equals(parameters, other.parameters) && Objects.equals(returnType, other.returnType)
				&& Arrays.equals(thrownType, other.thrownType);
	}

}
