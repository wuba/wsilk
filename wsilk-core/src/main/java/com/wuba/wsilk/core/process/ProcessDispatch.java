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

package com.wuba.wsilk.core.process;

import java.lang.annotation.Annotation;
import java.lang.reflect.Modifier;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.lang.model.element.Element;
import org.reflections.Reflections;
import com.google.common.collect.Lists;
import com.wuba.wsilk.common.Dependency;
import com.wuba.wsilk.common.MavenPom;
import com.wuba.wsilk.core.AbstractConfigAble;
import com.wuba.wsilk.core.ConfigAble;
import com.wuba.wsilk.core.Support;
import com.wuba.wsilk.core.WsilkConfiguration;
import com.wuba.wsilk.core.serializer.Serializer;

import lombok.Getter;

/**
 * 任务分发器
 * 
 * @author mindashuang
 * @since 2022-01-22
 * 
 */
@SuppressWarnings("rawtypes")
public class ProcessDispatch extends AbstractConfigAble implements ConfigAble {

	public ProcessDispatch(WsilkConfiguration conf) {
		super(conf);
	}

	@Getter
	private List<DynamicProcess> dynamicProcessList = Lists.newArrayList();

	public void process() {
		try {
			/** 收集所有注解 **/
			Map<Class<? extends Annotation>, Class<? extends Serializer>> serializerMap = serializerMap();

			/** 收集注解下的所有元素 */
			Map<Class<? extends Annotation>, Set<? extends Element>> elementMap = collectElements(
					serializerMap.keySet());

			List<Dependency> dependencies = Lists.newArrayList();
			/** 创建生成器 */
			serializerMap.entrySet().stream().forEach(e -> {
				DynamicProcess dynamicProcess = new DynamicProcess(getConfiguration());
				dynamicProcess.builder(e.getKey(), e.getValue());
				/** 依赖 */
				if (dynamicProcess.getSerializer() != null) {
					List<Dependency> dependencys = MavenPom.dependencys(dynamicProcess.getSerializer().getClass());
					dependencies.addAll(dependencys);
				}
				this.dynamicProcessList.add(dynamicProcess);
			});

			Collections.sort(dynamicProcessList);

			/** 初始化 */
			dynamicProcessList.stream().forEach(e -> {
				Set<? extends Element> elements = elementMap.get(e.getAnnotation());
				e.init(elements);
			});

			/** 运行 */
			dynamicProcessList.stream().forEach(e -> {
				Set<? extends Element> elements = elementMap.get(e.getAnnotation());
				e.process(elements);
			});

			/** 整体完成 */
			dynamicProcessList.stream().forEach(e -> {
				e.over();
			});

			/**
			 * 持久化
			 */
			MavenPom.serializable(dependencies, getConfiguration().getProjectPath());

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 通过注解 Support, 加载所有 Serializer
	 * 
	 */
	public Map<Class<? extends Annotation>, Class<? extends Serializer>> serializerMap() {// 加载所有子类
		WsilkConfiguration configuration = getConfiguration();
		Reflections reflections = configuration.reflections();
		Set<Class<? extends Serializer>> subClass = reflections.getSubTypesOf(Serializer.class);
		return subClass.stream().filter(e -> {
			return !Modifier.isAbstract(e.getModifiers()) && e.getAnnotation(Support.class) != null;
		}).collect(Collectors.toMap(e -> {
			return e.getAnnotation(Support.class).value();
		}, e -> e));

	}

	/**
	 * 收集注解下的所有元素
	 */
	protected Map<Class<? extends Annotation>, Set<? extends Element>> collectElements(
			Set<Class<? extends Annotation>> annotationClass) {
		return annotationClass.stream().collect(
				Collectors.toMap(e -> e, e -> getConfiguration().getElements(e).stream().collect(Collectors.toSet())));
	}

}
