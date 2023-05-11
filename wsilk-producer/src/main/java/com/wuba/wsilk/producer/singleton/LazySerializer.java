package com.wuba.wsilk.producer.singleton;

import static com.wuba.wsilk.codegen.Constants.*;

import java.io.IOException;
import java.lang.annotation.Annotation;

import com.wuba.wsilk.codegen.AnnotationMapValue;
import com.wuba.wsilk.codegen.JavaWriter;
import com.wuba.wsilk.codegen.Modifier;
import com.wuba.wsilk.codegen.model.SimpleType;
import com.wuba.wsilk.core.SourceEntityMeta;
import com.wuba.wsilk.core.WsilkConfiguration;

/**
 * 
 * LazySerializer
 * 
 * @author mindashuang
 */
public class LazySerializer extends CreateSerializer {

	@Override
	public void end(JavaWriter writer, SourceEntityMeta em) throws IOException {
		SimpleType inner = new SimpleType(INNER_CLASS_NAME);
		writer.beginClass(Modifier.Class.PRIVATE_STATIC, inner);
		writer.field(Modifier.Field.PRIVATE_STATIC_FINAL, em, NAME, NEW + em.getSimpleName() + BRAKETS);
		writer.end();
		super.end(writer, em);
	}

	private final static String INNER_CLASS_NAME = "InnerSingleton";

	public LazySerializer(WsilkConfiguration conf, Class<? extends Annotation> annClass, SingletonsSerializer parent,
			AnnotationMapValue annotationMapValue) {
		super(conf, annClass, parent, annotationMapValue);
	}

	public String defaultName() {
		return "Lazy";
	}

	@Override
	public void methods(JavaWriter writer, SourceEntityMeta em) throws IOException {
		writer.beginMethod(Modifier.Field.PUBLIC_STATIC, em, GET_NAME);
		writer.returns(INNER_CLASS_NAME + DOT + NAME);
		writer.end();
	}

}
