package com.wuba.wsilk.codegen;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class XmlSettings {

	private String encoding;

	private boolean indent;

	private char indentChars;

	private char newLineChars;

	private String newLineHandling;

	private boolean newLineOnAttributes;

	private boolean omitXmlDeclaration;

	private ConformanceLevel conformanceLevel;

	private String xmlLang;

	private String xmlSpace;

	public static enum ConformanceLevel {

		DOCUMENT,

		FRAGMENT,

		AUTO

	}
}
