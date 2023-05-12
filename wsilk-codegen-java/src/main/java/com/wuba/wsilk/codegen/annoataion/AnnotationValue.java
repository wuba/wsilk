package com.wuba.wsilk.codegen.annoataion;

import org.apache.commons.text.StringEscapeUtils;

import static com.wuba.wsilk.common.Symbols.*;

import java.lang.annotation.Annotation;

import lombok.EqualsAndHashCode;
import lombok.Getter;

/**
 * 注解中的值
 * 
 * @author mindashuang
 */
@EqualsAndHashCode
public class AnnotationValue {

	/**
	 * 值
	 */
	@Getter
	private Object value;

	/**
	 * 类型
	 * 
	 */
	private ValueType type;

	public AnnotationValue(Object value) {
		if (value instanceof String) {
			type = ValueType.STRING;
			String escaped = StringEscapeUtils.escapeJava(String.valueOf(value));
			this.value = QUOTE + escaped.replace("\\/", "/") + QUOTE;
		} else if (value instanceof Integer || value instanceof Long || value instanceof Double
				|| value instanceof Float) {
			type = ValueType.SIMPLE;
			this.value = String.valueOf(value);
		} else if (value instanceof Character) {
			type = ValueType.CHARACTER;
			this.value = '\'' + String.valueOf(value) + '\'';
		} else if (value instanceof Class<?>) {
			type = ValueType.CLASS;
			this.value = String.valueOf(value);
		} else if (value instanceof Enum<?>) {
			type = ValueType.EUNM_OR_ANNOTATION;
			Enum<?> enumValue = (Enum<?>) value;
			this.value = enumValue.name();
		} else if (value instanceof Annotation) {
			type = ValueType.EUNM_OR_ANNOTATION;
			this.value = String.valueOf(value);
		}
	}

	public AnnotationValue(Object value, ValueType type) {
		this.value = value;
		this.type = type;
	}

	public boolean isArray() {
		return type == ValueType.ARRAY;
	}

	public boolean isClass() {
		return type == ValueType.CLASS;
	}

	public boolean isType() {
		return type == ValueType.EUNM_OR_ANNOTATION;
	}

	public boolean isSimple() {
		return type == ValueType.SIMPLE;
	}

	public boolean isChar() {
		return type == ValueType.CHARACTER;
	}

	public boolean isString() {
		return type == ValueType.STRING;
	}

	public static enum ValueType {
		/** 字符串 */
		STRING,
		/** 字符 */
		CHARACTER,
		/** 类 */
		CLASS,
		/** 枚举或者注解 */
		EUNM_OR_ANNOTATION,
		/** 数组 */
		ARRAY,
		/** 简单类型 */
		SIMPLE,

		;

	}

	public String getSimpleValue() {
		String v = String.valueOf(value);
		if (type == ValueType.CLASS) {
			v = v + ".class";
		}
		return v;
	}

}
