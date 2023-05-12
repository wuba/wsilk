package com.wuba.wsilk.producer.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.Collection;
import java.util.List;
import java.util.Properties;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;

import com.google.common.collect.Lists;

import com.wuba.wsilk.codegen.AnnotationMapValue;
import com.wuba.wsilk.core.SourceEntityMeta;
import com.wuba.wsilk.core.Support;
import com.wuba.wsilk.core.WsilkConfiguration;
import com.wuba.wsilk.core.serializer.java.AbstractSingleJavaSerializer;

import lombok.Getter;

/**
 * 
 * 生成语言管理器
 * 
 * @author mindashuang
 */
@Support(value = Config.class, order = 1, pkgInlcudeSuffix = true, parentPkg = true, suffix = "Config")
public class ConfigSerializer extends AbstractSingleJavaSerializer<SourceEntityMeta> {

	@Getter
	private List<BundlesSerializer> bundlesSerializers = Lists.newArrayList();

	@Getter
	private BundleSerializer bundleSerializer;

	@Getter
	private String defaultName;

	@Getter
	private String javaName;

	public ConfigSerializer(WsilkConfiguration conf, Class<? extends Annotation> annClass) {
		super(conf, annClass);

	}

	@Override
	public void serialize(SourceEntityMeta em) {
		AnnotationMapValue info = em.findAnnotation(getAnnotationClass());
		defaultName = info.string("value");
		javaName = info.string("name");
		bundleSerializer = new BundleSerializer(getConfiguration(), getAnnotationClass(), this);
		/** 收集所有所有组件,获得代码路径 */
		File path = getParentPath(em);
		Collection<File> files = FileUtils.listFiles(path, new String[] { "properties" }, false);
		for (File file : files) {
			FileInputStream inputStream = null;
			try {
				inputStream = new FileInputStream(file);
				Properties properties = new Properties();
				properties.load(inputStream);
				BundlesSerializer bundlesSerializer = new BundlesSerializer(getConfiguration(), getAnnotationClass(),
						this);
				String bundleName = FilenameUtils.getBaseName(file.getName());
				if (bundleName.equals(defaultName)) {
					bundlesSerializer.setDefault();
				}
				bundlesSerializer.setName(bundleName);
				bundlesSerializer.setProperties(properties);
				bundlesSerializers.add(bundlesSerializer);
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				if (inputStream != null) {
					try {
						inputStream.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}
		int size = bundlesSerializers.size();

		for (int i = 0; i < size; i++) {
			bundlesSerializers.get(i).serialize(context(em, i, size));
		}

		bundleSerializer.serialize(context(em, 0, 0));
	}

}
