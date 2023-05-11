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

import java.util.Objects;

import com.wuba.wsilk.codegen.EntityMeta;

import lombok.Getter;
import lombok.Setter;

/**
 * 父类类型
 * 
 * @author mindashuang
 */
@Getter
@Setter
public class Supertype {

	private EntityMeta entityMeta;

	private Type type;

	@Override
	public int hashCode() {
		return Objects.hash(entityMeta, type);
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
		Supertype other = (Supertype) obj;
		return Objects.equals(entityMeta, other.entityMeta) && Objects.equals(type, other.type);
	}

}
