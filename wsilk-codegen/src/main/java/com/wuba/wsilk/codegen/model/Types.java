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

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.net.URI;

/**
 * 
 * 常用类型
 * 
 * @author mindashuang
 */

public final class Types {

	public static final Map<Type, Type> WRAPS;

	public static final Map<Type, Type> PRIMITIVES;

	public static final ClassType OBJECT = new ClassType(TypeCategory.SIMPLE, Object.class);

	public static final ClassType OBJECTS = new ClassType(TypeCategory.ARRAY, Object[].class);

	public static final ClassType BIG_DECIMAL = new ClassType(TypeCategory.NUMERIC, BigDecimal.class);

	public static final ClassType BIG_INTEGER = new ClassType(TypeCategory.NUMERIC, BigInteger.class);

	public static final ClassType BOOLEAN = new ClassType(TypeCategory.BOOLEAN, Boolean.class);

	public static final ClassType BOOLEAN_P = new ClassType(TypeCategory.BOOLEAN, boolean.class);

	public static final ClassType BYTE = new ClassType(TypeCategory.NUMERIC, Byte.class);

	public static final ClassType BYTE_P = new ClassType(TypeCategory.NUMERIC, byte.class);

	public static final ClassType CHARACTER = new ClassType(TypeCategory.COMPARABLE, Character.class);

	public static final ClassType CHAR = new ClassType(TypeCategory.COMPARABLE, char.class);

	public static final ClassType COLLECTION = new ClassType(TypeCategory.COLLECTION, Collection.class, OBJECT);

	public static final ClassType DOUBLE = new ClassType(TypeCategory.NUMERIC, Double.class);

	public static final ClassType DOUBLE_P = new ClassType(TypeCategory.NUMERIC, double.class);

	public static final ClassType FLOAT = new ClassType(TypeCategory.NUMERIC, Float.class);

	public static final ClassType FLOAT_P = new ClassType(TypeCategory.NUMERIC, float.class);

	public static final ClassType INTEGER = new ClassType(TypeCategory.NUMERIC, Integer.class);

	public static final ClassType INT = new ClassType(TypeCategory.NUMERIC, int.class);

	public static final ClassType ITERABLE = new ClassType(TypeCategory.SIMPLE, Iterable.class, OBJECT);

	public static final ClassType LIST = new ClassType(TypeCategory.LIST, List.class, OBJECT);

	public static final ClassType LOCALE = new ClassType(TypeCategory.SIMPLE, Locale.class);

	public static final ClassType LONG = new ClassType(TypeCategory.NUMERIC, Long.class);

	public static final ClassType LONG_P = new ClassType(TypeCategory.NUMERIC, long.class);

	public static final ClassType MAP = new ClassType(TypeCategory.MAP, Map.class, OBJECT, OBJECT);

	public static final ClassType SET = new ClassType(TypeCategory.SET, Set.class, OBJECT);

	public static final ClassType SHORT = new ClassType(TypeCategory.NUMERIC, Short.class);

	public static final ClassType SHORT_P = new ClassType(TypeCategory.NUMERIC, short.class);

	public static final ClassType STRING = new ClassType(TypeCategory.STRING, String.class);

	public static final ClassType URI = new ClassType(TypeCategory.COMPARABLE, URI.class);

	public static final ClassType VOID = new ClassType(TypeCategory.SIMPLE, void.class);

	public static final SimpleType DATE_TIME = new SimpleType(TypeCategory.DATETIME, "org.joda.time.DateTime",
			"org.joda.time", "DateTime", false, false);

	public static final SimpleType LOCAL_DATE = new SimpleType(TypeCategory.DATE, "org.joda.time.LocalDate",
			"org.joda.time", "LocalDate", false, false);

	public static final SimpleType LOCAL_TIME = new SimpleType(TypeCategory.TIME, "org.joda.time.LocalTime",
			"org.joda.time", "LocalTime", false, false);

	public static final ClassType ENUM = new ClassType(TypeCategory.SIMPLE, Enum.class);

	static {
		Map<Type, Type> primitives = new HashMap<Type, Type>();
		primitives.put(BOOLEAN, BOOLEAN_P);
		primitives.put(BYTE, BYTE_P);
		primitives.put(CHARACTER, CHAR);
		primitives.put(DOUBLE, DOUBLE_P);
		primitives.put(FLOAT, FLOAT_P);
		primitives.put(INTEGER, INT);
		primitives.put(LONG, LONG_P);
		primitives.put(SHORT, SHORT_P);
		PRIMITIVES = Collections.unmodifiableMap(primitives);

		Map<Type, Type> wraps = new HashMap<Type, Type>();
		wraps.put(Types.BOOLEAN_P, Types.BOOLEAN);
		wraps.put(Types.BYTE_P, Types.BYTE);
		wraps.put(Types.CHAR, Types.CHARACTER);
		wraps.put(Types.DOUBLE_P, Types.DOUBLE);
		wraps.put(Types.FLOAT_P, Types.FLOAT);
		wraps.put(Types.INT, Types.INTEGER);
		wraps.put(Types.LONG_P, Types.LONG);
		wraps.put(Types.SHORT_P, Types.SHORT);

		WRAPS = Collections.unmodifiableMap(wraps);

	}

	private Types() {
	}

}
