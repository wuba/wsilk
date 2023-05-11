package com.wuba.wsilk.producer.prototype;

import static com.wuba.wsilk.codegen.Tokens.*;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

import org.apache.commons.lang3.StringUtils;

import com.wuba.wsilk.codegen.AnnotationMapValue;
import com.wuba.wsilk.codegen.JavaWriter;
import com.wuba.wsilk.codegen.Modifier;
import com.wuba.wsilk.codegen.model.ClassType;
import com.wuba.wsilk.codegen.model.Parameter;
import com.wuba.wsilk.core.NoGenericException;
import com.wuba.wsilk.core.SourceEntityMeta;
import com.wuba.wsilk.core.Support;
import com.wuba.wsilk.core.WsilkConfiguration;
import com.wuba.wsilk.core.serializer.java.AbstractSingleJavaSerializer;

/**
 * 
 * 原型模式
 * 
 * @author mindashuang
 */
@Support(value = Prototype.class, order = 1, pkgInlcudeSuffix = false, override = false, suffix = "Prototype")
public class PrototypeSerializer extends AbstractSingleJavaSerializer<SourceEntityMeta> {

	private final static String PROTOTYPE = "prototype";

	private final static String CLONE_METHOD = "cloneMethod";

	Boolean deep;

	public PrototypeSerializer(WsilkConfiguration configuration, Class<? extends Annotation> annClass) {
		super(configuration, annClass);
	}

	@Override
	public SourceEntityMeta init(SourceEntityMeta em) throws NoGenericException {
		AnnotationMapValue info = em.findAnnotation(getAnnotationClass());
		deep = info.bool();
		return super.init(em);
	}

	@Override
	public void createJavaHead(JavaWriter writer, SourceEntityMeta t) throws IOException {
		writer.beginClass(t, getSuperClass(t), getSuperInterface(t));
	}

	@Override
	public void properties(JavaWriter writer, SourceEntityMeta t) throws IOException {
		if (deep) {
			writer.field(Modifier.Field.PRIVATE_FINAL, new ClassType(ByteArrayOutputStream.class),
					"byteArrayOutputStream");
		} else {
			// 创建一个属性
			writer.field(Modifier.Field.PRIVATE_FINAL, t.getOriginal(), PROTOTYPE);
			writer.field(Modifier.Field.PRIVATE_FINAL, new ClassType(Method.class), CLONE_METHOD);
		}
	}

	@Override
	public void constructors(JavaWriter writer, SourceEntityMeta t) throws IOException {
		writer.beginConstructor(new Parameter(PROTOTYPE, t.getOriginal()));
		if (deep) {
			writer.line("byteArrayOutputStream = new ByteArrayOutputStream();");
			writer.line(
					"try (ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream)) {");
			writer.line("	objectOutputStream.writeObject(", PROTOTYPE, ");");
			writer.line("	objectOutputStream.flush();");
		} else {
			writer.assign(PROTOTYPE);
			writer.line("try {");
			writer.line(CLONE_METHOD, " = Object.class.getDeclaredMethod(\"clone\");");
			writer.line(SPACE, CLONE_METHOD, ".setAccessible(true);");
		}
		writer.line("} catch (Exception e) {");
		writer.line("	throw new RuntimeException(e);");
		writer.line("}");
		writer.end();
	}

	@Override
	public void methods(JavaWriter writer, SourceEntityMeta t) throws IOException {

		String name = StringUtils.uncapitalize(t.getOriginal().getSimpleName());
		writer.beginMethod(t.getOriginal(), "get");
		writer.line(t.getOriginal().getSimpleName(), SPACE, name, EQ, NULL, SEMICOLON);
		writer.line("try {");
		if (deep) {
			writer.line(
					"ObjectInputStream objectInputStream = new ObjectInputStream(new ByteArrayInputStream(byteArrayOutputStream.toByteArray()));");
			writer.line(SPACE, name, EQ, "(", t.getOriginal().getSimpleName(), ")", "objectInputStream.readObject();");
		} else {
			writer.line(SPACE, name, EQ, "(", t.getOriginal().getSimpleName(), ")", CLONE_METHOD, ".invoke(", PROTOTYPE,
					");");
		}
		writer.line("} catch (Exception e) {");
		writer.line("	throw new RuntimeException(e);");
		writer.line("}");
		writer.returns(name);
		writer.end();

	}

	@Override
	public void importPackage(JavaWriter writer, SourceEntityMeta em) throws IOException {
		writer.importClasses(em.getFullName());
		if (deep) {
			writer.importPackages("java.io");
		} else {
			writer.imports(Method.class);
		}
	}

}
