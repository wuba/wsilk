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

/**
 * 生成代码的异常
 * 
 * @author mindashuang
 * 
 */
public class CodegenException extends RuntimeException {

	private static final long serialVersionUID = -8704782349669898467L;

	public CodegenException(String msg) {
		super(msg);
	}

	public CodegenException(String msg, Throwable t) {
		super(msg, t);
	}

	public CodegenException(Throwable t) {
		super(t);
	}
}
