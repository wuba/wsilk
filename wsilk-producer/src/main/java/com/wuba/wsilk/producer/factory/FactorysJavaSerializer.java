package com.wuba.wsilk.producer.factory;

import static com.wuba.wsilk.codegen.Constants.*;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.wuba.wsilk.codegen.AnnotationMapValue;
import com.wuba.wsilk.codegen.EntityMeta;
import com.wuba.wsilk.codegen.JavaWriter;
import com.wuba.wsilk.codegen.Modifier;
import com.wuba.wsilk.codegen.model.Parameter;
import com.wuba.wsilk.codegen.model.Type;
import com.wuba.wsilk.codegen.model.Types;
import com.wuba.wsilk.core.NoGenericException;
import com.wuba.wsilk.core.SourceEntityMeta;
import com.wuba.wsilk.core.Support;
import com.wuba.wsilk.core.WsilkConfiguration;
import com.wuba.wsilk.core.serializer.java.AbstractSingleJavaSerializer;
import com.wuba.wsilk.producer.factory.Factory.OptionType;

/**
 * 
 * spi
 * 
 * @author mindashuang
 */
@Support(value = Factory.class, order = 1, pkgInlcudeSuffix = false, parentPkg = false, override = true, suffix = "Factory")
public class FactorysJavaSerializer extends AbstractSingleJavaSerializer<SourceEntityMeta> {

	public FactorysJavaSerializer(WsilkConfiguration configuration, Class<? extends Annotation> annClass) {
		super(configuration, annClass);
	}

	private final static String FACTORY_METHOD = "create";

	private final static String PARAM = "option";

	private Set<EntityMeta> ems;

	String optionType;

	Boolean option4Constructor;

	private Map<String, String> optionValueMap = Maps.newHashMap();

	@Override
	public SourceEntityMeta init(SourceEntityMeta em) throws NoGenericException {
		AnnotationMapValue info = em.findAnnotation(getAnnotationClass());
		ems = Sets.newHashSet();
		option4Constructor = info.bool("constructorOptionValue", false);
		optionType = info.enumString();
		AnnotationMapValue[] annotationMapValues = info.annotations("options");
		for (AnnotationMapValue annotationMapValue : annotationMapValues) {
			EntityMeta entityMeta = this.getEntityTypeFactory()
					.createEntityMeta(annotationMapValue.getTypeMirror("bean"));
			ems.add(entityMeta);
			optionValueMap.put(entityMeta.getFullName(), annotationMapValue.string("optionValue"));
		}
		return super.init(em);
	}

	/**
	 * 导入依赖的包
	 */
	@Override
	public void importPackage(JavaWriter writer, SourceEntityMeta em) throws IOException {
		for (EntityMeta e : ems) {
			writer.importClasses(e.getFullName());
		}
	}

	/**
	 * 工程类
	 */
	@Override
	public void constructors(JavaWriter writer, SourceEntityMeta em) throws IOException {
		super.constructors(writer, em);
	}

	@Override
	public void methods(JavaWriter writer, SourceEntityMeta em) throws IOException {
		int length = ems.size();
		int index = 0;
		for (EntityMeta e : ems) {
			String optionValue = getOptionValue(e);
			/** 获得参数类型 */
			Type type = getType(optionType);
			if (index == 0) {
				writer.beginMethod(Modifier.Field.PUBLIC_STATIC, em.getOriginal(), FACTORY_METHOD,
						new Parameter(PARAM, type));
				writer.ifExp(param(optionValue, type), returns(e, option4Constructor));
			} else {
				writer.elseIfExp(param(optionValue, type), returns(e, option4Constructor));
			}
			index++;
			if (index == length) {
				writer.returnsNull();
				writer.end();
			}
		}
	}

	private String getOptionValue(EntityMeta e) {
		return optionValueMap.get(e.getFullName());
	}

	private String param(String optionValue, Type type) {
		if (type == Types.STRING) {
			return DOUBLE_QUOTATION + optionValue + DOUBLE_QUOTATION + ".equals" + method(PARAM);
		} else {
			return optionValue + "== " + PARAM;
		}
	}

	private String returns(EntityMeta e, Boolean option4Constructor) {
		String action = RETURN + NEW + e.getSimpleName();
		if (option4Constructor) {
			action += method(PARAM);
		} else {
			action += BRAKETS;
		}
		return action;
	}

	private Map<String, Type> typeMaps = getTypeMap();

	private Type getType(String optionType) {
		return typeMaps.get(optionType);
	}

	private Map<String, Type> getTypeMap() {
		Map<String, Type> map = Maps.newHashMap();
		map.put(OptionType.BOOLEAN.name(), Types.BOOLEAN);
		map.put(OptionType.BYTE.name(), Types.BYTE);
		map.put(OptionType.CHAR.name(), Types.CHAR);
		map.put(OptionType.DOUBLE.name(), Types.DOUBLE);
		map.put(OptionType.ENUM.name(), Types.ENUM);
		map.put(OptionType.FLOAT.name(), Types.FLOAT);
		map.put(OptionType.INTEGER.name(), Types.INTEGER);
		map.put(OptionType.LONG.name(), Types.LONG);
		map.put(OptionType.SHORT.name(), Types.SHORT);
		map.put(OptionType.STRING.name(), Types.STRING);
		return Collections.unmodifiableMap(map);
	}

}
