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
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.google.common.collect.Maps;

import lombok.Getter;

/**
 * 选项影射
 * 
 * @author mindashuang
 */
public class OptionsMapper extends AbstractConfigAble {

	public OptionsMapper(WsilkConfiguration conf) {
		super(conf);
	}

	/**
	 * 项目名称
	 * 
	 */
	@Getter
	private String projectName;

	/**
	 * 选项
	 * 
	 */
	private final Map<String, String> options = Maps.newHashMap();

	public File getProjectPath() {
		String projectPath = getOption("projectPath");
		if (projectPath != null) {
			File file = new File(projectPath);
			projectName = file.getName();
			return file;
		}
		return null;
	}

	/**
	 * 注册type
	 */
	public void regOption(String key, String value) {
		options.put(key, value);
	}

	public static final String PLACEHOLDER_PREFIX = "${";

	public static final String PLACEHOLDER_SUFFIX = "}";

	public String getOption(String type) {
		String path = options.get(type);
		if (path != null && path.indexOf(PLACEHOLDER_PREFIX) >= 0) {
			int dollar = 0;
			while ((dollar = path.indexOf(PLACEHOLDER_PREFIX, dollar)) >= 0) {
				int end = path.indexOf(PLACEHOLDER_SUFFIX, dollar);
				if (end > 0) {
					String key = path.substring(dollar + 2, end);
					String v = getOption(key);
					if (v != null) {
						path = StringUtils.replace(path, PLACEHOLDER_PREFIX + key + PLACEHOLDER_SUFFIX, v);
					} else {
						throw new MavenConfException("can not find " + key);
					}
				} else {
					throw new MavenConfException("can not find " + PLACEHOLDER_SUFFIX);
				}
			}
		}
		return path;
	}

}