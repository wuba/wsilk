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
import java.io.Writer;

/**
 * 
 * 解决方法嵌套的问题
 * 
 * @author mindashuang
 */
public class CompositeJavaWriter extends JavaWriter implements CompositeWriter<JavaWriter> {

	public CompositeJavaWriter(CompositeAppendable appendable) {
		super(appendable);
	}

	public CompositeJavaWriter(Writer w) {
		super(new CompositeAppendable(w));
	}

	@Override
	public boolean isStart(String method) {
		return getAppendable().isStart(method);
	}

	@Override
	public void startAppend(String method) {
		getAppendable().startAppend(method);
	}

	@Override
	public void endAppend(String method) {
		getAppendable().endAppend(method);
	}

	@Override
	public void merger() throws IOException {
		getAppendable().merger();
	}

	@Override
	public CompositeAppendable getAppendable() {
		return (CompositeAppendable) super.getAppendable();
	}

}
