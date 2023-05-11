package com.wuba.wsilk.producer.config;

import static com.wuba.wsilk.codegen.Constants.*;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

import org.apache.commons.lang3.RegExUtils;

import com.google.common.collect.Maps;
import com.wuba.wsilk.codegen.JavaWriter;
import com.wuba.wsilk.codegen.Modifier;
import com.wuba.wsilk.codegen.model.Type;
import com.wuba.wsilk.codegen.model.Types;
import com.wuba.wsilk.core.NoGenericException;
import com.wuba.wsilk.core.SourceEntityMeta;
import com.wuba.wsilk.core.WsilkConfiguration;
import com.wuba.wsilk.core.serializer.java.AbstractJavaSerializerDecorator;
import com.wuba.wsilk.producer.config.BundleSerializer.Names;

import lombok.Getter;
import lombok.Setter;

/**
 * 
 * 
 * 生成bundle
 * 
 * @author mindashuang
 */
public class BundlesSerializer extends AbstractJavaSerializerDecorator<SourceEntityMeta, JavaWriter, ConfigSerializer> {

	@Setter
	@Getter
	private String name;

	private boolean def = false;

	@Getter
	private SourceEntityMeta bean;

	private Map<String, String> values;

	private BundleSerializer bundleSerializer;

	public BundlesSerializer(WsilkConfiguration conf, Class<? extends Annotation> annClass, ConfigSerializer parent) {
		super(conf, annClass, parent);
		this.bundleSerializer = parent.getBundleSerializer();
	}

	@Override
	public Type getSuperClass(SourceEntityMeta t) throws IOException {
		t.setJavaName(getParent().getJavaName());
		return t.init(getSourceEntityMetaClass(), getSupport(), getConfiguration().getNamespace());
	}

	public void setProperties(Properties properties) {
		values = Maps.newHashMap();
		for (Entry<Object, Object> entry : properties.entrySet()) {
			String key = formatKey(String.valueOf(entry.getKey()));
			bundleSerializer.addKey(key);
			values.put(key, String.valueOf(entry.getValue()));
		}
	}

	@Override
	public SourceEntityMeta init(SourceEntityMeta em) throws NoGenericException {
		em.setJavaName(getParent().getJavaName() + "_" + name);
		bean = super.init(em);
		return bean;
	}

	@Override
	public void properties(JavaWriter writer, SourceEntityMeta t) throws IOException {
		for (Names names : bundleSerializer.getKeys()) {
			String value = values.get(names.getAbstractName());
			if (value == null) {
				value = EMPTY;
			}
			writer.field(Modifier.Field.PRIVATE_STATIC_FINAL, Types.STRING, names.getStaticName(),
					DOUBLE_QUOTATION + value + DOUBLE_QUOTATION);
		}
	}

	@Override
	public void methods(JavaWriter writer, SourceEntityMeta t) throws IOException {
		for (Names names : bundleSerializer.getKeys()) {
			writer.beginMethod(Types.STRING, names.getAbstractName());
			writer.returns(names.getStaticName());
			writer.end();
		}
	}

	/** 返回方法 */
	public String formatKey(String key) {
		return RegExUtils.replaceAll(key, "\\.", "_");
	}

	public void setDefault() {
		def = true;
	}

	public boolean isDefault() {
		return def;
	}

}
