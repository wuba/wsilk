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
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Stack;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

/**
 * 组合
 * 
 * @author mindashuang
 */
public class CompositeAppendable implements Appendable {

	/** 主要输出对象 */
	private final Appendable main;

	private StringBuilder current;

	private List<StringBuilder> appendables = Lists.newArrayList();

	private final Stack<StringBuilder> stack = new Stack<StringBuilder>();

	private Set<String> methodstack = Sets.newHashSet();

	private boolean meger = false;

	public CompositeAppendable(Appendable appendable) {
		this.main = appendable;
		newAppend();
	}

	@Override
	public Appendable append(CharSequence csq) throws IOException {
		return getCurrent().append(csq);
	}

	@Override
	public Appendable append(CharSequence csq, int start, int end) throws IOException {
		return getCurrent().append(csq, start, end);
	}

	@Override
	public Appendable append(char c) throws IOException {
		return getCurrent().append(c);
	}

	public Appendable getCurrent() {
		return meger ? main : current;
	}

	public boolean isStart(String method) {
		return !methodstack.contains(method);
	}

	/** 启动另外一个 */

	public void startAppend(String method) {
		if (methodstack.add(method)) {
			stack.push(current);
			newAppend();
		}
	}

	public void newAppend() {
		StringBuilder append = new StringBuilder();
		appendables.add(append);
		this.current = append;
	}

	/** 结束另外一个 */
	public void endAppend(String method) {
		methodstack.remove(method);
		this.current = stack.pop();
	}

	/** 合并 */
	public void merger() throws IOException {
		Iterator<StringBuilder> iterator = appendables.iterator();
		while (iterator.hasNext()) {
			main.append(iterator.next());
		}
		meger = true;
	}
}
