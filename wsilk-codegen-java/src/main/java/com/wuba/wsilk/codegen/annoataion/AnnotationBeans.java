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

package com.wuba.wsilk.codegen.annoataion;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.function.BiFunction;

/**
 * 注解bean的信息
 * 
 * @author mindashuang
 * 
 */
public class AnnotationBeans extends ArrayList<AnnotationBean> {

	private static final long serialVersionUID = 522806154612644057L;

	public AnnotationBean matchAll(AnnotationBean annotationBean) {
		return match(annotationBean, (b, a) -> {
			return b.equals(a);
		});
	}

	public AnnotationBean matchPref(AnnotationBean annotationBean) {
		return match(annotationBean, (b, a) -> {
			return b.getTypeName().equals(a.getTypeName());
		});
	}

	public AnnotationBean match(AnnotationBean annotationBean,
			BiFunction<AnnotationBean, AnnotationBean, Boolean> biFunction) {
		Iterator<AnnotationBean> iterator = this.iterator();
		while (iterator.hasNext()) {
			AnnotationBean b = iterator.next();
			if (biFunction.apply(b, annotationBean)) {
				return null;
			}
		}
		add(annotationBean);
		return annotationBean;
	}

}
