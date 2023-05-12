package com.wuba.wsilk.producer.config;

import static com.wuba.wsilk.codegen.Constants.*;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.Objects;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;

import com.google.common.collect.Sets;
import com.wuba.wsilk.codegen.JavaWriter;
import com.wuba.wsilk.codegen.Modifier;
import com.wuba.wsilk.codegen.model.Parameter;
import com.wuba.wsilk.codegen.model.Types;
import com.wuba.wsilk.core.NoGenericException;
import com.wuba.wsilk.core.SourceEntityMeta;
import com.wuba.wsilk.core.WsilkConfiguration;
import com.wuba.wsilk.core.serializer.java.AbstractJavaSerializerDecorator;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * 
 * 
 * 生成bundle
 * 
 * @author mindashuang
 */
public class BundleSerializer extends AbstractJavaSerializerDecorator<SourceEntityMeta, JavaWriter, ConfigSerializer> {

	@Getter
	private Set<Names> keys = Sets.newHashSet();

	private final String uName;

	public BundleSerializer(WsilkConfiguration conf, Class<? extends Annotation> annClass, ConfigSerializer parent) {
		super(conf, annClass, parent);
		uName = StringUtils.upperCase(getParent().getJavaName());
	}

	@Override
	public void createJavaHead(JavaWriter writer, SourceEntityMeta t) throws IOException {
		writer.mergeAnnotation();
		// 创建自己的类
		writer.beginClass(Modifier.Class.PUBLIC_ABSTRACT, t, getSuperClass(t), getSuperInterface(t));
	}

	@Override
	public void properties(JavaWriter writer, SourceEntityMeta t) throws IOException {
		writer.field(Modifier.Field.PRIVATE_STATIC, t, uName, "null");
	}

	@Override
	public void methods(JavaWriter writer, SourceEntityMeta t) throws IOException {
		for (Names names : keys) {
			writer.beginMethod(Modifier.Field.PUBLIC_ABSTRACT, Types.STRING, names.abstractName);
		}
		// 新增一个抽象发放

		writer.beginMethod(Modifier.Field.PUBLIC_STATIC, Types.VOID, "set" + getParent().getJavaName(),
				new Parameter("key", Types.STRING));
		boolean first = true;
		BundlesSerializer defaultBundle = null;
		for (BundlesSerializer bundlesSerializer : getParent().getBundlesSerializers()) {
			if (!bundlesSerializer.isDefault()) {
				String express = DOUBLE_QUOTATION + bundlesSerializer.getName() + DOUBLE_QUOTATION + ".equals(key)";
				String action = uName + EQ + NEW + bundlesSerializer.getBean().getSimpleName() + BRAKETS;
				if (first) {
					writer.ifExp(express, uName + EQ + NEW + bundlesSerializer.getBean().getSimpleName() + BRAKETS);
					first = false;
				} else {
					writer.elseIfExp(express, action);
				}
			} else {
				defaultBundle = bundlesSerializer;
			}
		}
		if (defaultBundle != null) {
			writer.elseExp(uName + EQ + NEW + defaultBundle.getBean().getSimpleName() + BRAKETS);
		}
		writer.end();

		// 第二个方法
		writer.beginMethod(Modifier.Field.PUBLIC_STATIC, t, "get");
		writer.returns(uName);
		writer.end();
		// 属性方法

		for (Names names : keys) {
			writer.beginMethod(Modifier.Field.PUBLIC_STATIC_FINAL, Types.STRING, names.staticName);
			writer.line(RETURN, uName, DOT, names.abstractName, BRAKETS, SEMICOLON);
			writer.end();
		}
	}

	@Override
	public SourceEntityMeta init(SourceEntityMeta em) throws NoGenericException {
		em.setJavaName(getParent().getJavaName());
		return super.init(em);
	}

	public void addKey(String key) {
		keys.add(new Names(key, key.toUpperCase()));
	}

	@Getter
	@Setter
	@AllArgsConstructor
	static class Names {

		private String abstractName;

		private String staticName;

		@Override
		public int hashCode() {
			return Objects.hash(abstractName);
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj) {
				return true;
			}
			if (obj == null) {
				return false;
			}
			if (getClass() != obj.getClass()) {
				return false;
			}
			Names other = (Names) obj;
			return Objects.equals(abstractName, other.abstractName);
		}

	}

}
