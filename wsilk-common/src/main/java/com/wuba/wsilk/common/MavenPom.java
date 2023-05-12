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

package com.wuba.wsilk.common;

import java.io.File;
import java.io.FileInputStream;
import java.util.List;
import com.google.common.collect.Lists;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * 依赖
 * 
 * @author mindashuang
 */

public class MavenPom {

	public static List<Dependency> dependencys(Class<?> target) {
		List<Dependency> dependencys = Lists.newArrayList();
		Dependency.List dependencyList = target.getAnnotation(Dependency.List.class);
		if (dependencyList != null) {
			for (Dependency d : dependencyList.value()) {
				dependencys.add(d);
			}
		} else {
			Dependency dependency = target.getAnnotation(Dependency.class);
			if (dependency != null) {
				dependencys.add(dependency);
			}
		}
		return dependencys;
	}

	/**
	 * 持久化
	 */
	public static void serializable(List<Dependency> dependencies, File file) {
		FileOutputStream fos = null;
		ObjectOutputStream oos = null;
		try {
			File pomFile = pomFile(file);
			fos = new FileOutputStream(pomFile);
			oos = new ObjectOutputStream(fos);
			oos.writeObject(dependencies);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (fos != null) {
					fos.close();
				}
				if (oos != null) {
					oos.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 读取
	 */

	@SuppressWarnings("unchecked")
	public static List<Dependency> deserialize(File file) {
		FileInputStream fis = null;
		ObjectInputStream ois = null;
		try {
			File pomFile = pomFile(file);
			if (pomFile.exists()) {
				fis = new FileInputStream(pomFile);
				ois = new ObjectInputStream(fis);
				List<Dependency> dependencys = (List<Dependency>) ois.readObject();
				pomFile.delete();
				return dependencys;
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} finally {
			try {
				if (fis != null) {
					fis.close();
				}
				if (ois != null) {
					ois.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	/**
	 * 获得一个临时文件
	 */
	public static File pomFile(File projectFile) {
		return new File(projectFile, ".wsilk/pom");
	}

}
