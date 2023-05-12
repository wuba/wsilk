package com.wuba.wsilk.producer.rule;

import java.io.File;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.Collection;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;

import com.google.common.collect.Lists;
import com.wuba.wsilk.core.SourceEntityMeta;
import com.wuba.wsilk.core.Support;
import com.wuba.wsilk.core.WsilkConfiguration;
import com.wuba.wsilk.core.serializer.java.AbstractJavaSerializerComposite;

import lombok.Getter;

/**
 * 
 * 通过yaml生成调用规则
 * 
 * @author mindashuang
 */
@Support(value = CallRule.class, order = 1, pkgInlcudeSuffix = true, parentPkg = true, suffix = "Rule")
public class CallRuleSerializer extends AbstractJavaSerializerComposite<SourceEntityMeta> {

	@Getter
	private List<CreateSerializer> createSerializers = Lists.newArrayList();

	public CallRuleSerializer(WsilkConfiguration conf, Class<? extends Annotation> annClass) {
		super(conf, annClass);
	}

	/**
	 * 收集所有所有组件
	 */
	@Override
	public void serialize(SourceEntityMeta em) {
		/** 获得代码路径 */
		File path = getParentPath(em);
		Collection<File> files = FileUtils.listFiles(path, new String[] { "rule" }, false);
		for (File file : files) {
			try {
				List<String> lines = FileUtils.readLines(file, getConfiguration().getCharset());
				CreateSerializer createSerializer = new CreateSerializer(getConfiguration(), getAnnotationClass(),
						this);
				String bundleName = FilenameUtils.getBaseName(file.getName());
				createSerializer.setName(bundleName);
				createSerializer.setLines(lines);
				createSerializers.add(createSerializer);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		int size = createSerializers.size();

		for (int i = 0; i < size; i++) {
			createSerializers.get(i).serialize(context(em, i, size));
		}

	}

}
