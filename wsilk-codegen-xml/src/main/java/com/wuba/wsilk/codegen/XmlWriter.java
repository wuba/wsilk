package com.wuba.wsilk.codegen;

import java.io.IOException;
import java.util.Stack;

/**
 * 写 xml
 */
public class XmlWriter extends AbstractCodeWriter<XmlWriter> implements CodeWriter<XmlWriter> {

	private static char INDENT_CHARS = '\t';
	private static char LEFT_CHARS = '<';
	private static char RIGHT_CHARS = '>';
	private static char END_CHARS = '/';

	private XmlSettings xmlSettings;

	private Stack<String> stack = new Stack<>();

	public XmlWriter(Appendable appendable) {
		this(appendable, new DefaultXmlSettings());
	}

	public XmlWriter(Appendable appendable, XmlSettings xmlSettings) {
		super(appendable, 0);
		this.xmlSettings = xmlSettings;
	}

	public XmlWriter startDocument() {
		// 写 <?xml version="1.0" encoding="utf-8" ?>
		return this;
	}

	public XmlWriter endDocument() {
		return this;
	}

	public XmlWriter beginNode(String nodeName, Attribute... attributes) throws IOException {
		return this;
	}

	public XmlWriter beginNode(String nodeName, String nodeValue, Attribute... attributes) throws IOException {
		return this;
	}

	public XmlWriter endNode() throws IOException {
		return this;
	}

	public XmlWriter cdata(String value) throws IOException {
		return this;
	}

	public XmlWriter comment(String value) throws IOException {
		return this;
	}

	public XmlWriter element(String elementName) throws IOException {
		String token = startElement(elementName);
		whitespace();
		append(token);
		append("/r/n");
		stack.push(elementName);
		return this;
	}

	public XmlWriter string(String name, String value) throws IOException {
		element(name);
		string(value);
		endElement();
		return this;
	}

	public XmlWriter string(String value) throws IOException {
		whitespace();
		// WriteWhitespace(1);
		append(value);
		append("/r/n");
		return this;
	}

	public XmlWriter endElement() throws IOException {
		if (!stack.isEmpty()) {
			String elementName = (String) stack.pop();
			String token = endElement(elementName);
			whitespace();
			append(token);
			append("/r/n");
		}
		return this;
	}

	private void whitespace(int count) throws IOException {
		for (int i = 0; i < count; i++) {
			append(INDENT_CHARS);
		}
	}

	private void whitespace() throws IOException {
		whitespace(stack.size());
	}

	private String startElement(String elementName) {
		return LEFT_CHARS + elementName + RIGHT_CHARS;
	}

	private String endElement(String elementName) {
		return LEFT_CHARS + END_CHARS + elementName + RIGHT_CHARS;
	}

}
