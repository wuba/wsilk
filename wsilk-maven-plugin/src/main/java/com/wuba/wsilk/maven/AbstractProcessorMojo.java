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

package com.wuba.wsilk.maven;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.DependencyResolutionRequiredException;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.util.Scanner;
import org.sonatype.plexus.build.incremental.BuildContext;

import com.google.common.collect.Lists;

import org.codehaus.plexus.compiler.javac.JavacCompiler;
import org.codehaus.plexus.compiler.javac.JavaxToolsCompiler;
import org.codehaus.plexus.compiler.CompilerConfiguration;
import org.codehaus.plexus.compiler.CompilerConfiguration.CompilerReuseStrategy;
import org.codehaus.plexus.compiler.CompilerResult;

/**
 * 
 * @author mindashuang
 * 
 */
public abstract class AbstractProcessorMojo extends AbstractMojo {

	private final static String SUFFIX = "/*.java";

	private static final String[] ALL_JAVA_FILES_FILTER = new String[] { "**/*.java" };

	/**
	 * 创建项目上下文
	 */
	@Component
	protected BuildContext buildContext;

	/**
	 * 项目
	 */
	@Parameter(readonly = false, required = false, defaultValue = "${project}")
	protected MavenProject project;

	/**
	 * 输出路径
	 */
	@Parameter(name = "outputDirectory", property = "outputDirectory", defaultValue = "wsilk/java", required = false, readonly = false)
	private String outputDirectory;

	/**
	 * 源码路径
	 */
	@Parameter(name = "sourceDirectory", property = "sourceDirectory", defaultValue = "${project.build.sourceDirectory}", required = false, readonly = false)
	protected String sourceDirectory;

	/**
	 * 处理器
	 */
	@Parameter(name = "processor", property = "processor", defaultValue = "com.wuba.wsilk.core.WsilkAnnotationProcessor", required = false, readonly = false)
	protected String processor;
	/**
	 * 编码
	 */
	@Parameter(name = "sourceEncoding", property = "sourceEncoding", defaultValue = "${project.build.sourceEncoding}", required = false, readonly = false)
	protected String sourceEncoding;

	/**
	 * 是否允许增量执行
	 */
	@Parameter(name = "incremental", property = "incremental", defaultValue = "true", required = false, readonly = false)
	protected boolean incremental;

	/**
	 * 是否支持覆盖
	 */
	@Parameter(name = "override", property = "override", defaultValue = "false", required = false, readonly = false)
	protected boolean override;

	/**
	 * 扫描的路径
	 */
	@Parameter(name = "scanPackage", property = "scanPackage", defaultValue = "", required = false, readonly = false)
	protected String scanPackage;

	/**
	 * 是否日志
	 */
	@Parameter(name = "logger", property = "logger", defaultValue = "false", required = false, readonly = false)
	protected boolean logger;

	/**
	 * 依赖的jar
	 */
	@Parameter(name = "pluginArtifacts", property = "pluginArtifacts", defaultValue = "${plugin.artifacts}", required = false, readonly = false)
	private List<Artifact> pluginArtifacts;

	/**
	 * 编译选择
	 */
	@Parameter(name = "options", property = "options", required = false, readonly = false)
	protected Map<String, String> options;

	/**
	 * 编译选项
	 */
	protected Map<String, String> compilerOptions;

	/**
	 * 包含
	 */
	protected Set<String> includes = new HashSet<String>();

	/**
	 * 显示错误
	 */
	protected boolean showWarnings = false;

	/**
	 * 错误日志
	 */
	protected boolean logOnlyOnError = false;

	private File loggerFile;

	private final String loggerFileName = "wsilk.log";

	public File getOutputDirectory() {
		return new File(project.getBasedir(), this.outputDirectory);
	}

	protected File getSourceDirectory() {
		return new File(this.sourceDirectory);
	}

	private List<String> compileClasspath() {
		List<String> pathElements = Lists.newArrayList();
		try {
			if (isForTest()) {
				pathElements = this.project.getTestClasspathElements();
			} else {
				pathElements = this.project.getCompileClasspathElements();
			}
		} catch (DependencyResolutionRequiredException e) {
			getLog().warn("exception calling getCompileClasspathElements", (Throwable) e);
			return null;
		}
		if (this.pluginArtifacts != null) {
			for (Artifact a : this.pluginArtifacts) {
				if (a.getFile() != null) {
					// 添加jar
					pathElements.add(a.getFile().getAbsolutePath());
				}
			}
		}
		return pathElements;
	}

	/**
	 * javac编译的选项
	 */
	private CompilerConfiguration buildCompilerConfiguration(Set<File> files) throws IOException {

		CompilerConfiguration configuration = new CompilerConfiguration();
		// 输出路径
		configuration.setAnnotationProcessors(new String[] { processor });
		configuration.setBuildDirectory(getSourceDirectory());
		configuration.setOutputLocation(getOutputDirectory().getAbsolutePath());
		configuration.setClasspathEntries(compileClasspath());
		configuration.setCompilerReuseStrategy(CompilerReuseStrategy.AlwaysNew);
		configuration.setWarnings("false");
		if (this.sourceEncoding != null) {
			configuration.setSourceEncoding(this.sourceEncoding);
		}
		configuration.setWarnings(SUFFIX);
		configuration.setProc("only");
		if (this.showWarnings) {
			configuration.setShowWarnings(showWarnings);
		}
		configuration.addSourceLocation(getSourceDirectory().getCanonicalPath());

		Properties model = project.getModel().getProperties();
		Properties omodel = project.getOriginalModel().getProperties();
		String target = model.getProperty("maven.compiler.target");
		if (target == null) {
			target = omodel.getProperty("maven.compiler.target");
		}
		String source = model.getProperty("maven.compiler.source");
		if (source == null) {
			source = omodel.getProperty("maven.compiler.source");
		}
		String version = model.getProperty("maven.compiler.compilerVersion");
		if (version == null) {
			version = omodel.getProperty("maven.compiler.compilerVersion");
		}
		configuration.setSourceVersion(source);
		configuration.setTargetVersion(target);
		// 源码
		configuration.setSourceFiles(files);
		Map<String, String> custom = new HashMap<>();
		if (this.options != null) {
			for (Map.Entry<String, String> entry : this.options.entrySet()) {
				custom.put("-A" + entry.getKey() + "=" + entry.getValue(), null);
			}
		}
		custom.put("-AprojectPath=" + project.getBasedir().getAbsolutePath(), null);
		custom.put("-Aoverride=" + override, null);
		if (getOutputDirectory() != null) {
			custom.put("-As=" + getOutputDirectory().getPath(), null);
		}
		configuration.setCustomCompilerArgumentsAsMap(custom);
		if (this.compilerOptions != null) {
			custom.putAll(this.compilerOptions);
		}
		List<String> opts = new ArrayList<>(custom.size() * 2);
		for (Map.Entry<String, String> compilerOption : custom.entrySet()) {
			String value = compilerOption.getValue();
			if (StringUtils.isNotBlank(value)) {
				opts.add(value);
			}
		}

		return configuration;
	}

	/**
	 * 过滤文件
	 */
	private Set<File> filterFiles(File directory) {
		Scanner scanner = this.buildContext.newScanner(getSourceDirectory());
		scanner.setIncludes(ALL_JAVA_FILES_FILTER);
		if (this.includes != null && !this.includes.isEmpty()) {
			String[] filters = this.includes.toArray(new String[this.includes.size()]);
			for (int i = 0; i < filters.length; i++) {
				filters[i] = filters[i].replace('.', '/') + SUFFIX;
			}
			scanner.setIncludes(filters);
		}
		scanner.scan();
		String[] includedFiles = scanner.getIncludedFiles();
		if (includedFiles == null || includedFiles.length == 0) {
			return Collections.emptySet();
		}
		Set<File> files = new HashSet<>();
		for (String includedFile : includedFiles) {
			files.add(new File(scanner.getBasedir(), includedFile));
		}
		return files;
	}

	public void debug(String log) {
		getLog().debug(log);
		printFile(1, log);
	}

	public void info(String log) {
		getLog().info(log);
		printFile(0, log);
	}

	public void error(String log, Exception e1) {
		getLog().error(log, e1);
		printFile(2, log, ExceptionUtils.getStackTrace(e1));
	}

	protected void printFile(int i, String... log) {
		if (logger) {
			try {
				FileUtils.writeLines(loggerFile, sourceEncoding, Arrays.asList(log), true);
			} catch (IOException e) {
				getLog().error(e.getMessage(), e);
			}
		}
	}

	/**
	 * 执行业务
	 */
	@Override
	public void execute() throws MojoExecutionException {
		if (getOutputDirectory() != null && !getOutputDirectory().exists()) {
			getOutputDirectory().mkdirs();
		}
		if (logger) {
			loggerFile = new File(getOutputDirectory(), loggerFileName);
		}
		if (sourceEncoding == null) {
			sourceEncoding = "UTF-8";
		}
		debug("start");
		addCompileSourceRoot();
		debug("Using build context: " + this.buildContext);
		File sourceDirectory = getSourceDirectory();
		debug("getSourceDirectory: " + sourceDirectory);
		try {
			debug("incremental is " + incremental);
			if (buildContext != null && incremental) {
				debug("incremental is " + buildContext.isIncremental());
				debug("hasDelta is " + buildContext.hasDelta(sourceDirectory));
				if (!(incremental && buildContext.hasDelta(sourceDirectory))) {
					info("Code generation is skipped in build because code was not modified.");
					return;
				}
			}
			JavacCompiler compiler = new JavacCompiler();
			JavaxToolsCompiler javaxToolsCompiler = new JavaxToolsCompiler();
			FieldUtils.writeField(compiler, "inProcessCompiler", javaxToolsCompiler, true);
			Set<File> files = filterFiles(sourceDirectory);
			if (files.isEmpty()) {
				debug("There is no sources to generatate");
				return;
			}
			CompilerConfiguration configuration = buildCompilerConfiguration(files);
			CompilerResult result = compiler.performCompile(configuration);
			info("compiler source");
			if (result.isSuccess()) {
				if (this.logOnlyOnError) {
					info(String.valueOf(result.getCompilerMessages()));
				}
			} else {
				if (this.logOnlyOnError) {
					info(String.valueOf(result.getCompilerMessages()));
				}
			}
			if (getOutputDirectory() != null) {
				// 更新pom.xml
				updatePom();
				// 更新项目
				this.buildContext.refresh(project.getParentFile());
			}
			debug("generatate over");
		} catch (Exception e1) {
			error("execute error", e1);
			throw new MojoExecutionException(e1.getMessage());
		}
	}

	public void updatePom() {
		PomUpdate pomUpdate = new PomUpdate(project, sourceEncoding);
		pomUpdate.update();
	}

	/** 把需要生成的代码路径加的classpath 中 */
	private void addCompileSourceRoot() {
		if (getOutputDirectory().exists()) {
			project.addCompileSourceRoot(getOutputDirectory().getPath());
		}
	}

	/** 是否是测试 */
	protected boolean isForTest() {
		return false;
	}
}
