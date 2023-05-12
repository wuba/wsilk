package com.wuba.wsilk.codegen;

/**
 * 常量
 * 
 * @author mindashuang
 */
public class Constants extends Tokens {

	/** 私有修饰符 */
	public static final String PRIVATE_FINAL = statement(PRIVATE, FINAL);

	public static final String PRIVATE_STATIC = statement(PRIVATE, STATIC);

	public static final String PRIVATE_STATIC_FINAL = statement(PRIVATE, STATIC, FINAL);

	public static final String PRIVATE_ABSTRACT = statement(PRIVATE, ABSTRACT);

	/** 保护 */
	public static final String PROTECTED_FINAL = statement(PROTECTED, FINAL);

	public static final String PROTECTED_STATIC = statement(PROTECTED, STATIC);

	public static final String PROTECTED_STATIC_FINAL = statement(PROTECTED, STATIC, FINAL);

	public static final String PROTECTED_ABSTRACT = statement(PROTECTED, ABSTRACT);

	/** 公共修饰符 */
	public static final String PUBLIC_FINAL = statement(PUBLIC, FINAL);

	public static final String PUBLIC_STATIC = statement(PUBLIC, STATIC);

	public static final String PUBLIC_STATIC_FINAL = statement(PUBLIC, STATIC, FINAL);

	public static final String PUBLIC_ABSTRACT = statement(PUBLIC, ABSTRACT);

	/** 私有类 */
	public static final String PRIVATE_CLASS = statement(CLASS);

	public static final String PRIVATE_ABSTRACT_CLASS = statement(PRIVATE, ABSTRACT, CLASS);

	public static final String PRIVATE_STATIC_CLASS = statement(PRIVATE, STATIC, CLASS);

	/** 保护 */
	public static final String PROTECTED_CLASS = statement(PROTECTED, CLASS);

	public static final String PROTECTED_ABSTRACT_CLASS = statement(PROTECTED, ABSTRACT, CLASS);

	public static final String PROTECTED_STATIC_CLASS = statement(PROTECTED, STATIC, CLASS);

	/** 公共类 */
	public static final String PUBLIC_CLASS = statement(PUBLIC, CLASS);

	public static final String PUBLIC_ABSTRACT_CLASS = statement(PUBLIC, ABSTRACT, CLASS);

	public static final String PUBLIC_STATIC_CLASS = statement(PUBLIC, STATIC, CLASS);

	public static final String PUBLIC_INTERFACE = statement(PUBLIC, INTERFACE);

	public static final String PROTECTED_INTERFACE = statement(PROTECTED, INTERFACE);

	/** 静态导入 */
	public static final String IMPORT_STATIC = statement(IMPORT, STATIC);

	public static final String BRAKETS = LPAREN + RPAREN;

	public static final String SET = "set";

	public static final String GET = "get";

	private final static String statement(String... modifys) {
		String statement = EMPTY;
		for (String modify : modifys) {
			statement += modify + SPACE;
		}
		return statement;
	}

	public static final String method(String param) {
		return LPAREN + param + RPAREN;
	}

	public static final String newBean(String name) {
		return SPACE + NEW + name + BRAKETS;
	}

}
