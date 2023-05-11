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

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.lang3.StringUtils;

import com.wuba.wsilk.core.AbstractConfigAble;
import com.wuba.wsilk.core.ConfigAble;
import com.wuba.wsilk.core.WsilkConfiguration;

/**
 * 
 * 一个简易的IOC
 * 
 * @author mindashuang
 */
public class InstanceUtils extends AbstractConfigAble implements ConfigAble {

	private final Map<String, Object> namedInstances = new HashMap<String, Object>();

	public InstanceUtils(WsilkConfiguration conf) {
		// 设置配置
		super(conf);
		// 设置配置
		namedInstances.put("conf", conf);

	}

	/**
	 * 加载对象
	 */

	@SuppressWarnings("unchecked")
	public <T> T createInstance(Class<?> implementation, Object... args) {
		try {
			Constructor<?> constructor = implementation.getConstructors()[0];
			return (T) constructor.newInstance(args);
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
				| SecurityException e) {
			e.printStackTrace();
		}
		throw new IllegalArgumentException("Got no  constructor for " + implementation.getName());
	}

	/**
	 * 加载对象
	 * 
	 */
	@SuppressWarnings("unchecked")
	public <T> T createInstance(Class<T> implementation) {
		Constructor<?> constructor = null;
		for (Constructor<?> c : implementation.getConstructors()) {
			if (c.isAnnotationPresent(Inject.class)) {
				constructor = c;
				break;
			}
		}
		// 默认构造器
		if (constructor == null) {
			try {
				constructor = implementation.getConstructor();
			} catch (SecurityException e) {
				throw new RuntimeException(e);
			} catch (NoSuchMethodException e) {
				throw new RuntimeException(e);
			}
		}
		if (constructor != null) {
			final Class<?>[] parameterType = constructor.getParameterTypes();
			int length = parameterType.length;
			Annotation[][] annotations = constructor.getParameterAnnotations();

			Object[] args = IntStream.range(0, length).mapToObj(i -> {
				Named named = (Named) Stream.of(annotations[i]).filter(e -> {
					return e instanceof Named;
				}).findFirst().get();

				return get(parameterType[i], named);
			}).toArray();

			try {
				T t = (T) constructor.newInstance(args);
				// 添加的mapper中
				namedInstances.put(StringUtils.uncapitalize(implementation.getSimpleName()), t);
				return t;
			} catch (InstantiationException e) {
				throw new RuntimeException(e);
			} catch (IllegalAccessException e) {
				throw new RuntimeException(e);
			} catch (InvocationTargetException e) {
				throw new RuntimeException(e);
			}
		} else {
			throw new IllegalArgumentException("Got no annotated constructor for " + implementation.getName());
		}
	}

	@SuppressWarnings("unchecked")
	public final <T> T get(Class<T> iface, Named named) {
		String name = named.value();
		if (namedInstances.containsKey(name)) {
			return (T) namedInstances.get(name);
		} else {
			T instance = (T) createInstance(iface);
			namedInstances.put(name, instance);
			return instance;
		}
	}

}
