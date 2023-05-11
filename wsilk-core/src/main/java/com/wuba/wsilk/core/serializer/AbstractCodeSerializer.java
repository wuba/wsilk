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

import java.io.File;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.List;
import java.util.Set;

import javax.lang.model.element.Element;
import org.apache.commons.lang3.StringUtils;
import com.google.common.collect.Lists;
import com.google.common.hash.Hashing;
import com.google.common.io.ByteSource;
import com.google.common.io.CharSource;
import com.google.common.io.Files;
import com.wuba.wsilk.codegen.AnnotationMapValue;
import com.wuba.wsilk.codegen.EntityMeta;
import com.wuba.wsilk.codegen.PropertyMeta;
import com.wuba.wsilk.codegen.model.Type;
import com.wuba.wsilk.core.SourceEntityMeta;
import com.wuba.wsilk.core.Support;
import com.wuba.wsilk.core.WsilkConfiguration;
import com.wuba.wsilk.core.utils.GenericUtils;

import lombok.Getter;

/**
 * 写 代码
 * 
 * @author mindashuang
 * 
 */
public abstract class AbstractCodeSerializer<T extends SourceEntityMeta> extends AbstractMetaSerializer<T> {

	/**
	 * 元素的接口
	 */
	public final static String ASSINGFROMS = "assignFroms";

	/**
	 * 元素的接口
	 */
	public final static String LIST = "$List";

	/**
	 * 注解类
	 */
	@Getter
	private final Class<? extends Annotation> annotationClass;

	/**
	 * SourceEntityMeta 的类型
	 */
	@Getter
	private final Class<T> sourceEntityMetaClass;

	/**
	 * 支持的注解
	 */
	@Getter
	private Support support;

	/**
	 * 元素的索引
	 */
	@Getter
	private int index;

	/**
	 * 元素的容量
	 */
	@Getter
	private int capacity;

	/**
	 * 元素的接口
	 */
	public List<Type> assignFroms;

	/**
	 * 上下文
	 */
	private Context context;

	/**
	 * meta 上的注解获得值
	 */
	public AbstractCodeSerializer(WsilkConfiguration conf, Class<? extends Annotation> annotationClass) {
		super(conf);
		/** 注解 */
		this.annotationClass = annotationClass;
		Class<?> thisClass = this.getClass();
		/** suport信息 */
		this.support = thisClass.getAnnotation(Support.class);
		/** 生成 */
		this.sourceEntityMetaClass = GenericUtils.getGeneric(thisClass);
	}

	/**
	 * 获得输出路径
	 */
	public File outputPath() throws IllegalAccessException {
		return new File(getConfiguration().getOptionsMapper().getOption("s"));
	}

	public boolean override() {
		if (getSupport() != null)
			return getSupport().override();
		return false;
	}

	/**
	 * 获得配置的路径
	 */
	public String getPath() {
		return this.getConfiguration().getOptionsMapper().getOption(getAnnotationClass().getSimpleName());
	}

	/**
	 * 获得元素的父路径
	 */
	public File getParentPath(SourceEntityMeta em) {
		return new File(getConfiguration().getPath(em.getElement())).getParentFile();
	}

	/**
	 * 生成 SourceEntityMeta
	 */
	public T createSourceEntityMeta(Class<?> target, Class<? extends Serializer<?>> serializer) {
		return getEntityTypeFactory().createEntityMeta(target).init(getSourceEntityMetaClass(),
				serializer.getAnnotation(Support.class), getConfiguration().getNamespace());
	}

	/**
	 * 生成 SourceEntityMeta
	 */
	public T createSourceEntityMeta(Element element, Class<? extends Serializer<?>> serializer) {
		return getEntityTypeFactory().createEntityMeta(element).init(getSourceEntityMetaClass(),
				serializer.getAnnotation(Support.class), getConfiguration().getNamespace());
	}

	/**
	 * 生成代码文件
	 */
	@Override
	public void serialize(Context context) {
		SourceEntityMeta t = context.getEntity();
		assignFroms = Lists.newArrayList();
		this.context = context;
		this.setOriginalMeta(t);
		/** 计算文件的hash */
		CodeHash codehash = calculateCodeHash(t);
		try {
			if (canSerialize(codehash)) {
				/** 获得接口上的信息 */
				assignForm(t);
				serialize(t);
				saveCodeHash(codehash);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 返回上下文
	 */
	public Context context() {
		return context;
	}

	/**
	 * 
	 * 生成代码
	 * 
	 * @param t 元数据
	 * @throws Exception 生成代码异常
	 * 
	 */
	protected abstract void serialize(SourceEntityMeta t) throws Exception;

	/**
	 * 是否源码变化，如果没有变化，就不生成
	 */
	private boolean canSerialize(CodeHash codehash) {
		return getConfiguration().isOverride() || codehash.canSerialize();
	}

	/**
	 * 是否是主键
	 */
	public PropertyMeta primary(EntityMeta em) {
		Set<PropertyMeta> propertyMetas = em.getAllProperties();
		PropertyMeta key = null;
		for (PropertyMeta pm : propertyMetas) {
			if (pm.primaryKey()) {
				key = pm;
				break;
			}
		}
		return key;
	}

	/**
	 * 拿到上面的接口
	 */
	protected void assignForm(SourceEntityMeta t) {
		AnnotationMapValue av = t.findAnnotation(getAnnotationClass());
		if (av != null) {
			Type[] types = av.types(ASSINGFROMS);
			assignForm(types);
		}
	}

	protected void assignForm(Type[] types) {
		if (types != null) {
			for (Type s : types) {
				assignFroms.add(s);
			}
		}
	}

	/**
	 * 计算hash 返回一个对象，对象中包含
	 */

	protected CodeHash calculateCodeHash(SourceEntityMeta em) {
		CodeHash codeHash = new CodeHash();
		if (em != null) {
			String javaCode = getConfiguration().getJavaCode(em.getElement());
			ByteSource byteSource = CharSource.wrap(javaCode).asByteSource(getConfiguration().getCharset());
			try {
				File tempFile = getConfiguration().getHashFile(em);
				if (tempFile.exists()) {
					/** 代码的hash */
					codeHash.setInputHash(Files.asCharSource(tempFile, getConfiguration().getCharset()).read());
				}
				/** 临时文件的hash */
				codeHash.setTmpHash(byteSource.hash(Hashing.sha256()).toString());
				/** hash最终的落地路径 */
				codeHash.setTmpHashFile(tempFile);
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {
			/**
			 * 处理没有元素数据的情况，比如客户自定义生成
			 */
			codeHash.setInputHash(StringUtils.EMPTY);
			codeHash.setTmpHash(StringUtils.EMPTY);
		}
		return codeHash;
	}

	/**
	 * 保存hash值
	 */
	protected void saveCodeHash(CodeHash codeHash) {
		try {
			File temp = codeHash.getTmpHashFile();
			if (temp != null) {
				File parent = temp.getParentFile();
				if (!parent.exists()) {
					parent.mkdirs();
				}
				Files.asCharSink(temp, getConfiguration().getCharset()).write(codeHash.getTmpHash());
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
