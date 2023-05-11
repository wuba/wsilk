package com.wuba.wsilk.producer.composite;

import static com.wuba.wsilk.codegen.Tokens.*;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.wuba.wsilk.codegen.JavaWriter;
import com.wuba.wsilk.codegen.MethodMeta;
import com.wuba.wsilk.codegen.Modifier;
import com.wuba.wsilk.codegen.model.ClassType;
import com.wuba.wsilk.codegen.model.Parameter;
import com.wuba.wsilk.codegen.model.Type;
import com.wuba.wsilk.codegen.model.Types;
import com.wuba.wsilk.codegen.spec.MethodElement;
import com.wuba.wsilk.core.SourceEntityMeta;
import com.wuba.wsilk.core.Support;
import com.wuba.wsilk.core.WsilkConfiguration;
import com.wuba.wsilk.core.serializer.java.AbstractSingleJavaSerializer;
import com.wuba.wsilk.core.tf.visitor.TypeVisitor;

/**
 * 
 * 组合模式
 * 
 * @author mindashuang
 */
@Support(value = Composite.class, order = 1, pkgInlcudeSuffix = false, override = false, suffix = "Composite")
public class CompositeSerializer extends AbstractSingleJavaSerializer<SourceEntityMeta> {

	private final static String COMPOSITE = "composite";

	public CompositeSerializer(WsilkConfiguration configuration, Class<? extends Annotation> annClass) {
		super(configuration, annClass);
	}

	/**
	 * 继承原始类
	 */
	@Override
	public Type getSuperClass(SourceEntityMeta t) throws IOException {
		return t.getOriginal();
	}

	@Override
	public void properties(JavaWriter writer, SourceEntityMeta t) throws IOException {
		// 创建一个集合
		writer.field(Modifier.Field.PRIVATE_FINAL, new ClassType(List.class, t.getOriginal()), COMPOSITE,
				"new ArrayList<>()");
	}

	@Override
	public void methods(JavaWriter writer, SourceEntityMeta t) throws IOException {
		// 添加
		writer.beginMethod(MethodElement.builder(t, "add", new Parameter("t", t.getOriginal())));
		writer.line(THIS, DOT, COMPOSITE, DOT, "add(t);");
		writer.returns();
		writer.end();

		// 删除
		writer.beginMethod(MethodElement.builder(t, "remove", new Parameter("t", t.getOriginal())));
		writer.line(THIS, DOT, COMPOSITE, DOT, "remove(t);");
		writer.returns();
		writer.end();
		// 获得索引
		writer.beginMethod(MethodElement.builder(t.getOriginal(), "getChild", new Parameter("index", Types.INT)));
		writer.line("if(index>=" + COMPOSITE + ".size()){");
		writer.line(SPACE, RETURN, THIS, DOT, COMPOSITE, DOT, "get(index);");
		writer.line("}else{");
		writer.line(SPACE, RETURN, "null", SEMICOLON);
		writer.line("}");
		writer.end();

		Set<MethodMeta> methodMetas = t.getOriginal().getAllMethod();
		for (MethodMeta methodMeta : methodMetas) {
			writer.beginMethod(MethodElement.builder(methodMeta.getReturnType(), methodMeta.getName(), methodMeta.getParameters()));
			// 所有lead 需要执行相同的方法
			writer.line(COMPOSITE, DOT, "forEach((e) -> {");
			writer.line(SPACE, "e", DOT, methodMeta.getName(), writer.packMethod(methodMeta.getParameters()),
					SEMICOLON);
			writer.line("})", SEMICOLON);

			/** 不返回 */
			if (methodMeta.getReturnType() == TypeVisitor.VOID_TYPE) {
				writer.superInvoke(methodMeta.getName(), methodMeta.getParameters());
			} else {
				writer.superInvokeReturn(methodMeta.getName(), methodMeta.getParameters());
			}
			writer.end();
		}
	}

	@Override
	public void importPackage(JavaWriter writer, SourceEntityMeta em) throws IOException {
		writer.importClasses(em.getFullName());
		writer.imports(ArrayList.class, List.class);
	}

}
