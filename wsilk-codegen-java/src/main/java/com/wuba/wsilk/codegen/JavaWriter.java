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

import static com.wuba.wsilk.codegen.Constants.*;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Stack;

import org.apache.commons.lang3.StringUtils;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.base.Strings;
import com.wuba.wsilk.codegen.annoataion.AnnotationBean;
import com.wuba.wsilk.codegen.annoataion.AnnotationBeans;
import com.wuba.wsilk.codegen.annoataion.AnnotationUtils;
import com.wuba.wsilk.codegen.annoataion.AnnotationValue;
import com.wuba.wsilk.codegen.model.Parameter;
import com.wuba.wsilk.codegen.model.Type;
import com.wuba.wsilk.codegen.model.Types;
import com.wuba.wsilk.codegen.parser.JavaUnit;
import com.wuba.wsilk.codegen.spec.FieldElement;
import com.wuba.wsilk.codegen.spec.MethodElement;

import lombok.Setter;

/**
 * 写java
 * 
 * @author mindashuang
 */
public class JavaWriter extends AbstractCodeWriter<JavaWriter>
		implements IJavaWriter<JavaWriter>, IMergeWriter<JavaMergeCode>, CodeWriter<JavaWriter> {

	private final static String JAVA_LANG = "java.lang";

	/**
	 * 类
	 */
	@Setter
	private Set<String> classes = new HashSet<String>();

	/**
	 * 所有的包
	 */
	@Setter
	private Set<String> packages = new HashSet<String>();

	/**
	 * 静态导入
	 */
	@Setter
	private Set<String> statics = new HashSet<String>();

	/**
	 * 类型
	 */
	@Setter
	private Stack<Type> types = new Stack<Type>();

	/**
	 * 注解信息
	 */
	private final AnnotationBeans annotations = new AnnotationBeans();

	/**
	 * 合并的代码
	 */
	private JavaMergeCode mergeCode;

	private boolean mergeAnnotation = true;

	/**
	 * 重置
	 */
	public JavaWriter reInit(JavaWriter javaWriter) {
		javaWriter.setClasses(classes);
		javaWriter.setPackages(packages);
		javaWriter.setTypes(types);
		return javaWriter;
	}

	/**
	 * 构造
	 */
	public JavaWriter(Appendable appendable) {
		super(appendable, 4);
		this.packages.add(JAVA_LANG);

	}

	/**
	 * 设置定制化代码
	 */
	@Override
	public void setMergeCode(JavaMergeCode mergeCode) {
		this.mergeCode = mergeCode;
	}

	/**
	 * 合并import
	 */
	@Override
	public void mergeImport() throws IOException {
		if (mergeCode != null && mergeCode.getCodeUnit() != null) {
			JavaUnit javaUnit = mergeCode.getCodeUnit();
			if (javaUnit.getPackages() != null && javaUnit.getPackages().size() > 0) {
				for (String im : javaUnit.getPackages()) {
					if (!packages.contains(im)) {
						packages.add(im);
						line(IMPORT, SPACE, im, DOT_STAR, SEMICOLON);
					}
				}
			}
			if (javaUnit.getClasses() != null && javaUnit.getClasses().size() > 0) {
				for (String im : javaUnit.getClasses()) {
					if (!classes.contains(im)) {
						classes.add(im);
						line(IMPORT, SPACE, im, SEMICOLON);
					}
				}
			}

			if (javaUnit.getStatics() != null && javaUnit.getStatics().size() > 0) {
				for (String im : javaUnit.getStatics()) {
					if (!statics.contains(im)) {
						statics.add(im);
						line(IMPORT_STATIC, im, DOT_STAR, SEMICOLON);
					}
				}
			}
		}
	}

	/**
	 * 合并中间的
	 */
	public void mergeBody() throws IOException {
		if (mergeCode != null) {
			if (StringUtils.isNotEmpty(mergeCode.getCode())) {
				line(mergeCode.getCode());
			}
		}
	}

	/**
	 * 写注解
	 */
	public void mergeAnnotation() throws IOException {
		if (mergeCode != null && mergeCode.getCodeUnit() != null) {
			AnnotationBeans ans = mergeCode.getCodeUnit().getAnnotationBeans();
			if (ans != null && ans.size() > 0) {
				for (AnnotationBean anntation : ans) {
					/** 添加历史,排除重复 添加客户的注解 */
					if (annotations.matchPref(anntation) != null) {
						annotation(anntation);
					}
				}
			}
		}
		mergeAnnotation = false;
	}

	/**
	 * 禁止相同的注解
	 */
	public JavaWriter annotation(Class<? extends Annotation> type, Pair<?>... pairs) throws IOException {
		AnnotationBean annotationBean = AnnotationUtils.createAnnotationBean(type, this::getType, pairs);
		this.annotation(annotationBean);
		return this;
	}

	/**
	 * 写注解
	 * 
	 * @throws IOException
	 */
	public JavaWriter annotation(AnnotationBean anntationBean) throws IOException {
		if (mergeAnnotation) {
			annotations.add(anntationBean);
		}
		annotations.add(anntationBean);
		boolean append = true;
		if (append) {// 当头部的时候，不允许重复添加
			indent();
			append(AT);
			append(anntationBean.getTypeName());
			List<Pair<?>> pairs = anntationBean.getPairs();
			if (pairs != null && pairs.size() > 0) {
				if (pairs.size() == 1 && pairs.get(0).getName() == null) {
					append(LPAREN);
					annotationConstant(pairs.get(0).getValue());
					append(RPAREN);
				} else {
					boolean first = true;
					for (Pair<?> pair : pairs) {
						if (!first) {
							append(COMMA);
						} else {
							append(LPAREN);
						}
						first = false;
						append(pair.getName()).append(EQ);
						annotationConstant(pair.getValue());
					}
					if (!first) {
						append(RPAREN);
					}
				}
			}
			nl();
		}
		return this;
	}

	/**
	 * 输出
	 * 
	 * @throws IOException
	 */
	private void annotationConstant(Object value) throws IOException {
		if (value.getClass().isArray()) {
			append(LBRACE);
			boolean first = true;
			for (Object o : (Object[]) value) {
				if (!first) {
					append(COMMA);
				}
				annotationConstant(o);
				first = false;
			}
			append(RBRACE);
		} else {
			if (value instanceof AnnotationBean) {
				AnnotationBean bean = (AnnotationBean) value;
				annotation(bean);
			} else {
				AnnotationValue annotationValue = (AnnotationValue) value;
				append(annotationValue.getSimpleValue());
			}
		}
	}

	@Override
	public JavaWriter annotation(Annotation annotation) throws IOException {
		return annotation(AnnotationUtils.createAnnotationBean(annotation, this::getType));
	}

	@Override
	public JavaWriter annotation(Class<? extends Annotation> annotationType) throws IOException {
		return annotation(AnnotationUtils.createAnnotationBean(annotationType, this::getType));
	}

	private String getType(Class<?> type) {
		String name;
		if (classes.contains(type.getName()) || packages.contains(type.getPackage().getName())) {
			name = type.getSimpleName();
		} else {
			name = type.getName();
		}
		return name;
	}

	@Override
	public JavaWriter beginClass(Modifier.Class modifier, Type type) throws IOException {
		return beginClass(modifier, type, null);
	}

	public JavaWriter beginClass(Type type) throws IOException {
		return beginClass(Modifier.Class.PUBLIC, type, null);
	}

	@Override
	public JavaWriter beginClass(Modifier.Class modifier, Type type, Type superClass, Type... interfaces)
			throws IOException {
		return createClass(modifier, type, superClass, interfaces);
	}

	public JavaWriter beginClass(Type type, Type superClass, Type... interfaces) throws IOException {
		return createClass(Modifier.Class.PUBLIC, type, superClass, interfaces);
	}

	private JavaWriter createClass(Modifier.Class modifier, Type type, Type superClass, Type... interfaces)
			throws IOException {
		packages.add(type.getPackageName());
		beginLine(modifier.name, type.getGenericName(false, packages, classes));
		if (superClass != null) {
			append(SPACE).append(EXTENDS).append(SPACE).append(superClass.getGenericName(false, packages, classes));
		}
		if (interfaces != null && interfaces.length > 0) {
			append(SPACE);
			append(IMPLEMENTS);
			append(SPACE);
			for (int i = 0; i < interfaces.length; i++) {
				if (i > 0) {
					append(COMMA);
				}
				append(interfaces[i].getGenericName(false, packages, classes));
			}
		}
		append(LBRACE).nl().nl();
		in();
		types.push(type);
		return this;
	}

	@Override
	public JavaWriter beginInterface(Modifier.Class modifier, Type type, Type... interfaces) throws IOException {
		packages.add(type.getPackageName());
		beginLine(modifier.name, type.getGenericName(false, packages, classes));
		if (interfaces.length > 0) {
			append(SPACE);
			append(EXTENDS);
			append(SPACE);
			for (int i = 0; i < interfaces.length; i++) {
				if (i > 0) {
					append(COMMA);
				}
				append(interfaces[i].getGenericName(false, packages, classes));
			}
		}
		append(LBRACE).nl().nl();
		in();
		types.push(type);
		return this;
	}

	public JavaWriter beginInterface(Type type, Type... interfaces) throws IOException {
		return beginInterface(Modifier.Class.PUBLIC_INTERFACE, type, interfaces);
	}

	/** 构造器 */
	public <T> JavaWriter beginConstructor(Modifier.Field modifier, Collection<T> parameters,
			Function<T, Parameter> transformer) throws IOException {
		types.push(types.peek());
		beginLine(modifier.name, types.peek().getSimpleName()).params(parameters, transformer).append(LBRACE).nl();
		return in();
	}

	public <T> JavaWriter beginConstructor(Collection<T> parameters, Function<T, Parameter> transformer)
			throws IOException {
		return beginConstructor(Modifier.Field.PUBLIC, parameters, transformer);
	}

	public JavaWriter beginConstructor(Modifier.Field modifier, Parameter... parameters) throws IOException {
		types.push(types.peek());
		beginLine(modifier.name, types.peek().getSimpleName()).params(parameters).append(LBRACE).nl();
		return in();
	}

	public JavaWriter beginConstructor(Parameter... parameters) throws IOException {
		return beginConstructor(Modifier.Field.PUBLIC, parameters);
	}

	/** 方法 */
	public JavaWriter beginMethod(Modifier.Field modifier, Type returnType, String methodName, Type[] exceptions,
			Parameter... args) throws IOException {
		types.push(types.peek());
		beginLine(modifier.name, returnType.getGenericName(true, packages, classes), SPACE, methodName).params(args);
		/** 抛出异常 */
		if (exceptions != null && exceptions.length > 0) {
			append(SPACE).append(THROWS);
			for (int i = 0; i < exceptions.length; i++) {
				if (i > 0) {
					append(COMMA);
				}
				append(exceptions[i].getSimpleName());
			}
		}
		/** 如果是抽象方法，可以不实现 */
		if (!(modifier == Modifier.Field.PROTECTED_ABSTRACT || modifier == Modifier.Field.PUBLIC_ABSTRACT)) {
			append(LBRACE).nl();
			return in();
		} else {
			return append(SEMICOLON).nl().nl();
		}
	}

	@Override
	public JavaWriter beginMethod(MethodElement methodElement) throws IOException {
		return beginMethod(methodElement.getModifier(), methodElement.getReturnType(), methodElement.getName(),
				methodElement.getExceptions(), methodElement.getArgs());
	}

	public JavaWriter beginMethod(Modifier.Field modifier, Type returnType, String methodName, Parameter... args)
			throws IOException {
		return beginMethod(MethodElement.builder(modifier, returnType, methodName, args));
	}

	public JavaWriter beginMethod(Type returnType, String methodName, Parameter... args) throws IOException {
		return beginMethod(MethodElement.builder(returnType, methodName, args));
	}

	@Override
	public <T> JavaWriter beginMethod(Modifier.Field field, Type returnType, String methodName,
			Collection<T> parameters, Function<T, Parameter> transformer) throws IOException {
		return beginMethod(MethodElement.builder(field, returnType, methodName, transform(parameters, transformer)));
	}

	public <T> JavaWriter beginMethod(Type returnType, String methodName, Collection<T> parameters,
			Function<T, Parameter> transformer) throws IOException {
		return beginMethod(Modifier.Field.PUBLIC, returnType, methodName, parameters, transformer);
	}

	@Override
	public JavaWriter end() throws IOException {
		types.pop();
		out();
		return line(RBRACE).nl();
	}

	@Override
	public JavaWriter field(Type type, String name) throws IOException {
		return line(type.getGenericName(true, packages, classes), SPACE, name, SEMICOLON).nl();
	}

	public JavaWriter field(Type type, String name, String value) throws IOException {
		return line(type.getGenericName(true, packages, classes), SPACE, name, EQ, value, SEMICOLON).nl();
	}

	public JavaWriter field(Modifier.Field modifier, Type type, String name) throws IOException {
		return line(modifier.name, type.getGenericName(true, packages, classes), SPACE, name, SEMICOLON).nl();
	}

	public JavaWriter field(Modifier.Field modifier, Type type, String name, String value) throws IOException {
		return line(modifier.name, type.getGenericName(true, packages, classes), SPACE, name, EQ, value, SEMICOLON)
				.nl();
	}

	public JavaWriter field(FieldElement fieldElement) throws IOException {
		return field(fieldElement.getModifier(), fieldElement.getType(), fieldElement.getName(),
				fieldElement.getValue());
	}

	@Override
	public String getClassConstant(String className) {
		return className + DOT_CLASS;
	}

	@Override
	public String getGenericName(boolean asArgType, Type type) {
		return type.getGenericName(asArgType, packages, classes);
	}

	@Override
	public String getRawName(Type type) {
		return type.getRawName(packages, classes);
	}

	@Override
	public JavaWriter imports(Class<?>... imports) throws IOException {
		for (Class<?> cl : imports) {
			classes.add(cl.getName());
			line(IMPORT, SPACE, cl.getName(), SEMICOLON);
		}
		nl();
		return this;
	}

	@Override
	public JavaWriter imports(Package... imports) throws IOException {
		for (Package p : imports) {
			packages.add(p.getName());
			line(IMPORT, SPACE, p.getName(), DOT_STAR, SEMICOLON);
		}
		nl();
		return this;
	}

	@Override
	public JavaWriter importClasses(String... imports) throws IOException {
		for (String cl : imports) {
			classes.add(cl);
			line(IMPORT, SPACE, cl, SEMICOLON);
		}
		nl();
		return this;
	}

	@Override
	public JavaWriter importPackages(String... imports) throws IOException {
		for (String p : imports) {
			packages.add(p);
			line(IMPORT, SPACE, p, DOT_STAR, SEMICOLON);
		}
		nl();
		return this;
	}

	@Override
	public JavaWriter staticimports(Class<?>... imports) throws IOException {
		for (Class<?> cl : imports) {
			statics.add(cl.getName());
			line(IMPORT_STATIC, cl.getName(), DOT_STAR, SEMICOLON);
		}
		return this;
	}

	public JavaWriter staticimports(Type... types) throws IOException {
		for (Type type : types) {
			statics.add(type.getFullName());
			line(IMPORT_STATIC, type.getFullName(), DOT_STAR, SEMICOLON);
		}
		return this;
	}

	@Override
	public JavaWriter javadoc(String... lines) throws IOException {
		line("/**");
		for (String line : lines) {
			line(" * ", line);
		}
		return line(" */");
	}

	@Override
	public JavaWriter packageDecl(String packageName) throws IOException {
		packages.add(packageName);
		return line(PACKAGE, SPACE, packageName, SEMICOLON).nl();
	}

	private <T> JavaWriter params(Collection<T> parameters, Function<T, Parameter> transformer) throws IOException {
		append(LPAREN);
		boolean first = true;
		for (T param : parameters) {
			if (!first) {
				append(COMMA);
			}
			param(transformer.apply(param));
			first = false;
		}
		append(RPAREN);
		return this;
	}

	private JavaWriter params(Parameter... params) throws IOException {
		append(LPAREN);
		if (params != null) {
			for (int i = 0; i < params.length; i++) {
				if (i > 0) {
					append(COMMA);
				}
				param(params[i]);
			}
		}
		append(RPAREN);
		return this;
	}

	private JavaWriter param(Parameter parameter) throws IOException {
		append(parameter.getType().getGenericName(true, packages, classes));
		append(SPACE);
		append(parameter.getName());
		return this;
	}

	@Override
	public JavaWriter suppressWarnings(String type) throws IOException {
		return line("@SuppressWarnings(\"", type, "\")");
	}

	@Override
	public JavaWriter suppressWarnings(String... types) throws IOException {
		return line("@SuppressWarnings(\"", Joiner.on(",").join(types), ",\")");
	}

	private <T> Parameter[] transform(Collection<T> parameters, Function<T, Parameter> transformer) {
		Parameter[] rv = new Parameter[parameters.size()];
		int i = 0;
		for (T value : parameters) {
			rv[i++] = transformer.apply(value);
		}
		return rv;
	}

	/** 添加代码 */
	public JavaWriter addStatement(String format, Object... args) throws IOException {
		line(Strings.lenientFormat(format, args));
		return nl();
	}

	/**
	 * 返回
	 * 
	 * @throws IOException
	 */

	public JavaWriter returns(String name, boolean thisPrefix) throws IOException {
		if (thisPrefix) {
			return line(RETURN, THIS, DOT, name, SEMICOLON);
		} else {
			return line(RETURN, name, SEMICOLON);
		}
	}

	public JavaWriter returns(String name) throws IOException {
		return returns(name, false);
	}

	public JavaWriter returns() throws IOException {
		return returns(THIS);
	}

	public JavaWriter returnsNull() throws IOException {
		return returns(NULL);
	}

	/**
	 * 赋值
	 */
	public JavaWriter assign(String name) throws IOException {
		return assign(name, name);
	}

	public JavaWriter assign(String key, String name) throws IOException {
		return line(THIS, DOT, key, EQ, name, SEMICOLON);
	}

	/** 设置set方法 */
	public JavaWriter set(Type type, String name) throws IOException {
		return set(type, name, StringUtils.capitalize(name));
	}

	public JavaWriter set(Type type, String name, String methodName) throws IOException {
		beginMethod(
				MethodElement.builder(Modifier.Field.PUBLIC, Types.VOID, SET + methodName, new Parameter(name, type)));
		line(THIS, DOT, name, EQ, name, SEMICOLON);
		end();
		return this;
	}

	/** 设置get方法 */
	public JavaWriter get(Type type, String name) throws IOException {
		return get(type, name, StringUtils.capitalize(name));
	}

	public JavaWriter get(Type type, String name, String methodName) throws IOException {
		beginMethod(MethodElement.builder(Modifier.Field.PUBLIC, type, GET + methodName));
		returns(name, true);
		end();
		return this;
	}

	/** if else */
	public JavaWriter ifExp(String express, String action) throws IOException {
		return ifelse(express, action, IF);
	}

	public JavaWriter elseExp(String action) throws IOException {
		return ifelse(null, action, ELSE).nl();
	}

	public JavaWriter elseIfExp(String express, String action) throws IOException {
		return ifelse(express, action, ELSEIF);
	}

	public JavaWriter ifelse(String express, String action, String token) throws IOException {
		if (express != null) {
			indent();
		}
		append(token);
		if (express != null) {
			append(LPAREN).append(express).append(RPAREN);
		}
		append(LBRACE).nl();
		in();
		indent();
		append(action).append(SEMICOLON).nl();
		out();
		indent();
		append(RBRACE);
		return this;
	}

	/** for each */

	/** while */

	/** while */

	/** try catch */

	/** 调用父类 */
	public JavaWriter superInvoke(String name, Parameter... args) throws IOException {
		return line(SUPER, DOT, name, packMethod(args), SEMICOLON);
	}

	public JavaWriter superInvokeReturn(String name, Parameter... args) throws IOException {
		return line(RETURN, SUPER, DOT, name, packMethod(args), SEMICOLON);
	}

	/**
	 * 组装参数
	 */
	public String packMethod(Parameter... args) {
		String packMethod = BRAKETS;
		if (args != null && args.length > 0) {
			StringBuilder builder = new StringBuilder();
			builder.append(LPAREN);
			int length = args.length;
			for (int i = 0; i < length; i++) {
				if (i > 0) {
					builder.append(COMMA);
				}
				Parameter p = args[i];
				builder.append(p.getName());
			}
			builder.append(RPAREN);
			packMethod = builder.toString();
		}
		return packMethod;
	}

}
