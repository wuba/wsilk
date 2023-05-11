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

import java.io.IOException;
import java.io.Writer;
import java.lang.annotation.Annotation;

import com.wuba.wsilk.codegen.CompositeJavaWriter;
import com.wuba.wsilk.core.SourceEntityMeta;
import com.wuba.wsilk.core.WsilkConfiguration;

/**
 * 复合型 输出，主要是解决输出多个方法的情况，我们在输出的时候，存在输出方法的切换，加入A
 * 方法调用B,我们输出了A调用B的代码后，然后完成B的输出后，我们会回来接着输出A
 * 
 * @author mindashuang
 */
public abstract class AbstractJavaSerializerComposite<T extends SourceEntityMeta>
		extends AbstractJavaSerializer<T, CompositeJavaWriter> {

	public AbstractJavaSerializerComposite(WsilkConfiguration conf, Class<? extends Annotation> annClass) {
		super(conf, annClass);
	}

	@Override
	public CompositeJavaWriter codeWriter(Writer w) {
		return new CompositeJavaWriter(w);
	}

	@Override
	public void end(CompositeJavaWriter w, T em) throws IOException {
		w.merger();
		super.end(w, em);
	}

}
