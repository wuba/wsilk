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
 * 写代码
 * 
 * @author mindashuang
 */

public interface CodeWriter<T extends CodeWriter<T>> extends Appendable {

	/**
	 * 启动一行代码
	 * 
	 * @param segments 代码片段
	 * 
	 * @return 代码输出器
	 * @throws IOException 输出失败
	 */
	public T beginLine(String... segments) throws IOException;

	/**
	 * 写一行代码
	 * 
	 * @param segments 代码片段
	 * 
	 * @return 代码输出器
	 * @throws IOException 输出失败
	 */
	public T line(String... segments) throws IOException;

	/**
	 * 代码换行
	 * 
	 * @return 代码输出器
	 * @throws IOException 输出失败
	 */
	public T nl() throws IOException;

	/**
	 * 添加代码排班的空格
	 * 
	 * @return 代码输出器
	 * @throws IOException 输出失败
	 */
	public T indent() throws IOException;

	/**
	 * 代码输出器
	 * 
	 * @return 代码输出器
	 */
	public Appendable getAppendable();

}
