package com.wuba.wsilk.producer.mapper;

import com.google.common.collect.Sets;
import com.wuba.wsilk.codegen.AnnotationMapValue;
import com.wuba.wsilk.codegen.JavaWriter;
import com.wuba.wsilk.codegen.Modifier;
import com.wuba.wsilk.codegen.PropertyMeta;
import com.wuba.wsilk.codegen.model.ClassType;
import com.wuba.wsilk.codegen.model.Parameter;
import com.wuba.wsilk.codegen.model.Type;
import com.wuba.wsilk.core.*;
import com.wuba.wsilk.core.serializer.java.AbstractSingleJavaSerializer;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 
 * 两个对象字段影射
 * 
 * @author mindashuang
 */

@SuppressWarnings({ "unchecked", "rawtypes" })
@Support(value = Mapper.class, suffix = "Mapper")
public class MapperSerializer extends AbstractSingleJavaSerializer<SourceEntityMeta> {

	private boolean classCastInit = false;

	private final Map<Class<?>, Set<Class<?>>> classCastMap = new HashMap<>();

	public MapperSerializer(WsilkConfiguration configuration, Class<? extends Annotation> annClass) {
		super(configuration, annClass);
	}

	SourceEntityMeta target;

	@Override
	public SourceEntityMeta init(SourceEntityMeta em) {
		AnnotationMapValue info = em.findAnnotation(Mapper.class);
 		target = this.getEntityTypeFactory().createEntityMeta(info.type());
		if (!classCastInit) {
			registerClassCastMap();
		}
		return super.init(em);
	}

	@Override
	public void methods(JavaWriter writer, SourceEntityMeta em) throws IOException {
		this.cloneMethod().clone(writer, em.getOriginal(), target);
	}

	private CloneConsumer<JavaWriter, SourceEntityMeta, SourceEntityMeta> cloneMethod() {
		return ((writer, src, des) -> {
			try {
				ClassType srcCt = new ClassType(src.getJavaClass());
				ClassType desCt = new ClassType(des.getJavaClass());
				String srcName = "src";
				String desName = "des";

				writer.beginMethod(Modifier.Field.PUBLIC_STATIC, new ClassType(void.class), "copy",
						new Parameter(srcName, srcCt), new Parameter(desName, desCt));
				writer.line("    if(", srcName, " != null && ", desName, " != null) { ");

				Set<PropertyMeta> allProperties = src.getAllProperties();

				Map<String, PropertyMeta> desMap = des.getAllProperties().stream()
						.collect(Collectors.toMap(PropertyMeta::getName, Function.identity(), (k1, k2) -> k1));

				if (allProperties != null) {

					for (PropertyMeta f : allProperties) {

						if (desMap.containsKey(f.getName()) && cast(f.getType(), desMap.get(f.getName()).getType())) {
							writer.line(fieldSimpleCopy(srcName, desName, f, desMap.get(f.getName())));
						}
					}
				}

				writer.line("    }");
				writer.end();
			} catch (IOException e) {
				e.printStackTrace();
			}
		});
	}

	private boolean cast(Type src, Type des) {
		Class srcClz = src.getJavaClass();
		Class desClz = des.getJavaClass();
		if (srcClz == desClz) {
			return true;
		}
		if (classCastMap.containsKey(srcClz) && classCastMap.get(srcClz).contains(desClz)) {
			return true;
		}
		return isInstanceof(srcClz, desClz);
	}

	private boolean isInstanceof(Class c, Class p) {
		if (c == p) {
			return true;
		}
		if (c.isAssignableFrom(p)) {
			return true;
		}
		if (c != Object.class) {
			SourceEntityMeta em = getEntityTypeFactory().createEntityMeta(c.getSuperclass());
			if (em != null) {
				return isInstanceof(em.getJavaClass(), p);
			}
		}
		return false;
	}

	private String fieldSimpleCopy(String src, String des, PropertyMeta fsrc, PropertyMeta fdes) {
		return new StringBuilder("    ").append(des).append(".").append(fieldStName(fdes)).append("(").append(src)
				.append(".").append(fieldGetName(fsrc)).append("());").toString();
	}

	private String fieldGetName(PropertyMeta f) {
		String prefix = f.getType().getJavaClass().equals(boolean.class) ? "is" : "get";
		return prefix + upperFirstChar(f.getName());
	}

	private String upperFirstChar(String name) {
		String str1 = name.substring(0, 1);
		String str2 = name.substring(1);
		return str1.toUpperCase() + str2;
	}

	private String fieldStName(PropertyMeta f) {
		return "set" + upperFirstChar(f.getName());
	}

	private void registerClassCastMap() {
		// Widening Casting (automatically) - converting a smaller type to a larger type
		// size
		// byte-> short-> char-> int-> long-> float->double
		// Primitive type Wrapper class
		// boolean Boolean
		// byte Byte
		// short Short
		// char Character
		// int Integer
		// long Long
		// float Float
		// double Double

		classCastMap.put(boolean.class, Sets.newHashSet(Boolean.class, boolean.class));
		classCastMap.put(byte.class, Sets.newHashSet(Byte.class, byte.class));
		classCastMap.put(short.class, Sets.newHashSet(Short.class, short.class, byte.class));
		classCastMap.put(char.class, Sets.newHashSet(Character.class, char.class, short.class, byte.class));
		classCastMap.put(int.class, Sets.newHashSet(Integer.class, int.class, char.class, short.class, byte.class));
		classCastMap.put(long.class,
				Sets.newHashSet(Long.class, long.class, int.class, char.class, short.class, byte.class));
		classCastMap.put(float.class,
				Sets.newHashSet(Float.class, float.class, long.class, int.class, char.class, short.class, byte.class));
		classCastMap.put(double.class, Sets.newHashSet(Double.class, double.class, float.class, long.class, int.class,
				char.class, short.class, byte.class));

		classCastMap.put(Boolean.class, Sets.newHashSet(boolean.class, Boolean.class));
		classCastMap.put(Byte.class, Sets.newHashSet(byte.class, Byte.class));
		classCastMap.put(Short.class, Sets.newHashSet(short.class, Short.class));
		classCastMap.put(Character.class, Sets.newHashSet(char.class, Character.class));
		classCastMap.put(Integer.class, Sets.newHashSet(int.class, Integer.class));
		classCastMap.put(Long.class, Sets.newHashSet(long.class, Long.class));
		classCastMap.put(Float.class, Sets.newHashSet(float.class, Float.class));
		classCastMap.put(Double.class, Sets.newHashSet(double.class, Double.class));
		classCastInit = true;
	}

	/**
	 * 克隆对象
	 * 
	 * @author mindashuang
	 */
	@FunctionalInterface
	public static interface CloneConsumer<W, C, M> {
		/**
		 * 克隆
		 * 
		 * @param w 输出
		 * @param c 来源
		 * @param m
		 */
		void clone(W w, C c, M m);
	}

	@Override
	public void importPackage(JavaWriter writer, SourceEntityMeta em) throws IOException {
		Class src = em.getOriginal().getJavaClass();
		writer.imports(src);
		Class des = target.getJavaClass();
		if (src != des) {
			writer.imports(des);
		}
	}

}
