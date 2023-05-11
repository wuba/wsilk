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

import java.util.*;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.*;

import com.wuba.wsilk.core.process.ProcessDispatch;

/**
 * 处理语法树
 * 
 * @author mindashuang
 * 
 */
public abstract class AbstractAnnotationProcessor extends AbstractProcessor {

	@Override
	public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
		// 不覆盖运行
		if (roundEnv.processingOver() || annotations.size() == 0) {
			return false;
		}
		// 没有找到元素
		if (roundEnv.getRootElements() == null || roundEnv.getRootElements().isEmpty()) {
			return false;
		}
		// 配置
		WsilkConfiguration conf = new WsilkConfiguration(processingEnv, roundEnv);

		if (conf.canStart()) {
			conf.info("start processor");
			// 操作类
			ProcessDispatch processDispatch = new ProcessDispatch(conf);
			processDispatch.process();
		}
		return true;
	}

	@Override
	public SourceVersion getSupportedSourceVersion() {
		return SourceVersion.latestSupported();
	}

}
