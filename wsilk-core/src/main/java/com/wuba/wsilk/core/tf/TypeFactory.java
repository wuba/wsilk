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
package com.wuba.wsilk.core.tf;

import javax.lang.model.util.Elements;

import com.wuba.wsilk.core.AbstractConfigAble;
import com.wuba.wsilk.core.ConfigAble;
import com.wuba.wsilk.core.WsilkConfiguration;
import com.wuba.wsilk.core.adapter.WsilkProcessingEnvironment;

public abstract class TypeFactory extends AbstractConfigAble implements ConfigAble {

	protected final WsilkProcessingEnvironment processingEnv;

	protected final Elements elementUtils;

	protected final javax.lang.model.util.Types typeUtils;

	public TypeFactory(WsilkConfiguration configuration) {
		super(configuration);
		this.processingEnv = configuration.getProcessingEnv();
		elementUtils = processingEnv.getElementUtils();
		typeUtils = processingEnv.getTypeUtils();
	}

}
