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

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;

import org.apache.commons.lang3.StringUtils;

import com.google.common.collect.Sets;
import com.wuba.wsilk.codegen.model.Constructor;
import com.wuba.wsilk.codegen.model.Supertype;
import com.wuba.wsilk.codegen.model.Type;
import com.wuba.wsilk.codegen.model.TypeAdapter;
import com.wuba.wsilk.codegen.model.TypeCategory;

import lombok.Getter;
import lombok.Setter;

/**
 * 基础元数据
 * 
 * @author mindashuang
 */
public class EntityMeta extends TypeAdapter implements Comparable<EntityMeta>, Meta {

	/**
	 * 注解信息
	 */
	@Getter
	@Setter
	private Map<String, ? extends AnnotationMapValue> annotations = new HashMap<>();

	private final Set<String> propertyNames = new HashSet<String>();

	private final Set<String> methodNames = new HashSet<String>();

	private final Set<String> escapedPropertyNames = new HashSet<String>();

	private final Set<String> escapedMethodNames = new HashSet<String>();

	private String modifiedSimpleName;

	private int escapeSuffix = 1;

	/**
	 * 父类
	 */
	@Getter
	private Set<Supertype> superTypes;

	/**
	 * 接口
	 */
	@Getter
	private Set<Supertype> interfaceTypes;

	/**
	 * 元素
	 */
	@Setter
	@Getter
	private Element element;

	/**
	 * 属性
	 */
	@Getter
	private final LinkedHashSet<PropertyMeta> properties = new LinkedHashSet<PropertyMeta>();

	/**
	 * 数据
	 */
	@Getter
	private final Map<Object, Object> data = new HashMap<Object, Object>();

	/**
	 * 构造器
	 */
	private final Set<Constructor> constructors = new HashSet<Constructor>();

	@Getter
	private final LinkedHashSet<MethodMeta> methodMetas = new LinkedHashSet<MethodMeta>();

	/**
	 * 获得所有Method
	 */
	public Set<MethodMeta> getAllMethod() {
		// 获得所有method meta
		return methodMetas;
	}

	public EntityMeta(Element element, Type type) {
		super(type);
		this.element = element;
	}

	/**
	 * 拿到所有属性
	 */
	public Set<PropertyMeta> getAllProperties() {
		LinkedHashSet<PropertyMeta> all = new LinkedHashSet<PropertyMeta>();
		all.addAll(properties);
		/** 父类在前面 */
		if (superTypes != null) {
			superTypes.stream().forEach(e -> {
				all.addAll(e.getEntityMeta().getAllProperties());
			});
		}
		return all;
	}

	public PropertyMeta findProperties(String name) {
		for (PropertyMeta propertyMeta : properties) {
			if (propertyMeta.getName().equals(name)) {
				return propertyMeta;
			}
		}
		return null;
	}

	public void addProperty(PropertyMeta field) {
		if (!propertyNames.contains(field.getName())) {
			propertyNames.add(field.getName());
			escapedPropertyNames.add(field.getEscapedName());
			properties.add(validateField(field));
		}
	}

	public void addMethod(MethodMeta methodMeta) {
		if (!methodNames.contains(methodMeta.getName())) {
			methodNames.add(methodMeta.getName());
			escapedMethodNames.add(methodMeta.getEscapedName());
			methodMetas.add(validateMethod(methodMeta));
		}
	}

	public void addAllPropertys(LinkedHashSet<PropertyMeta> fields) {
		properties.addAll(fields);
	}

	public void addSuperType(Type type, EntityMeta entityMeta) {
		if (superTypes == null) {
			superTypes = Sets.newHashSet();
		}
		Supertype supertype = new Supertype();
		supertype.setType(type);
		supertype.setEntityMeta(entityMeta);
		superTypes.add(supertype);
	}

	public String getRoot() {
		return getPackageName();
	}

	/**
	 * 添加接口
	 */
	public void addInterfaceType(Type type, EntityMeta entityMeta) {
		if (interfaceTypes == null) {
			interfaceTypes = Sets.newHashSet();
		}
		Supertype supertype = new Supertype();
		supertype.setType(type);
		supertype.setEntityMeta(entityMeta);
		interfaceTypes.add(supertype);
	}

	private PropertyMeta validateField(PropertyMeta field) {
		if (field.getName().equals(modifiedSimpleName) || field.getEscapedName().equals(modifiedSimpleName)) {
			do {
				modifiedSimpleName = StringUtils.uncapitalize(getType().getSimpleName()) + (escapeSuffix++);
			} while (propertyNames.contains(modifiedSimpleName));
		}
		return field;
	}

	private MethodMeta validateMethod(MethodMeta method) {
		if (method.getName().equals(modifiedSimpleName) || method.getEscapedName().equals(modifiedSimpleName)) {
			do {
				modifiedSimpleName = StringUtils.uncapitalize(getType().getSimpleName()) + (escapeSuffix++);
			} while (propertyNames.contains(modifiedSimpleName));
		}
		return method;
	}

	@Override
	public int compareTo(EntityMeta o) {
		return getType().getFullName().compareTo(o.getType().getFullName());
	}

	@Override
	public int hashCode() {
		return getFullName().hashCode();
	}

	@Override
	public boolean equals(Object o) {
		if (o == this) {
			return true;
		} else if (o instanceof Type) {
			return getFullName().equals(((Type) o).getFullName());
		} else {
			return false;
		}
	}

	public TypeElement getTypeElement() {
		return (TypeElement) element;
	}

	/**
	 * 
	 * 判断是不是接口
	 * 
	 */
	public boolean isInterface() {
		return getTypeElement().getKind().isInterface();
	}

	public void addConstructor(Constructor co) {
		constructors.add(co);
	}

	public Set<Constructor> getConstructors() {
		return constructors;
	}

	public TypeCategory getOriginalCategory() {
		return super.getCategory();
	}

	/**
	 * 是否是实体
	 * 
	 */
	public boolean hasEntityFields() {
		return hasPropertyWithType(TypeCategory.ENTITY);
	}

	public Supertype getSuperType() {
		return superTypes != null && superTypes.size() == 1 ? superTypes.iterator().next() : null;
	}

	/**
	 * 判断类型
	 */
	private boolean hasPropertyWithType(TypeCategory category) {
		for (PropertyMeta property : properties) {
			if (property.getType().getCategory() == category) {
				return true;
			}
		}
		return false;
	}

}
