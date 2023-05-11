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

import java.util.Set;

import javax.lang.model.element.Element;

import com.wuba.wsilk.core.process.ConfigProcess;

/**
 * 
 * 进程
 * 
 * @author mindashuang
 * 
 */
public interface Process extends ConfigProcess {

	/**
	 * 处理所有相关的元素
	 * 
	 * @param elements 处理的元素
	 * 
	 */
	public <E extends Element> void process(Set<E> elements);

}
