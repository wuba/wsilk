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

import java.util.HashSet;
import java.util.Set;

/**
 * 类别
 * 
 * @author mindashuang
 * 
 */
public enum TypeCategory {
	/**
	 * 简单类型
	 */
	SIMPLE(null),
	/**
	 * map类型
	 *
	 */
	MAP(null),
	/**
	 * 集合类型
	 */
	COLLECTION(null),
	/**
	 * List 类型
	 */
	LIST(COLLECTION),
	/**
	 * set类型
	 */
	SET(COLLECTION),
	/**
	 * 数组类型
	 */
	ARRAY(null),
	/**
	 * 比较类型
	 */
	COMPARABLE(SIMPLE),
	/**
	 * bool类型
	 */
	BOOLEAN(COMPARABLE, Boolean.class.getName()),
	/**
	 * 日期类型
	 */
	DATE(COMPARABLE, java.sql.Date.class.getName(), "org.joda.time.LocalDate", "java.time.LocalDate"),
	/**
	 * 日期时间类型
	 */
	DATETIME(COMPARABLE, java.util.Calendar.class.getName(), java.util.Date.class.getName(),
			java.sql.Timestamp.class.getName(), "org.joda.time.LocalDateTime", "org.joda.time.Instant",
			"org.joda.time.DateTime", "org.joda.time.DateMidnight", "java.time.Instant", "java.time.LocalDateTime",
			"java.time.OffsetDateTime", "java.time.ZonedDateTime"),
	/**
	 * 时间类型
	 */
	TIME(COMPARABLE, java.sql.Time.class.getName(), "org.joda.time.LocalTime", "java.time.LocalTime",
			"java.time.OffsetTime"),
	/**
	 * 枚举类型
	 */
	ENUM(COMPARABLE),
	/**
	 * 自定义类型
	 */
	CUSTOM(null),

	/**
	 * 实体类型
	 */
	ENTITY(null),

	/**
	 * 数值类型
	 */
	NUMERIC(COMPARABLE),
	/**
	 * 字符串类型
	 */
	STRING(COMPARABLE, String.class.getName());

	private final TypeCategory superType;

	private final Set<String> types;

	TypeCategory(TypeCategory superType, String... types) {
		this.superType = superType;
		this.types = new HashSet<String>(types.length);
		for (String type : types) {
			this.types.add(type);
		}
	}

	public TypeCategory getSuperType() {
		return superType;
	}

	public boolean supports(Class<?> cl) {
		return supports(cl.getName());
	}

	public boolean supports(String className) {
		return types.contains(className);
	}

	/**
	 * 父类型是否这个分类
	 * 
	 * @param ancestor
	 * @return
	 */
	public boolean isSubCategoryOf(TypeCategory ancestor) {
		if (this == ancestor) {
			return true;
		} else if (superType == null) {
			return false;
		} else {
			return superType == ancestor || superType.isSubCategoryOf(ancestor);
		}
	}

	public static TypeCategory get(String className) {
		for (TypeCategory category : values()) {
			if (category.supports(className)) {
				return category;
			}
		}
		return SIMPLE;
	}

}
