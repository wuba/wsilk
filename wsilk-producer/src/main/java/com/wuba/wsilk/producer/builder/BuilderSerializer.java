package com.wuba.wsilk.producer.builder;

import static com.wuba.wsilk.codegen.Constants.*;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;

import com.wuba.wsilk.codegen.JavaWriter;
import com.wuba.wsilk.codegen.Modifier;
import com.wuba.wsilk.codegen.PropertyMeta;
import com.wuba.wsilk.codegen.model.ClassType;
import com.wuba.wsilk.codegen.model.Parameter;
import com.wuba.wsilk.core.NoGenericException;
import com.wuba.wsilk.core.SourceEntityMeta;
import com.wuba.wsilk.core.Support;
import com.wuba.wsilk.core.WsilkConfiguration;
import com.wuba.wsilk.core.serializer.java.AbstractSingleJavaSerializer;

/**
 * 
 * Builder 的生成器
 * 
 * @author mindashuang
 */
@Support(value = Builder.class, suffix = "Builder", override = false)
public class BuilderSerializer extends AbstractSingleJavaSerializer<SourceEntityMeta> {

	/** 拿到原始类的类型 */
	private ClassType bean;

	private String name;

	public BuilderSerializer(WsilkConfiguration configuration, Class<? extends Annotation> annClass) {
		super(configuration, annClass);
	}

	@Override
	public SourceEntityMeta init(SourceEntityMeta em) throws NoGenericException {
		// 拿到原始类的类型
		bean = new ClassType(em.getJavaClass());
		// 拿到原始类的名字
		name = StringUtils.uncapitalize(bean.getSimpleName());
		return super.init(em);
	}

	@Override
	public void constructors(JavaWriter writer, SourceEntityMeta em) throws IOException {
		// 创建一个私有构造器
		writer.beginConstructor(Modifier.Field.PRIVATE);
		writer.line(THIS, DOT, name, EQ, NEW, em.getOriginal().getSimpleName(), BRAKETS, SEMICOLON);
		writer.end();
	}

	@Override
	public void methods(JavaWriter writer, SourceEntityMeta em) throws IOException {// 重写所有方法
		// 写一个静态create的方法
		writer.beginMethod(Modifier.Field.PUBLIC_STATIC, em, "create");
		String javaName = em.getSimpleName();
		String proxName = StringUtils.uncapitalize(javaName);
		writer.line(javaName, SPACE, proxName, EQ, NEW, javaName, BRAKETS, SEMICOLON);
		writer.line(RETURN, proxName, SEMICOLON);
		writer.end();

		// 遍历元素数据的所有属性，生成对应的方法
		Set<PropertyMeta> propertyMetas = em.getOriginal().getAllProperties();
		if (propertyMetas != null) {
			for (PropertyMeta propertyMeta : propertyMetas) {
				writer.beginMethod(em, propertyMeta.getName(),
						new Parameter(propertyMeta.getName(), propertyMeta.getType()));
				writer.line(THIS, DOT, name, DOT, SET, StringUtils.capitalize(propertyMeta.getName()), "(",
						propertyMeta.getName(), ");");
				writer.line(RETURN, THIS, SEMICOLON);
				writer.end();
			}
		}
	}

	@Override
	public void properties(JavaWriter writer, SourceEntityMeta em) throws IOException {
		// 生成一个私有遍历
		writer.field(Modifier.Field.PRIVATE, bean, name);
	}

	@Override
	public void importPackage(JavaWriter writer, SourceEntityMeta em) throws IOException {
		// 导入原始数据的对象全名 getOriginal() 是获得注解上的原数据对象
		writer.importClasses(em.getOriginal().getFullName());
	}

}