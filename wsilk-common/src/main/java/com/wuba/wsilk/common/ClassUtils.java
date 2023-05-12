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

package com.wuba.wsilk.common;

import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;

import static com.wuba.wsilk.common.Symbols.*;

/**
 * 类工具
 * 
 * @author mindashuang
 */
public final class ClassUtils {

	private static final Set<String> JAVA_LANG = Collections.singleton("java.lang");

	public static String getName(Class<?> cl) {
		return getName(cl, JAVA_LANG, Collections.<String>emptySet());
	}

	public static String getFullName(Class<?> cl) {
		if (cl.isArray()) {
			return getFullName(cl.getComponentType()) + ARRAY;
		}
		final String name = cl.getName();
		if (name.indexOf(DOLLAR) > 0) {
			return getFullName(cl.getDeclaringClass()) + DOT + cl.getSimpleName();
		}
		return name;
	}

	public static String getPackageName(Class<?> cl) {
		while (cl.isArray()) {
			cl = cl.getComponentType();
		}
		final String name = cl.getName();
		final int i = name.lastIndexOf(DOT);
		if (i > 0) {
			return name.substring(0, i);
		} else {
			return StringUtils.EMPTY;
		}
	}

	public static String getName(Class<?> cl, Set<String> packages, Set<String> classes) {
		if (cl.isArray()) {
			return getName(cl.getComponentType(), packages, classes) + ARRAY;
		}
		final String canonicalName = cl.getName().replace(DOLLAR, DOT);
		final int i = cl.getName().lastIndexOf(DOT);
		if (classes.contains(canonicalName)) {
			return cl.getSimpleName();
		} else if (cl.getEnclosingClass() != null) {
			return getName(cl.getEnclosingClass(), packages, classes) + DOT + cl.getSimpleName();
		} else if (i == -1) {
			return canonicalName;
		} else if (packages.contains(canonicalName.substring(0, i))) {
			return canonicalName.substring(i + 1);
		} else {
			return canonicalName;
		}
	}

	public static Class<?> normalize(Class<?> clazz) {
		if (List.class.isAssignableFrom(clazz)) {
			return List.class;
		} else if (Set.class.isAssignableFrom(clazz)) {
			return Set.class;
		} else if (Collection.class.isAssignableFrom(clazz)) {
			return Collection.class;
		} else if (Map.class.isAssignableFrom(clazz)) {
			return Map.class;
			// check for CGLIB generated classes
		} else if (clazz.getName().contains(DOLLAR + DOLLAR)) {
			Class<?> zuper = clazz.getSuperclass();
			if (zuper != null && !Object.class.equals(zuper)) {
				return zuper;
			}
		} else if (!Modifier.isPublic(clazz.getModifiers())) {
			return normalize(clazz.getSuperclass());
		}
		return clazz;
	}

	private ClassUtils() {
	}

	public static Class<?> loaderClass(String className) {
		try {
			return Class.forName(className);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		return null;

	}

}
