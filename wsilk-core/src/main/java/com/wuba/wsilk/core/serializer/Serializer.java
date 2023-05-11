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
package com.wuba.wsilk.core.serializer;


import com.wuba.wsilk.core.ConfigAble;
import com.wuba.wsilk.core.NoGenericException;
import com.wuba.wsilk.core.SourceEntityMeta;
import com.wuba.wsilk.core.Support;
import com.wuba.wsilk.core.tf.AbstractEntityTypeFactory;

/**
 * 
 * 代码生成器
 * 
 * @author mindashuang
 */
public interface Serializer<T extends SourceEntityMeta> extends ConfigAble {

	/**
	 * 生成代码
	 * 
	 * @param em       元数据
	 * @param index    元数据的位置
	 * @param capacity 容量
	 * 
	 * @throws NoGenericException 生成代码失败
	 * 
	 */
	public void serialize(Context context) throws NoGenericException;

	/**
	 * 获得实体
	 * 
	 * @param entityTypeFactory 设置元数据工厂
	 * 
	 */
	default AbstractEntityTypeFactory<? extends SourceEntityMeta> getEntityTypeFactory() {
		return getConfiguration().getEntityTypeFactory();
	}

	/**
	 * 获得上下文
	 */
	public Context context();

	/**
	 * 获得排序
	 * 
	 * @return 获得排序
	 */
	default int getOrder() {
		return getSupport().order();
	}

	/**
	 * 获得支持的类型及配置
	 * 
	 * @return 获得对象上的 Support注解
	 */
	Support getSupport();

	/**
	 * 获得Context
	 */
	default Context context(SourceEntityMeta em, int index, int capacity) {
		return Context.builder().entity(em).index(index).capacity(capacity).build();
	}

	default Context context(SourceEntityMeta em) {
		return context(em, 0, 0);
	}

	/**
	 * 任务结束
	 */
	default void over() {

	}
}
