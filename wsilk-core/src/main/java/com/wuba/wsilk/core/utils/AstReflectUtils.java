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

package com.wuba.wsilk.core.utils;

import javax.tools.JavaFileObject;

import java.io.InputStream;
import java.net.URI;
import java.lang.reflect.InvocationTargetException;

import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;

import org.apache.commons.lang3.reflect.FieldUtils;
import org.apache.commons.lang3.reflect.MethodUtils;

public class AstReflectUtils {

	private final static String GET_ANNOTATION_TYPE = "getAnnotationType";

	public final static AnnotationMirror getAnnotationType(Object entity) {
		return reflectMethod(entity, GET_ANNOTATION_TYPE);
	}

	private final static String VALUES = "values";

	public static Object[] values(Object entity) {
		return reflectMethod(entity, VALUES);
	}

	/**
	 * 获得 Element 路径
	 * 
	 * @throws InvocationTargetException
	 * @throws IllegalAccessException
	 * @throws NoSuchMethodException
	 */
	public static String getPath(Element element) {
		JavaFileObject sourcefile = (JavaFileObject) sourcefile(element);
		URI uri = sourcefile.toUri();
		String path = uri.getPath();
		return path == null ? null : path;
	}

	private final static String OPEN_INPUTSTREAM = "openInputStream";

	private final static String PACKAGE_INFO = "package_info";

	/**
	 * 获得inputStream
	 * 
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 * @throws NoSuchMethodException
	 */
	public static InputStream openInputStream(Element element)
			throws IllegalAccessException, NoSuchMethodException, InvocationTargetException {
		return reflectMethod(sourcefile(element), OPEN_INPUTSTREAM);
	}

	private final static String SOURCE_FILE = "sourcefile";

	public static Object sourcefile(Element element) {
		return reflectField(element.getKind() == ElementKind.PACKAGE ? reflectField(element, PACKAGE_INFO) : element,
				SOURCE_FILE);
	}

	private final static String AS_ELEMENT = "asElement";

	public static Element asElement(DeclaredType declaredType) {
		if (declaredType.getKind() == TypeKind.DECLARED || declaredType.getKind() == TypeKind.PACKAGE) {
			return asElement((Object) declaredType);
		}
		return null;
	}

	public static Element asElement(Object entity) {
		return reflectMethod(entity, AS_ELEMENT);
	}

	public static String name2(Object entity) {
		return String.valueOf(reflectField(entity, NAME));
	}

	private final static String GET_UPPERBOUND = "getUpperBound";

	public static TypeMirror getUpperBound(TypeMirror typeMirror) {
		return reflectMethod(typeMirror, GET_UPPERBOUND);
	}

	private final static String GET_VALUE = "getValue";

	public static Object getValue(Object entity) {
		return reflectMethod(entity, GET_VALUE);
	}

	public static String name(Object entity) {
		return String.valueOf(reflectField(entity, NAME));
	}

	private final static String FST = "fst";
	private final static String NAME = "name";

	public static String fst(Object entity) {
		return name(reflectField(entity, FST));
	}

	private final static String SND = "snd";

	public static Object snd(Object entity) throws IllegalAccessException {
		return reflectField(entity, SND);
	}

	@SuppressWarnings("unchecked")
	private final static <T> T reflectMethod(Object entity, String name) {
		try {
			if (entity != null) {
				return (T) MethodUtils.invokeMethod(entity, name);
			}
		} catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
			e.printStackTrace();
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	private final static <T> T reflectField(Object entity, String name) {
		try {
			if (entity != null) {
				return (T) FieldUtils.readField(entity, name, true);
			}
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		return null;
	}

}
