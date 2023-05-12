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
package com.wuba.wsilk.codegen.annoataion;

import java.util.List;
import java.util.Objects;

import com.google.common.collect.Lists;
import com.wuba.wsilk.codegen.Pair;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 注解相关信息
 * 
 * @author mindashuang
 */

@Getter
@Setter
@ToString
public class AnnotationBean {

	/** 注解类型 */
	private String typeName;

	/** 注解的值 */
	private List<Pair<?>> pairs;

	public AnnotationBean(String typeName) {
		this.typeName = typeName;
	}

	public AnnotationBean(String typeName, Object value) {
		this(typeName);
		pairs = Lists.newArrayList();
		pairs.add(Pair.createPair(null, value));
	}

	public AnnotationBean(String typeName, List<Pair<?>> pairs) {
		this(typeName);
		this.pairs = pairs;
	}

	public String getTypeName() {
		return typeName;
	}

	@Override
	public int hashCode() {
		return Objects.hash(pairs, typeName);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		AnnotationBean other = (AnnotationBean) obj;
		return Objects.equals(pairs, other.pairs) && Objects.equals(typeName, other.typeName);
	}

}
