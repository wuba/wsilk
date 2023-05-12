package com.wuba.wsilk.codegen;

/**
 * 修饰符
 * 
 * @author mindashuang
 */
public class Modifier {

	public static enum Field {

		/** 私有修饰符 */

		PRIVATE(Constants.PRIVATE + Constants.SPACE),

		PRIVATE_FINAL(Constants.PRIVATE_FINAL),

		PRIVATE_STATIC(Constants.PRIVATE_STATIC),

		PRIVATE_STATIC_FINAL(Constants.PRIVATE_STATIC_FINAL),

		/** 保护 */

		PROTECTED(Constants.PROTECTED + Constants.SPACE),

		PROTECTED_FINAL(Constants.PROTECTED_FINAL),

		PROTECTED_STATIC(Constants.PROTECTED_STATIC),

		PROTECTED_STATIC_FINAL(Constants.PROTECTED_STATIC_FINAL),

		PROTECTED_ABSTRACT(Constants.PROTECTED_ABSTRACT),

		/** 公共修饰符 */

		PUBLIC(Constants.PUBLIC + Constants.SPACE),

		PUBLIC_FINAL(Constants.PUBLIC_FINAL),

		PUBLIC_STATIC(Constants.PUBLIC_STATIC),

		PUBLIC_STATIC_FINAL(Constants.PUBLIC_STATIC_FINAL),

		PUBLIC_ABSTRACT(Constants.PUBLIC_ABSTRACT);

		String name;

		Field(String name) {
			this.name = name;
		}
	}

	public static enum Class {

		/** 私有类 */
		PRIVATE(Constants.PRIVATE_CLASS),

		PRIVATE_ABSTRACT(Constants.PRIVATE_ABSTRACT_CLASS),

		PRIVATE_STATIC(Constants.PRIVATE_STATIC_CLASS),

		/** 保护 */
		PROTECTED(Constants.PROTECTED_CLASS),

		PROTECTED_ABSTRACT(Constants.PROTECTED_ABSTRACT_CLASS),

		PROTECTED_STATIC(Constants.PROTECTED_STATIC_CLASS),

		/** 公共类 */
		PUBLIC(Constants.PUBLIC_CLASS),

		PUBLIC_ABSTRACT(Constants.PUBLIC_ABSTRACT_CLASS),

		PUBLIC_STATIC(Constants.PUBLIC_STATIC_CLASS),

		/** 接口 */
		PUBLIC_INTERFACE(Constants.PUBLIC_INTERFACE),

		PROTECTED_INTERFACE(Constants.PROTECTED_INTERFACE);

		String name;

		Class(String name) {
			this.name = name;
		}
	}
}
