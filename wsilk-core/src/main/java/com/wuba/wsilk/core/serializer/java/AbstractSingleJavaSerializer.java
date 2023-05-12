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

package com.wuba.wsilk.core.serializer.java;

import java.io.Writer;
import java.lang.annotation.Annotation;

import com.wuba.wsilk.codegen.JavaWriter;
import com.wuba.wsilk.core.SourceEntityMeta;
import com.wuba.wsilk.core.WsilkConfiguration;

/**
 * 写 单个 Java
 * 
 * @author mindashuang
 */
public abstract class AbstractSingleJavaSerializer<T extends SourceEntityMeta>
		extends AbstractJavaSerializer<T, JavaWriter> {

	public AbstractSingleJavaSerializer(WsilkConfiguration conf, Class<? extends Annotation> annClass) {
		super(conf, annClass);
	}

	public JavaWriter codeWriter(Writer w) {
		return new JavaWriter(w);
	}

}
