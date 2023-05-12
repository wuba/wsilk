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

package com.wuba.wsilk.core.process;

import java.lang.annotation.Annotation;
import java.util.Set;

import javax.lang.model.element.Element;

import com.wuba.wsilk.core.ConfigAble;

/**
 * 处理元素类
 * 
 * @author mindashuang
 */
public interface ConfigProcess extends ConfigAble {

	/**
	 * 初始化
	 * 
	 * @param elements 所有处理的元素
	 */
	public <E extends Element> void init(Set<E> elements);

	/**
	 * 加载所有子类
	 * 
	 * @return 返回注解
	 */
	public Class<? extends Annotation> getAnnotation();

	/**
	 * 顺序
	 * 
	 * @return 返回排序
	 */
	default int order() {
		return 100;
	}

}
