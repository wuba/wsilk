package com.wuba.wsilk.producer.singleton;

import static com.wuba.wsilk.codegen.Constants.*;

import java.io.IOException;
import java.lang.annotation.Annotation;

import com.wuba.wsilk.codegen.AnnotationMapValue;
import com.wuba.wsilk.codegen.JavaWriter;
import com.wuba.wsilk.codegen.Modifier;
import com.wuba.wsilk.core.SourceEntityMeta;
import com.wuba.wsilk.core.WsilkConfiguration;

/**
 * 
 * Eager 的问题
 * 
 * @author mindashuang
 */
public class EagerSerializer extends CreateSerializer {

	public EagerSerializer(WsilkConfiguration conf, Class<? extends Annotation> annClass, SingletonsSerializer parent,
			AnnotationMapValue annotationMapValue) {
		super(conf, annClass, parent, annotationMapValue);
	}

	@Override
	public void properties(JavaWriter writer, SourceEntityMeta em) throws IOException {
		writer.field(Modifier.Field.PRIVATE_STATIC_FINAL, em, NAME, NEW + em.getSimpleName() + BRAKETS);
	}

	@Override
	public String defaultName() {
		return "Eager";
	}

	@Override
	public void methods(JavaWriter writer, SourceEntityMeta em) throws IOException {
		writer.beginMethod(Modifier.Field.PUBLIC_STATIC, em, GET_NAME);
		writer.returns(NAME);
		writer.end();
	}

}
