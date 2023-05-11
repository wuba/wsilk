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

import java.lang.reflect.ParameterizedType;

import com.wuba.wsilk.core.NoGenericException;

/**
 * 获得泛型类型
 * 
 * @author mindashuang
 */
public class GenericUtils {

	/**
	 * 获取类上的注解类型
	 */
	@SuppressWarnings("unchecked")
	public static <T> Class<T> getGeneric(Class<?> cls) throws NoGenericException {
		java.lang.reflect.Type type = (java.lang.reflect.Type) cls.getGenericSuperclass();
		if (type instanceof ParameterizedType) {
			ParameterizedType pt = (ParameterizedType) type;
			return (Class<T>) pt.getActualTypeArguments()[0];
		} else if (type instanceof Class) {
			return getGeneric((Class<?>) type);
		}
		throw new NoGenericException("没有找到泛型类");
	}

}
