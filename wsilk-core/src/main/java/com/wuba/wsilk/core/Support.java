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

import java.lang.annotation.Annotation;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 
 * 提供生成器的注解支持
 * 
 * @author mindashuang
 */
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE })
public @interface Support {

	/** 支持的注解 */
	Class<? extends Annotation> value();

	/** 排序 */
	int order() default 0;

	/** 生成类的后缀 */
	String suffix() default "";

	/** 生成的新类的包路径 是在原始类的包路径的上一层,否则为同一层 */
	boolean parentPkg() default false;

	/** 生成的新类包路径是否加上suffix的标识 */
	boolean pkgInlcudeSuffix() default true;

	/** 是否留出重写空间 */
	boolean override() default false;

}
