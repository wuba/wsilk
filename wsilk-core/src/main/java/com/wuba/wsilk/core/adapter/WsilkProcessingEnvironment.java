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

package com.wuba.wsilk.core.adapter;

import java.lang.reflect.InvocationTargetException;
import java.util.Map;

import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;

import org.apache.commons.lang3.reflect.FieldUtils;
import org.apache.commons.lang3.reflect.MethodUtils;

public class WsilkProcessingEnvironment {

	private final ProcessingEnvironment processingEnvironment;

	private final static String GET_PROCESSOR_CLASSLOADER = "getProcessorClassLoader";

	private final static String OPTIONS = "options";

	public WsilkProcessingEnvironment(ProcessingEnvironment processingEnvironment) {
		this.processingEnvironment = processingEnvironment;
	}

	public Messager getMessager() {
		return processingEnvironment.getMessager();
	}

	public Elements getElementUtils() {
		return processingEnvironment.getElementUtils();
	}

	public Map<String, String> getOptions() {
		return processingEnvironment.getOptions();
	}

	@SuppressWarnings("unchecked")
	public Map<String, String> options(String key) {
		try {
			Object option = FieldUtils.readField(processingEnvironment, OPTIONS, true);
			if (option instanceof Map) {
				return (Map<String, String>) option;
			} else {
				return (Map<String, String>) FieldUtils.readField(option, "values", true);
			}
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		return null;
	}

	public Filer getFiler() {
		return processingEnvironment.getFiler();
	}

	public Types getTypeUtils() {
		return processingEnvironment.getTypeUtils();
	}

	public ClassLoader getProcessorClassLoader() {
		try {
			return (ClassLoader) MethodUtils.invokeMethod(processingEnvironment, GET_PROCESSOR_CLASSLOADER);
		} catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
			e.printStackTrace();
		}
		return processingEnvironment.getClass().getClassLoader();
	}

}
