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
 * 代码合并的接口
 * 
 * @author mindashuang
 */
public interface IMergeWriter<T extends MergeCode> {

	/**
	 * 导入就的代码
	 * 
	 * @throws IOException 导入失败
	 */
	public void mergeImport() throws IOException;

	/**
	 * 合并注解
	 * 
	 * @throws IOException 合并失败
	 */
	public void mergeAnnotation() throws IOException;

	/**
	 * 合并body内容
	 * 
	 * @throws IOException 合并失败
	 */
	public void mergeBody() throws IOException;

	/**
	 * 设置合并代码
	 * 
	 * @param mergeCode 需要合并的代码信息
	 * 
	 */
	public void setMergeCode(T mergeCode);

}
