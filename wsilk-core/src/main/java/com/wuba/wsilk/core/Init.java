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

import com.wuba.wsilk.codegen.EntityMeta;
import com.wuba.wsilk.codegen.model.SimpleType;
import com.wuba.wsilk.codegen.model.Type;
import com.wuba.wsilk.common.Symbols;

import org.apache.commons.lang3.StringUtils;

/**
 * 类型转换器
 * 
 * @author mindashuang
 */
public interface Init extends Type {

	public final static String SUFFIX = "Support";

	/**
	 * 传递class 获得一个新的元数据
	 * 
	 * @param cls     类型
	 * @param support 支持类
	 * @return 初始化并返回新的类型
	 * 
	 */
	public <T extends SourceEntityMeta> T init(Class<T> cls, Support support, String root);

	/**
	 * 获得子类
	 * 
	 * @return 获得java子类的名字
	 * 
	 */
	default String getChildName() {
		return getSimpleName() + SUFFIX;
	}

	/**
	 * 获得 javaName
	 * 
	 * @param suffix java名字的后缀
	 * @return 获得java的名字
	 */
	String getJavaName(String suffix);

	/**
	 * 设置新的javaName
	 * 
	 * @param javaName 设置生成java的名字
	 */
	void setJavaName(String javaName);

	/**
	 * 创建一个新的 Type
	 * 
	 * @param support 新的类型的注解支持
	 * 
	 * @return 获得新的类型
	 */
	default Type newType(Support support, String namespance) {
		String simpleName = getJavaName(support.suffix());
		// 上个目录
		String root = root(support, namespance);
		Type type = new SimpleType(this.getCategory(), root + Symbols.DOT + simpleName, root, simpleName, false, false);
		return type;
	}

	default String root(Support support, String namespance) {
		String packageName = this.getPackageName();
		String root;
		if (StringUtils.isEmpty(namespance)) {
			setTop(true);
			root = path(support, packageName);
		} else {
			String find = Symbols.DOT + namespance + Symbols.DOT;
			int pos = packageName.indexOf(find);
			if (pos > 0) {
				int length = find.length();
				String prefix = packageName.substring(0, pos);
				String suffix = packageName.substring(pos + length - 1);
				if (support.pkgInlcudeSuffix()) {
					prefix = prefix + Symbols.DOT + StringUtils.uncapitalize(support.suffix());
				}
				root = prefix + suffix;
				setTop(false);
			} else {
				setTop(true);
				root = path(support, packageName);
			}
		}
		return root;
	}

	default String path(Support support, String packageName) {
		// 上个目录
		if (support.parentPkg()) {
			packageName = packageName.substring(0, packageName.lastIndexOf(Symbols.DOT));
		}
		// 添加suffix目录
		if (support.pkgInlcudeSuffix()) {
			packageName = packageName + Symbols.DOT + StringUtils.uncapitalize(support.suffix());
		}
		return packageName;
	}

	/**
	 * 获得原始类型
	 * 
	 * @return 获得原始元数据
	 */
	public abstract EntityMeta getOriginal();

	/**
	 * 是否是顶级目录
	 */
	public void setTop(boolean top);

}
