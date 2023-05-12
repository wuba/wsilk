package com.wuba.wsilk.producer.meta;

import java.util.List;

import javax.lang.model.element.Element;
import javax.lang.model.type.TypeMirror;

import com.wuba.wsilk.codegen.model.Type;
import com.wuba.wsilk.core.SourceEntityMeta;

/**
 * 元数据资源
 * 
 * @author mindashuang
 */
public class MySourceEntityMeta extends SourceEntityMeta {

	private final static String SERIALIZABLE = "java.io.Serializable";

	public MySourceEntityMeta(Element element, Type type) {

		super(element, type);
	}

	/**
	 * 判断对象是否继承 Serializable
	 */
	public boolean isSerializable() {
		boolean serializable = false;
		List<? extends TypeMirror> inters = this.getOriginal().getTypeElement().getInterfaces();
		for (TypeMirror tm : inters) {
			if (tm.toString().startsWith(SERIALIZABLE)) {
				serializable = true;
			}
		}
		return serializable;
	}

}
