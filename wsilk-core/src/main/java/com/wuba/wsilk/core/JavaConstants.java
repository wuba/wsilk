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

import java.io.File;
import java.nio.charset.Charset;

import com.google.common.base.Joiner;

/**
 * 
 * java maven 项目的相关配置
 * 
 * @author mindashuang
 */
public class JavaConstants {

	public final static String SRC_MAIN_RESOURCES = getSrcMainResources();

	public final static String SRC_MAIN_JAVA = getSrcMainJava();

	public final static String SCR_TEST_JAVA = getSrcTestJava();

	public final static String SRC_TEST_RESOURCES = getSrcTestResources();

	public final static String SCR_WEB_APP = getWebapp();

	private final static String SRC = "src";
	private final static String MAIN = "main";
	private final static String JAVA = "java";
	private final static String TEST = "test";
	private final static String RESOURCE = "resources";
	private final static String WEBAPP = "webapp";

	/** 字符集 */
	public final static Charset CHARSET = Charset.defaultCharset();

	public static String getSrcMainJava() {
		return path(SRC, MAIN, JAVA);
	}

	public static String getSrcTestJava() {
		return path(SRC, TEST, JAVA);
	}

	public static String getSrcMainResources() {
		return path(SRC, MAIN, RESOURCE);
	}

	public static String getSrcTestResources() {
		return path(SRC, TEST, RESOURCE);
	}

	public static String getWebapp() {
		return path(SRC, WEBAPP);
	}

	public static String path(String... g) {
		String s = File.separator;
		return s + Joiner.on(s).join(g) + s;
	}
}
