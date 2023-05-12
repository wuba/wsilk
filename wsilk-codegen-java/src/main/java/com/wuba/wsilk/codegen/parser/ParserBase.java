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
package com.wuba.wsilk.codegen.parser;

import java.util.ArrayList;
import java.util.List;

import com.wuba.wsilk.codegen.annoataion.AnnotationBeans;

/**
 * 解析基础类
 * 
 * @author mindashuang
 */
public class ParserBase {

	/**
	 * 添加
	 */
	protected <T> List<T> add(List<T> list, T obj) {
		if (list == null) {
			list = emptyList();
		}
		list.add(obj);
		return list;
	}

	protected <T> List<T> emptyList() {
		return new ArrayList<>();
	}

	protected AnnotationBeans annotationBeans() {
		return new AnnotationBeans();
	}

}
