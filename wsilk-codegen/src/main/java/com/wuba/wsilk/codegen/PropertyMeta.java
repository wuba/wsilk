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

import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.google.common.base.Objects;
import com.wuba.wsilk.codegen.model.Type;

/**
 * 字段元数据
 * 
 * @author mindashuang
 */
public class PropertyMeta extends AbstractElementMeta implements Comparable<PropertyMeta>, Meta {

	private final Type type;

	private List<String> inits = Collections.<String>emptyList();

	private boolean inherited = false;

	/** 判断是否是主键 */
	private boolean primaryKey;

	public PropertyMeta(EntityMeta entityMeta, String name, Type type) {
		super(entityMeta, name);
		this.type = type;
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(getEntityMeta().getFullName(), getName(), type);
	}

	@Override
	public boolean equals(Object o) {
		if (o == this) {
			return true;
		} else if (o instanceof PropertyMeta) {
			PropertyMeta p = (PropertyMeta) o;
			return p.getEntityMeta().getFullName().equals(getEntityMeta().getFullName())
					&& p.getName().equals(getName()) && p.type.equals(type);
		} else {
			return false;
		}
	}

	@Override
	public int compareTo(PropertyMeta o) {
		int rv = getName().compareToIgnoreCase(o.getName());
		if (rv == 0) {
			return getName().compareTo(o.getName());
		} else {
			return rv;
		}
	}

	public Type getType() {
		return type;
	}

	public boolean isInherited() {
		return inherited;
	}

	public List<String> getInits() {
		return inits;
	}

	public Type getParameter(int i) {
		return type.getParameters().get(i);
	}

	public boolean primaryKey() {
		return primaryKey;
	}

	private String id = "javax.persistence.Id";

	public void setAnnotations(Map<String, ? extends AnnotationMapValue> annotations) {
		super.setAnnotations(annotations);
		if (annotations.containsKey(id)) {
			primaryKey = true;
		}
	}

	public String getStaticName() {
		return getEscapedName().toUpperCase();
	}

	@Override
	public String toString() {
		return "PropertyMeta [name=" + getName() + "]";
	}

	public boolean isEnum() {
		return this.getType().getJavaClass().isEnum();
	}

}
