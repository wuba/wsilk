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

package com.wuba.wsilk.codegen.model;

import java.util.List;
import java.util.Set;

/**
 * 类型
 * 
 * @author mindashuang
 */
public interface Type {
	/**
	 * 获得类型
	 * 
	 * @param category 类型的分类
	 * 
	 * @return 类型
	 * 
	 */
	Type as(TypeCategory category);

	/**
	 * 获得数组类型
	 * 
	 * @return 获得数组类型
	 * 
	 */
	Type asArrayType();

	/**
	 * 获得组件类型
	 * 
	 * @return 获得组件类型
	 * 
	 */
	Type getComponentType();

	/**
	 * 获得封闭类型
	 * 
	 * @return 获得封闭类型
	 * 
	 */
	Type getEnclosingType();

	/**
	 * 获得类别
	 * 
	 * @return 获得类别
	 * 
	 */
	TypeCategory getCategory();

	/**
	 * 获得完整路径的名字
	 * 
	 * @return 获得完整路径的名字
	 * 
	 */
	String getFullName();

	/**
	 * 获得原始名字
	 * 
	 * @param asArgType 是否是参数
	 * 
	 * @return 获得原始名字
	 * 
	 */
	String getGenericName(boolean asArgType);

	/**
	 * 获得泛型名字
	 * 
	 * @param asArgType 是否是参数
	 * @param packages  包
	 * @param classes   类
	 * 
	 * @return 获得原始名字
	 * 
	 */
	String getGenericName(boolean asArgType, Set<String> packages, Set<String> classes);

	/**
	 * 获得java class
	 * 
	 * @return 获得java class
	 * 
	 */
	Class<?> getJavaClass();

	/**
	 * 获得包名字
	 * 
	 * @return 获得包名
	 * 
	 */
	String getPackageName();

	/**
	 * 获得参数
	 * 
	 * @return 获得参数
	 * 
	 */
	List<Type> getParameters();

	/**
	 * 获得原始名字
	 * 
	 * @param packages 包
	 * @param classes  类
	 * @return 获得原始名字
	 * 
	 */
	String getRawName(Set<String> packages, Set<String> classes);

	/**
	 * 获得简单名字
	 * 
	 * @return 获得简单名字
	 * 
	 */
	String getSimpleName();

	/**
	 * 是否是final
	 * 
	 * @return 是否是final
	 * 
	 */
	boolean isFinal();

	/**
	 * 是否是基础类
	 * 
	 * @return 是否是基础类
	 * 
	 */
	boolean isPrimitive();

	/**
	 * 是否是memeber类
	 * 
	 * @return 是否是memeber类
	 * 
	 */
	boolean isMember();

}