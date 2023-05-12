package com.wuba.wsilk.producer.wrapper;

import static com.wuba.wsilk.codegen.Tokens.*;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.List;
import java.util.Set;

import com.wuba.wsilk.codegen.JavaWriter;
import com.wuba.wsilk.codegen.MethodMeta;
import com.wuba.wsilk.codegen.Modifier;
import com.wuba.wsilk.codegen.model.Parameter;
import com.wuba.wsilk.codegen.model.Type;
import com.wuba.wsilk.codegen.model.TypeExtends;
import com.wuba.wsilk.codegen.model.Types;
import com.wuba.wsilk.core.SourceEntityMeta;
import com.wuba.wsilk.core.Support;
import com.wuba.wsilk.core.WsilkConfiguration;
import com.wuba.wsilk.core.serializer.java.AbstractSingleJavaSerializer;

/**
 * 
 * 组合模式
 * 
 * @author mindashuang
 */
@Support(value = Wrapper.class, order = 1, pkgInlcudeSuffix = false, override = true, suffix = "Wrapper")
public class WrapperSerializer extends AbstractSingleJavaSerializer<SourceEntityMeta> {

	private final static String DELEGATE = "delegate";

	private final static String RESULT = "result";

	private final static Type RETURN_TYPE = new TypeExtends(Types.VOID);

	public WrapperSerializer(WsilkConfiguration configuration, Class<? extends Annotation> annClass) {
		super(configuration, annClass);
	}

	@Override
	public void createJavaHead(JavaWriter writer, SourceEntityMeta t) throws IOException {
		writer.beginClass(Modifier.Class.PUBLIC_ABSTRACT, t, getSuperClass(t), getSuperInterface(t));
	}

	@Override
	public void properties(JavaWriter writer, SourceEntityMeta t) throws IOException {
		// 创建一个属性
		writer.field(Modifier.Field.PRIVATE, t.getOriginal(), DELEGATE);
	}

	@Override
	public List<Type> superInterface(SourceEntityMeta t) throws IOException {
		List<Type> superType = super.superInterface(t);
		if (t.getOriginal().isInterface()) {
			superType.add(t.getOriginal());
		}
		return superType;
	}

	@Override
	public Type getSuperClass(SourceEntityMeta t) throws IOException {
		if (!t.getOriginal().isInterface()) {
			return t.getOriginal();
		}
		return null;
	}

	@Override
	public void constructors(JavaWriter writer, SourceEntityMeta t) throws IOException {
		writer.beginConstructor(new Parameter(DELEGATE, t.getOriginal()));
		writer.assign(DELEGATE);
		writer.end();
	}

	@Override
	public void methods(JavaWriter writer, SourceEntityMeta t) throws IOException {
		writer.beginMethod(t.getOriginal(), "getDelegate");
		writer.returns(DELEGATE);
		writer.end();

		// 获得装饰的最下层
		writer.beginMethod(t.getOriginal(), "getLastDelegate");
		writer.line(t.getOriginal().getSimpleName(), SPACE, RESULT, EQ, THIS, DOT, DELEGATE, SEMICOLON);
		writer.line("while (", RESULT, " instanceof ", t.getSimpleName(), ") {");
		writer.line(SPACE, RESULT, EQ, "((", t.getSimpleName(), ") ", RESULT, ").getDelegate();");
		writer.line("}");
		writer.returns(RESULT);
		writer.end();

		// 拆开装饰模式
		writer.beginMethod(Modifier.Field.PUBLIC_STATIC, t.getOriginal(), "unwrap",
				new Parameter(DELEGATE, t.getOriginal()));
		writer.line("if (", DELEGATE, " instanceof ", t.getSimpleName(), ") {");
		writer.line(SPACE, RETURN, "((", t.getSimpleName(), ") ", DELEGATE, ").getLastDelegate();");
		writer.line("}");
		writer.returns(DELEGATE);
		writer.end();

		// 添加
		Set<MethodMeta> methodMetas = t.getOriginal().getAllMethod();
		for (MethodMeta methodMeta : methodMetas) {
			writer.beginMethod(methodMeta.getReturnType(), methodMeta.getName(), methodMeta.getParameters());
			// 所有lead 需要执行相同的方法
			Type type = methodMeta.getReturnType();
			if (RETURN_TYPE.equals(type)) {
				writer.line(SPACE, DELEGATE, DOT, methodMeta.getName(), writer.packMethod(methodMeta.getParameters()),
						SEMICOLON);
			} else {
				writer.line(RETURN, SPACE, DELEGATE, DOT, methodMeta.getName(),
						writer.packMethod(methodMeta.getParameters()), SEMICOLON);
			}
			writer.end();
		}
	}

	@Override
	public void importPackage(JavaWriter writer, SourceEntityMeta em) throws IOException {
		writer.importClasses(em.getFullName());
	}

}
