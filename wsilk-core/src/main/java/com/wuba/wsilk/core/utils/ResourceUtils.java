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
package com.wuba.wsilk.core.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.Charset;
import java.util.Properties;

/**
 * 
 * 读取本地配置
 * 
 * @author mindashuang
 */
public class ResourceUtils {

	public final static Properties getProperties(String resourceName) {
		return getProperties(resourceName, null);
	}

	public final static Properties getProperties(String resourceName, ClassLoader classLoader) {
		Properties props = new Properties();
		try {
			InputStream inputStream = getResource(resourceName, classLoader);
			if (inputStream != null) {
				props.load(inputStream);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return props;
	}

	public final static InputStream getResource(String fileName) {
		return getResource(fileName, null);
	}

	public final static InputStream getResource(String fileName, ClassLoader classLoader) {
		InputStream inputStream = null;
		try {
			File file = new File(fileName);
			if (!file.isAbsolute()) {
				file = new File(System.getProperty("user.dir"), fileName);
			}
			inputStream = new FileInputStream(file);
		} catch (Exception e) {
			if (inputStream != null) {
				try {
					inputStream.close();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		}
		if (inputStream == null) {
			if (classLoader == null) {
				classLoader = Thread.currentThread().getContextClassLoader();
			}
			try {
				inputStream = classLoader.getResourceAsStream(fileName);
			} catch (Exception e) {
				;
			}
		}
		return inputStream;
	}

	public final static String getPath(String fileName) throws UnsupportedEncodingException {
		String path = null;
		ClassLoader loader = Thread.currentThread().getContextClassLoader();
		if (path == null) {
			try {
				path = loader.getResource(fileName).getPath();
			} catch (Exception e) {
				;
			}
		}
		if (path == null) {
			try {
				path = ResourceUtils.class.getResource(fileName).getPath();
			} catch (Exception e) {
				;
			}
		}
		return path != null ? URLDecoder.decode(path, Charset.defaultCharset().toString()) : path;
	}

}
