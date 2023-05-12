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
import java.io.StringWriter;
import java.io.Writer;

/**
 * 格式化接口
 * 
 * @author mindashuang
 */
public abstract class AbstractFormatWriter extends Writer {

	private final Writer writer;

	private StringWriter stringWriter = new StringWriter();

	private final boolean format;

	public AbstractFormatWriter(Writer writer, boolean format) {
		this.writer = writer;
		this.format = format;
	}

	@Override
	public void write(char[] cbuf, int off, int len) throws IOException {
		stringWriter.write(cbuf, off, len);
	}

	@Override
	public void flush() throws IOException {
		stringWriter.flush();
		String soruce = null;
		soruce = stringWriter.toString();
		if (format) {
			soruce = format(soruce);
		}
		writer.write(soruce);
		writer.flush();
	}

	/**
	 * 代码格式化
	 * 
	 * @param source 原始代码
	 * @return 格式化好的代码
	 * 
	 */
	public abstract String format(String source);

	@Override
	public void close() throws IOException {
		// 新代码
		writer.close();
	}

}
