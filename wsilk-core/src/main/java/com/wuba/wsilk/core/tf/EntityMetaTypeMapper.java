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

package com.wuba.wsilk.core.tf;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import javax.lang.model.element.Element;

import com.wuba.wsilk.codegen.EntityMeta;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * 生成的对象的缓存及生成逻辑
 * 
 * @author mindashuang
 */
public class EntityMetaTypeMapper {

	private final Map<Key, EntityMeta> entityTypeCache = new HashMap<Key, EntityMeta>();

	private EntityMetaTypeMapper() {
	}

	private final static EntityMetaTypeMapper INSTANCE = new EntityMetaTypeMapper();

	public final static EntityMetaTypeMapper getInstance() {
		return INSTANCE;
	}

	/**
	 * 获得
	 */
	@SuppressWarnings("unchecked")
	public <T extends EntityMeta> T getType(Element element, Class<?> cls, Function<Element, List<String>> nameFunction,
			Function<Element, T> typeFunction) {
		// 拿到类的key
		List<String> names = nameFunction.apply(element);
		Key key = new Key(names, cls);
		T entityMeta = (T) entityTypeCache.get(key);
		if (entityMeta == null) {
			entityMeta = typeFunction.apply(element);
			entityTypeCache.put(key, entityMeta);
		}
		return entityMeta;
	}

	@AllArgsConstructor
	@Getter
	@Setter
	public static class Key {

		private List<String> names;

		private Class<?> cls;

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((cls == null) ? 0 : cls.hashCode());
			result = prime * result + ((names == null) ? 0 : names.hashCode());
			return result;
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
			Key other = (Key) obj;
			if (cls == null) {
				if (other.cls != null) {
					return false;
				}
			} else if (!cls.equals(other.cls)) {
				return false;
			}
			if (names == null) {
				if (other.names != null) {
					return false;
				}
			} else if (!names.equals(other.names)) {
				return false;
			}
			return true;
		}

	}

}
