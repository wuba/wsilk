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

/**
 * 配置信息
 * 
 * @author mindashuang
 */
public abstract class AbstractConfigAble implements ConfigAble {

	private WsilkConfiguration conf;

	public AbstractConfigAble(WsilkConfiguration conf) {
		this.conf = conf;
	}

	@Override
	public WsilkConfiguration getConfiguration() {
		return conf;
	}

	public void error(String log) {
		getConfiguration().error(log);
	}

	public void info(String log) {
		getConfiguration().info(log);
	}

	public void warning(String log) {
		getConfiguration().warning(log);
	}
}
