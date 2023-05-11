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
package com.wuba.wsilk.codegen.parser;

import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.google.common.collect.Lists;
import com.wuba.wsilk.codegen.Constants;
import com.wuba.wsilk.codegen.Tokens;
import com.wuba.wsilk.codegen.annoataion.AnnotationBean;
import com.wuba.wsilk.codegen.annoataion.AnnotationBeans;

import lombok.Getter;
import lombok.Setter;

/**
 * java 的部分信息
 * 
 * @author mindashuang
 */
@Getter
@Setter
public class JavaUnit {

	private List<String> classes = Lists.newArrayList();

	private List<String> packages = Lists.newArrayList();

	private List<String> statics = Lists.newArrayList();

	private AnnotationBeans annotationBeans;

	void addImport(String imp) {
		if (StringUtils.isNotEmpty(imp)) {
			/** 静态引入 */
			if (imp.startsWith(Tokens.STATIC)) {
				statics.add(imp.substring(7, imp.length() - 2));
			} else {
				if (imp.endsWith(Constants.DOT_STAR)) {
					packages.add(imp.substring(0, imp.length() - 2));
				} else {
					classes.add(imp);
				}
			}
		}
	}

	public void setImports(List<ImportLine> importLines) {
		for (ImportLine importLine : importLines) {
			this.addImport(importLine.toImport());
		}
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		for (String c : classes) {
			builder.append(c).append("\n");
		}
		for (String c : packages) {
			builder.append(c).append("\n");
		}
		for (String c : statics) {
			builder.append(c).append("\n");
		}
		for (AnnotationBean annotationBean : annotationBeans) {
			builder.append(annotationBean.toString()).append("\n");
		}
		return builder.toString();
	}

}
