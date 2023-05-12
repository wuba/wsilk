package com.wuba.wsilk.producer.spi;

import java.io.File;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.Set;
import java.util.SortedSet;

import org.apache.commons.io.FileUtils;

import com.google.common.collect.Sets;
import com.wuba.wsilk.codegen.AnnotationMapValue;
import com.wuba.wsilk.codegen.EntityMeta;
import com.wuba.wsilk.codegen.model.Supertype;
import com.wuba.wsilk.core.SourceEntityMeta;
import com.wuba.wsilk.core.Support;
import com.wuba.wsilk.core.WsilkConfiguration;
import com.wuba.wsilk.core.serializer.java.AbstractSingleJavaSerializer;

/**
 * 
 * spi
 * 
 * @author mindashuang
 */
@Support(value = SPI.class, order = 1, pkgInlcudeSuffix = false, parentPkg = false, override = false)
public class SpiJavaSerializer extends AbstractSingleJavaSerializer<SourceEntityMeta> {

	public static final String SERVICES_PATH = "META-INF/services/";

	public SpiJavaSerializer(WsilkConfiguration configuration, Class<? extends Annotation> annClass) {
		super(configuration, annClass);
	}

	@Override
	public void serialize(SourceEntityMeta em) {
		AnnotationMapValue info = em.findAnnotation(getAnnotationClass());
		EntityMeta type = getEntityTypeFactory().createEntityMeta(info.type());
		Set<Supertype> supertypes = em.getInterfaceTypes();
		for (Supertype supertype : supertypes) {
			// 代表注解中的包含 接口
			if (supertype.getEntityMeta().equals(type)) {
				String resourceFile = SERVICES_PATH + type.getFullName();
				File file = getConfiguration().getSrcResourceFile(resourceFile);
				SortedSet<String> allServices = Sets.newTreeSet();
				try {
					if (file.exists()) {
						// 读取里面的文件
						Set<String> oldServices = Sets
								.newHashSet(FileUtils.readLines(file, getConfiguration().getCharset()));
						allServices.addAll(oldServices);
						if (!allServices.add(em.getFullName())) {
							info("No new service entries being added.");
							continue;
						}
					} else {
						FileUtils.forceMkdirParent(file);
						file.createNewFile();
						allServices.add(em.getFullName());
					}
					FileUtils.writeLines(file, allServices);
				} catch (IOException e) {
					error("Unable to create " + resourceFile + ", " + e);
					return;
				}
			} else {
				error("");
			}
		}
	}

}
