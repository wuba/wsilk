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

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.lang.annotation.Annotation;
import java.util.List;

import javax.annotation.Generated;
import javax.tools.JavaFileObject;

import org.apache.commons.io.FileUtils;

import com.wuba.wsilk.codegen.EntityMeta;
import com.wuba.wsilk.codegen.JavaFormatWriter;
import com.wuba.wsilk.codegen.JavaMergeCode;
import com.wuba.wsilk.codegen.JavaWriter;
import com.wuba.wsilk.codegen.Modifier;
import com.wuba.wsilk.codegen.Pair;
import com.wuba.wsilk.codegen.SerializeInfo;
import com.wuba.wsilk.codegen.model.SimpleType;
import com.wuba.wsilk.codegen.model.Type;
import com.wuba.wsilk.core.NoGenericException;
import com.wuba.wsilk.core.SourceEntityMeta;
import com.wuba.wsilk.core.WsilkConfiguration;
import com.wuba.wsilk.core.serializer.AbstractCodeSerializer;

/**
 * 生成java 代码
 */
abstract class AbstractJavaSerializer<T extends SourceEntityMeta, W extends JavaWriter>
		extends AbstractCodeSerializer<T> {

	protected AbstractJavaSerializer(WsilkConfiguration conf, Class<? extends Annotation> annClass) {
		super(conf, annClass);
	}

	/**
	 * 获得源码路径，并获得原来的代码及写文件的Writer
	 */
	private SerializeInfo javaWriter(T em) throws IOException {
		File srcPath = getSrcPath(em);
		getConfiguration().info("start path : " + srcPath.getAbsolutePath());
		Writer writer = null;
		String name = em.getFullName();
		File sourceFile = new File(srcPath, name.replaceAll("\\.", "/") + ".java");
		String oldCode = null;
		FileUtils.forceMkdir(sourceFile.getParentFile());
		/**
		 * 如果生成器上没有注解
		 */
		if (override()) {
			oldCode = getConfiguration().readFileString(sourceFile);
		}
		writer = new BufferedWriter(
				new OutputStreamWriter(new FileOutputStream(sourceFile, false), getConfiguration().getCharset()));
		return new SerializeInfo(oldCode, writer);
	}

	/**
	 * 获得源码路径
	 */
	public File getSrcPath(EntityMeta em) {
		getConfiguration().info("start serializer name:" + em.getFullName());
		String path = getPath();
		File srcPath = null;
		if (path == null) {
			try {
				srcPath = outputPath();
			} catch (IllegalAccessException e) {
				srcPath = new File(getConfiguration().getProjectPath(), "src/main/java");
			}
		} else {
			srcPath = new File(getConfiguration().getProjectPath(), path);
		}
		return srcPath;
	}

	/**
	 * 输出代码的Writer
	 */
	public abstract W codeWriter(Writer w);

	/**
	 * 输出代码
	 */
	@Override
	protected void serialize(SourceEntityMeta t) {
		Writer w = null;
		try {
			T em = init(t);
			// 创建文件
			SerializeInfo serializeInfo = javaWriter(em);
			// 读取旧的代码，并提取用户的部分内容
			JavaMergeCode mergeCode = JavaMergeCode.create(serializeInfo, em.getSimpleName(), em.getChildName());
			// 生成输出
			w = new JavaFormatWriter(serializeInfo.getWriter(), getConfiguration().getCodeFormat());
			// 写java
			W writer = codeWriter(w);

			// 把客户的旧代码合并进来
			writer.setMergeCode(mergeCode);

			// 写包名
			if (!em.getPackageName().isEmpty()) {
				writer.packageDecl(em.getPackageName());
			}
			// 默认导入
			importDefault(writer, em);

			// 用户导入
			importPackage(writer, em);

			if (override()) {
				writer.mergeImport();
			}

			generatedMessage(writer, em);

			// 写java类
			createJavaHead(writer, em);

			// 写属性
			properties(writer, em);

			// 写构造器
			constructors(writer, em);

			// 写方法
			methods(writer, em);

			// 结束
			end(writer, em);

			// 写外部子类
			innerClass(writer, em);

			w.flush();

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (w != null) {
				try {
					w.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * 生成器的信息
	 */
	protected void generatedMessage(W writer, T t) throws IOException {
		writer.nl();
		writer.annotation(Generated.class, Pair.createValuePair(t.getFullName()));
	}

	/**
	 * 生成java代码
	 */
	public JavaFileObject createSourceFile(EntityMeta em) throws IOException {
		return getConfiguration().getProcessingEnv().getFiler().createSourceFile(em.getFullName(), em.getElement());
	}

	/**
	 * 写不警告信息
	 */
	public void suppressAllWarnings(W writer) throws IOException {
		writer.suppressWarnings("all", "rawtypes", "unchecked");
	}

	/**
	 * 通过原始的java 生成 目标java
	 */
	public T init(SourceEntityMeta em) throws NoGenericException {
		return em.init(getSourceEntityMetaClass(), getSupport(), getConfiguration().getNamespace());
	}

	/**
	 * 生成java的头部内容
	 */
	public void createJavaHead(W writer, T t) throws IOException {
		classAnnotation(writer, t);
		if (override()) {
			SimpleType child = new SimpleType(t.getChildName());
			writer.mergeAnnotation();
			writer.beginClass(t, child);
			// 合并body的内容
			writer.mergeBody();
			writer.end();
			writer.beginClass(Modifier.Class.PRIVATE, child, getSuperClass(t), getSuperInterface(t));
		} else {
			// 创建自己的类
			writer.beginClass(t, getSuperClass(t), getSuperInterface(t));
		}
	}

	/**
	 * 导入依赖的包
	 */
	public void importPackage(W writer, T t) throws IOException {
	}

	/**
	 * 导入默认的包
	 */
	public void importDefault(W writer, T t) throws IOException {
		writer.importPackages("javax.annotation");
		if (assignFroms != null) {
			for (Type assignFrom : assignFroms) {
				writer.importClasses(assignFrom.getFullName());
			}
		}
	}

	/**
	 * 导入注解信息
	 */
	public void classAnnotation(W writer, T t) throws IOException {
	}

	/**
	 * 获得子类
	 */
	public Type getSuperClass(T t) throws IOException {
		return null;
	}

	/**
	 * 获得要实现的接口
	 */
	protected Type[] getSuperInterface(T t) throws IOException {
		List<Type> types = superInterface(t);
		return types.toArray(new Type[types.size()]);
	}

	public List<Type> superInterface(T t) throws IOException {
		return assignFroms;
	}

	/**
	 * 创建构造器
	 */
	public void constructors(W writer, T t) throws IOException {
	}

	/**
	 * 创建属性
	 */
	public void properties(W writer, T t) throws IOException {
	}

	/**
	 * 创建方法
	 */
	public void methods(W writer, T t) throws IOException {
	}

	/**
	 * 创建内部类
	 */
	public void innerClass(W writer, T t) throws IOException {

	}

	/**
	 * 结束类
	 */
	public void end(W w, T em) throws IOException {
		w.end();
	}

}
