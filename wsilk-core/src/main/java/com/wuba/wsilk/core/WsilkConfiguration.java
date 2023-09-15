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

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;

import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic.Kind;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.util.ConfigurationBuilder;

import com.google.common.collect.Sets;
import com.wuba.wsilk.core.tf.DefaultEntityTypeFactory;
import com.wuba.wsilk.common.ClassUtils;
import com.wuba.wsilk.common.ThrowsUtils;
import com.wuba.wsilk.core.adapter.WsilkProcessingEnvironment;
import com.wuba.wsilk.core.tf.AbstractEntityTypeFactory;
import com.wuba.wsilk.core.utils.AstReflectUtils;
import com.wuba.wsilk.core.utils.InstanceUtils;
import com.wuba.wsilk.core.utils.ResourceUtils;

import lombok.Getter;

/**
 * 配置类
 * 
 * @author mindashuang
 * 
 */
public class WsilkConfiguration {

	private final static String NAMESPACE = "namespace";

	/** APT 的核心工具类 */
	@Getter
	private WsilkProcessingEnvironment processingEnv;

	/** APT 环境信息 */
	@Getter
	private RoundEnvironment roundEnv;

	/** 注解集合 */
	private Set<Class<? extends Annotation>> annotations;

	/** 对象实例化工具类 */
	private InstanceUtils instanceUtils;

	/**
	 * 项目的路径
	 */
	@Getter
	private File projectPath;

	@Getter
	private OptionsMapper optionsMapper;

	@Getter
	private Charset charset = Charset.forName("UTF-8");

	private boolean codeFormat = false;

	private boolean canStart = true;

	private File loggerFile;

	@Getter
	private AbstractEntityTypeFactory<? extends SourceEntityMeta> entityTypeFactory;

	/**
	 * 扫描的包
	 */
	private String[] scanPackage = new String[] { "com.wuba" };

	private String namespace;

	public WsilkConfiguration(ProcessingEnvironment processingEnv, RoundEnvironment roundEnv) {
		this.processingEnv = new WsilkProcessingEnvironment(processingEnv);
//		charset = Charset.forName("UTF-8");
		this.roundEnv = roundEnv;
		optionsMapper = new OptionsMapper(this);
		/** 设置关注注解 */
		for (Entry<String, String> entry : this.processingEnv.getOptions().entrySet()) {
			optionsMapper.regOption(entry.getKey(), entry.getValue());
		}
		String charset = optionsMapper.getOption("charset", "UTF-8");
		if (StringUtils.isNoneEmpty(charset)) {
			this.charset = Charset.forName(charset);
		}
		File projectFile = optionsMapper.getProjectPath();

		if (projectFile != null) {
			/** 获得项目路径 */
			projectPath = projectFile;
			codeFormat = BooleanUtils.toBoolean(optionsMapper.getOption("codeFormat"));
			try {
				this.scanPackage = scanPackage(getClassLoader());
			} catch (IOException e) {
				this.scanPackage = new String[] { "com.wuba" };
				e.printStackTrace();
			}
			String typeFactoryClass = optionsMapper.getOption("typeFactory");
			if (typeFactoryClass != null) {
				Class<?> cls = ClassUtils.loaderClass(typeFactoryClass);
				if (cls != null) {
					entityTypeFactory = getInstanceUtils().createInstance(cls, this);
				}
			}
			String loggerFilePath = optionsMapper.getOption("loggerFile");
			if (loggerFilePath != null) {
				loggerFile = new File(loggerFilePath);
			}
			if (entityTypeFactory == null) {
				this.entityTypeFactory = new DefaultEntityTypeFactory(this);
			}

		} else {
			canStart = false;
		}
	}

	private String[] scanPackage(ClassLoader classLoader) throws IOException {
		Enumeration<URL> resources = classLoader.getResources("META-INF/wsilk");
		Set<String> pks = Sets.newHashSet();
		while (resources.hasMoreElements()) {
			URL url = resources.nextElement();
			URLConnection urlConnection = url.openConnection();
			if (urlConnection instanceof JarURLConnection) {
				JarURLConnection jarURL = (JarURLConnection) urlConnection;
				List<String> values = IOUtils.readLines(jarURL.getInputStream(), getCharset());
				pks.addAll(values);
			} else {
			}
		}
		return pks.toArray(new String[pks.size()]);
	}

	/**
	 * 是否支持覆盖
	 */
	public boolean isOverride() {
		return BooleanUtils.toBoolean(optionsMapper.getOption("override"));
	}

	/**
	 * 
	 * 是否达到运行条件
	 * 
	 */
	public boolean canStart() {
		return canStart;
	}

	/**
	 * 代码是否格式化
	 */
	public boolean getCodeFormat() {
		return codeFormat;
	}

	/**
	 * 错误信息
	 * 
	 * @param message 消息
	 */
	public void error(String message) {
		processingEnv.getMessager().printMessage(Kind.ERROR, message);
		log(message);
	}

	public void error(Exception e) {
		log(ThrowsUtils.string(e));
	}

	/**
	 * 其它信息
	 * 
	 * @param message 消息
	 */
	public void other(String message) {
		processingEnv.getMessager().printMessage(Kind.OTHER, message);
		log(message);
	}

	/**
	 * info
	 * 
	 * @param message 消息
	 */
	public void info(String message) {
		processingEnv.getMessager().printMessage(Kind.NOTE, message);
		log(message);
	}

	/**
	 * 警告
	 * 
	 * @param message 消息
	 */
	public void warning(String message) {
		processingEnv.getMessager().printMessage(Kind.MANDATORY_WARNING, message);
		log(message);
	}

	private void log(String... message) {
		try {
			FileUtils.writeLines(loggerFile, getCharset().name(), Arrays.asList(message), true);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 读取java源码
	 * 
	 * @param Element 元素
	 */
	public String getJavaCode(Element element) {
		StringWriter writer = new StringWriter();
		try {
			IOUtils.copy(AstReflectUtils.openInputStream(element), writer, charset);
		} catch (IOException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
			e.printStackTrace();
		}
		return writer.toString();
	}

	/**
	 * 获得源码路径
	 * 
	 * @param Element 元素
	 */
	public String getPath(Element element) {
		if (element != null) {
			return AstReflectUtils.getPath(element);
		}
		return projectPath.getAbsolutePath();
	}

	/**
	 * 要处理的注解
	 */
	public Set<Class<? extends Annotation>> getEntityAnnotations() {
		return annotations;
	}

	/**
	 * 添加我们关注的所有注解
	 * 
	 * @param annotation 注解
	 * 
	 */
	public WsilkConfiguration setEntityAnnotation(Class<? extends Annotation> annotation) {
		if (annotations == null) {
			annotations = Sets.newHashSet();
		}
		annotations.add(annotation);
		return this;
	}

	/**
	 * 实例化对象管工具
	 */
	public InstanceUtils getInstanceUtils() {
		if (instanceUtils == null) {
			instanceUtils = new InstanceUtils(this);
		}
		return instanceUtils;
	}

	/**
	 * 获得注解的元素
	 */
	public Set<? extends Element> getElements(Class<? extends Annotation> a) {
		return roundEnv.getElementsAnnotatedWith(a);
	}

	/**
	 * 通过类名获得 element
	 */
	public TypeElement getElement(String sourceFile) {
		return processingEnv.getElementUtils().getTypeElement(sourceFile);
	}

	/**
	 * 
	 * 加载项目的的配置文件
	 * 
	 * @param propertyFile 配文件的名字或者路径+名字
	 */
	public Properties loadAllProperties(String propertyFile) {
		Properties properties = null;
		try {
			properties = ResourceUtils.getProperties(propertyFile, getClassLoader());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return properties;
	}

	/**
	 * 获得项目的classloader
	 */
	public ClassLoader getClassLoader() {
		return processingEnv.getProcessorClassLoader();
	}

	/**
	 * 扫描并加载类
	 */
	public Reflections reflections() {
		return new Reflections(new ConfigurationBuilder().addClassLoader(getClassLoader()).forPackages(scanPackage)
				.addScanners(new SubTypesScanner(true)));
	}

	/**
	 * 从项目中载入文件内容
	 * 
	 * @param resourcePath 文件路径
	 * @param fileName     文件名字
	 * 
	 * @throws IOException
	 */
	public String loadFile(String resourcePath, String fileName) throws IOException {
		return readFileString(getFile(resourcePath, fileName));
	}

	/**
	 * 获得文件
	 * 
	 * @param resourcePath 文件路径
	 * @param fileName     文件名字
	 */
	public File getFile(String resourcePath, String fileName) {
		return new File(projectPath, resourcePath + fileName);
	}

	/**
	 * 获得hash的文件
	 */
	public File getHashFile(SourceEntityMeta em) {
		return new File(getProjectPath(),
				".wsilk" + File.separator + "hash" + File.separator + em.getFullName() + ".sha256");
	}

	/**
	 * 获得src/main/resources/ 下的文件
	 * 
	 * @param resourcePath 文件路径
	 * @param fileName     文件名字
	 */
	public File getSrcResourceFile(String fileName) {
		return getFile(JavaConstants.getSrcMainResources(), fileName);
	}

	/**
	 * 读取文件内容
	 */
	public String readFileString(File sourceFile) throws IOException {
		String content = null;
		if (sourceFile.exists()) {
			content = FileUtils.readFileToString(sourceFile, getCharset());
		}
		return content;
	}

	public String getNamespace() {
		if (namespace == null) {
			namespace = getOptionsMapper().getOption(NAMESPACE);
			if (namespace == null) {
				namespace = StringUtils.EMPTY;
			}
		}
		return namespace;
	}

}
