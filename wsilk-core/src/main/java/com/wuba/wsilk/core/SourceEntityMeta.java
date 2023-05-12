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

package com.wuba.wsilk.core;

import javax.lang.model.element.Element;

import com.wuba.wsilk.codegen.EntityMeta;
import com.wuba.wsilk.codegen.model.Type;

import lombok.Getter;
import lombok.Setter;

/**
 * 
 * 获得源码的信息
 * 
 * @author mindashuang
 */
@Setter
@Getter
public class SourceEntityMeta extends EntityMeta implements Init {

	/** 原始类型 */
	private SourceEntityMeta original;

	/** 指定javaName */
	private String javaName;

	/**
	 * 顶级目录
	 */
	@Setter
	@Getter
	private boolean top = true;

	public SourceEntityMeta(Element element, Type type) {
		super(element, type);
	}

	/**
	 * 生成新的类型
	 */
	@Override
	public <T extends SourceEntityMeta> T init(Class<T> cls, Support support, String namespance) {
		T t = null;
		try {
			Type type = newType(support, namespance);
			t = (T) cls.getConstructor(Element.class, Type.class).newInstance(this.getElement(), type);
			t.setAnnotations(this.getAnnotations());
			t.setTop(top);
			t.setOriginal(this);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return t;
	}

	/**
	 * 重新初始化
	 */
	public void reInit() {
		this.javaName = null;
		this.original = null;
	}

	/**
	 * 获得javaName
	 */
	public String getJavaName(String suffix) {
		if (javaName == null) {
			String packageName = this.getPackageName();
			String fullName = this.getFullName();
			if (fullName.length() > packageName.length()) {
				javaName = this.getFullName().substring(packageName.length() + 1) + suffix;
			}
		}
		return javaName;
	}

}
