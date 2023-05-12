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

import java.io.Writer;

import com.google.googlejavaformat.java.Formatter;

/**
 * 代码格式化
 * 
 * @author mindashuang
 */
public class JavaFormatWriter extends AbstractFormatWriter {

	public JavaFormatWriter(Writer writer, boolean format) {
		super(writer, format);
	}

	static Formatter formatter = new Formatter();

	@Override
	public String format(String source) {
		try {
			source = formatter.formatSource(source);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return source;
	}

}
