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

package com.wuba.wsilk.codegen;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.Collection;

import com.google.common.base.Function;
import com.wuba.wsilk.codegen.model.Parameter;
import com.wuba.wsilk.codegen.model.Type;
import com.wuba.wsilk.codegen.spec.FieldElement;
import com.wuba.wsilk.codegen.spec.MethodElement;

/**
 * 
 * 写java
 * 
 * @author mindashuang
 */
public interface IJavaWriter<T extends AbstractCodeWriter<T>> extends CodeWriter<T> {

	/**
	 * 
	 * 通过类型获得原始名字
	 * 
	 * @param type 类型
	 * @return 获得原始名字
	 */
	String getRawName(Type type);

	/**
	 * 获得泛型名字
	 * 
	 * @param asArgType 是否是参数类型
	 * @param type      类型
	 * @return 获得泛型名字
	 */
	String getGenericName(boolean asArgType, Type type);

	/**
	 * 获得类的常量
	 * 
	 * @param className 类名
	 * 
	 * @return 类的常量名
	 */
	String getClassConstant(String className);

	/**
	 * 设置包路径
	 * 
	 * @param packageName 包路径
	 * 
	 * @return 返回代码输出器
	 * @throws IOException 代码输出异常
	 */
	T packageDecl(String packageName) throws IOException;

	/**
	 * 导入的类
	 * 
	 * @param imports 类信息
	 * 
	 * @return 返回代码输出器
	 * @throws IOException 代码输出异常
	 */
	T imports(Class<?>... imports) throws IOException;

	/**
	 * 导入的包
	 * 
	 * @param imports 包信息
	 * 
	 * @return 返回代码输出器
	 * @throws IOException 代码输出异常
	 */
	T imports(Package... imports) throws IOException;

	/**
	 * 导入的类
	 * 
	 * @param classes 类数组
	 * 
	 * @return 返回代码输出器
	 * @throws IOException 代码输出异常
	 */
	T importClasses(String... classes) throws IOException;

	/**
	 * 导入包
	 * 
	 * @param packages 导入的包
	 * 
	 * @return 返回代码输出器
	 * @throws IOException 代码输出异常
	 */
	T importPackages(String... packages) throws IOException;

	/**
	 * 静态导入
	 * 
	 * @param imports 导入的类
	 * 
	 * @return 返回代码输出器
	 * @throws IOException 代码输出异常
	 */
	T staticimports(Class<?>... imports) throws IOException;

	/**
	 * 添加注解
	 * 
	 * @param annotation 注解
	 * 
	 * @return 返回代码输出器
	 * @throws IOException 代码输出异常
	 */
	T annotation(Annotation annotation) throws IOException;

	/**
	 * 添加注解
	 * 
	 * @param annotation 注解
	 * 
	 * @return 返回代码输出器
	 * @throws IOException 代码输出异常
	 */
	T annotation(Class<? extends Annotation> annotation) throws IOException;

	/**
	 * 创建一个类
	 * 
	 * @param modifier 修饰符
	 * @param type     类的类型
	 * 
	 * @return 返回代码输出器
	 * @throws IOException 代码输出异常
	 */
	T beginClass(Modifier.Class modifier, Type type) throws IOException;

	/**
	 * 创建一个类
	 * 
	 * @param modifier   修饰符
	 * @param type       类的类型
	 * @param superClass 父类类型
	 * @param interfaces 继承的接口
	 * 
	 * @return 返回代码输出器
	 * @throws IOException 代码输出异常
	 */
	T beginClass(Modifier.Class modifier, Type type, Type superClass, Type... interfaces) throws IOException;

	/**
	 * 创建一个接口
	 * 
	 * @param modifier   修饰符
	 * @param type       接口类型
	 * @param interfaces 继承的接口
	 * 
	 * @return 返回代码输出器
	 * @throws IOException 代码输出异常
	 */
	T beginInterface(Modifier.Class modifier, Type type, Type... interfaces) throws IOException;

	/**
	 * 添加构造器
	 * 
	 * @param modifier    修饰符
	 * @param params      方法参数
	 * @param transformer 参数转换器
	 * 
	 * @return 返回代码输出器
	 * @throws IOException 代码输出异常
	 */
	<M> T beginConstructor(Modifier.Field modifier, Collection<M> params, Function<M, Parameter> transformer)
			throws IOException;

	/**
	 * 添加构造器
	 * 
	 * @param modifier 修饰符
	 * @param params   方法参数
	 * 
	 * 
	 * @return 返回代码输出器
	 * @throws IOException 代码输出异常
	 */
	T beginConstructor(Modifier.Field modifier, Parameter... params) throws IOException;

	/**
	 * 添加方法
	 * 
	 * @param modifier   修饰符
	 * @param returnType 返回值
	 * @param exceptions 异常类型
	 * @param methodName 方法名
	 * @param args       方法参数
	 * 
	 * 
	 * @return 返回代码输出器
	 * @throws IOException 代码输出异常
	 */
	T beginMethod(Modifier.Field modifier, Type returnType, String methodName, Type[] exceptions, Parameter... args)
			throws IOException;

	/**
	 * 添加方法
	 * 
	 * @param modifier    修饰符
	 * @param returnType  返回值
	 * @param methodName  方法名
	 * @param parameters  方法参数
	 * @param transformer 参数转换器
	 * 
	 * 
	 * @return 返回代码输出器
	 * @throws IOException 代码输出异常
	 */
	<M> JavaWriter beginMethod(Modifier.Field modifier, Type returnType, String methodName, Collection<M> parameters,
			Function<M, Parameter> transformer) throws IOException;

	/**
	 * 添加方法
	 * 
	 * @param methodElement 方法的结构
	 * 
	 * @return 返回代码输出器
	 * @throws IOException 代码输出异常
	 */
	T beginMethod(MethodElement methodElement) throws IOException;

	/**
	 * 结束方法
	 * 
	 * @return 返回代码输出器
	 * @throws IOException 代码输出异常
	 */
	T end() throws IOException;

	/**
	 * 添加字段
	 * 
	 * @param type 类型
	 * @param name 名字
	 * 
	 * @return 返回代码输出器
	 * @throws IOException 代码输出异常
	 */

	T field(Type type, String name) throws IOException;

	/**
	 * 添加字段
	 * 
	 * @param type  类型
	 * @param name  名字
	 * @param value 值
	 * 
	 * @return 返回代码输出器
	 * @throws IOException 代码输出异常
	 */
	T field(Type type, String name, String value) throws IOException;

	/**
	 * 添加字段
	 * 
	 * @param modifier 修饰符
	 * @param type     类型
	 * @param name     名字
	 * 
	 * @return 返回代码输出器
	 * @throws IOException 代码输出异常
	 */
	T field(Modifier.Field modifier, Type type, String name) throws IOException;

	/**
	 * 添加字段
	 * 
	 * @param modifier 修饰符
	 * @param type     类型
	 * @param name     名字
	 * @param value    值
	 * 
	 * @return 返回代码输出器
	 * @throws IOException 代码输出异常
	 */
	T field(Modifier.Field modifier, Type type, String name, String value) throws IOException;

	/**
	 * 添加字段
	 * 
	 * @param fieldElement 字段结构
	 * 
	 * @return 返回代码输出器
	 * @throws IOException 代码输出异常
	 */

	T field(FieldElement fieldElement) throws IOException;

	/**
	 * 添加doc
	 * 
	 * @param lines 代码片段
	 * 
	 * @return 返回代码输出器
	 * @throws IOException 代码输出异常
	 */
	T javadoc(String... lines) throws IOException;

	/**
	 * 添加一行数据
	 * 
	 * @param segments 代码片段
	 * 
	 * @return 返回代码输出器
	 * @throws IOException 代码输出异常
	 */
	T beginLine(String... segments) throws IOException;

	/**
	 * 按照行输出代码
	 * 
	 * @param segments 代码片段
	 * 
	 * @return 返回代码输出器
	 * @throws IOException 代码输出异常
	 */
	T line(String... segments) throws IOException;

	/**
	 * 换行
	 * 
	 * @return 返回代码输出器
	 * @throws IOException 代码输出异常
	 */
	T nl() throws IOException;

	/**
	 * 添加警告注解
	 * 
	 * @param type 类型
	 * 
	 * @return 返回代码输出器
	 * @throws IOException 代码输出异常
	 * 
	 */
	T suppressWarnings(String type) throws IOException;

	/**
	 * 添加警告注解
	 * 
	 * @param types 类型
	 * 
	 * @return 返回代码输出器
	 * @throws IOException 代码输出异常
	 * 
	 */
	T suppressWarnings(String... types) throws IOException;

}