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

package com.wuba.wsilk.codegen;

import java.io.IOException;

/**
 * 组合
 * 
 * @author mindashuang
 */
public interface CompositeWriter<T extends AbstractCodeWriter<T>> extends CodeWriter<T> {

	/**
	 * 是否可以开启一个方法
	 * 
	 * @param method 方法名
	 * @return 是否可以开启一个方法
	 */
	boolean isStart(String method);

	/**
	 * 开始一个方法分支
	 * 
	 * @param method 方法名
	 */
	void startAppend(String method);

	/**
	 * 结束一个方法分支
	 * 
	 * @param method 方法名
	 */
	void endAppend(String method);

	/**
	 * 合并
	 * 
	 * @throws IOException 合并失败
	 */
	void merger() throws IOException;

}
