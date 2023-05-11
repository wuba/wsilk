/*
 * Copyright (C) 2005-present, 58.com.  All rights reserved.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.wuba.wsilk.core.tf;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.util.ElementFilter;

import javax.lang.model.type.*;

import com.wuba.wsilk.codegen.EntityMeta;
import com.wuba.wsilk.codegen.Meta;
import com.wuba.wsilk.codegen.MethodMeta;
import com.wuba.wsilk.codegen.PropertyMeta;
import com.wuba.wsilk.codegen.model.Parameter;
import com.wuba.wsilk.codegen.model.Type;
import com.wuba.wsilk.core.WsilkConfiguration;
import com.wuba.wsilk.core.tf.visitor.TypeVisitor;

import com.wuba.wsilk.codegen.AnnotationMapValue;

import lombok.Getter;

/**
 * 
 * 解析模型所用
 * 
 * @author mindashuang
 */
@SuppressWarnings("unchecked")
public abstract class AbstractEntityTypeFactory<T extends EntityMeta> extends AbstractTypeFactory {

	@Getter
	private EntityMetaTypeMapper entityMetaType = EntityMetaTypeMapper.getInstance();

	private final Class<T> targetEntityMetaClass;

	public AbstractEntityTypeFactory(WsilkConfiguration configuration, Class<T> targetEntityMetaClass) {
		super(configuration);
		this.targetEntityMetaClass = targetEntityMetaClass;
	}

	/** 获得类型访问器 */
	public TypeVisitor getTypeVisitor() {
		return new TypeVisitor(this);
	}

	/**
	 * 获得 EntityType
	 */
	public T getEntityMeta(Element element) {
		return entityMetaType.getType(element, targetEntityMetaClass, this::name, this::entityMeta);
	}

	/**
	 * 初始化 EntityType
	 */
	public void init(Element element) {
	}

	public TypeElement getTypeElement(Class<?> cls) {
		return getTypeElement(cls.getName());
	}

	public T createEntityMeta(Type type) {
		return createEntityMeta(getTypeElement(type.getJavaClass()));
	}

	public T createEntityMeta(TypeMirror typeMirror) {
		return createEntityMeta(typeMirror, getType(typeMirror));
	}

	public T createEntityMeta(TypeMirror typeMirror, Type type) {
		T t = (T) getConfiguration().getInstanceUtils().createInstance(targetEntityMetaClass, toElement(typeMirror),
				type);
		// 如果是类
		if (typeMirror instanceof DeclaredType) {

			DeclaredType declaredType = (DeclaredType) typeMirror;
			TypeElement e = (TypeElement) declaredType.asElement();
			List<? extends Element> elements = e.getEnclosedElements();

			// 获得属性
			handlePropertyMeta(t, elements);

			// 获得方法
			handleMethodMeta(t, elements);

			// 注册父类
			for (Type superType : getSupertypes(typeMirror, false)) {
				t.addSuperType(superType, createEntityMeta(superType));
			}
			// 注册接口
			for (Type interfaceType : getSupertypes(typeMirror, true)) {
				t.addInterfaceType(interfaceType, createEntityMeta(interfaceType));
			}

		}
		Map<String, AnnotationMapValue> annotationMap = getAnnotation(toElement(typeMirror));
		t.setAnnotations(annotationMap);
		return t;
	}

	/**
	 * 根据已知类来获得信息
	 */
	public T createEntityMeta(Class<?> cls) {
		return createEntityMeta(getTypeElement(cls));
	}

	public T createEntityMeta(DeclaredType classType) {
		return createEntityMeta((TypeElement) classType.asElement());
	}

	/**
	 * 通过 TypeElement 获得信息
	 */
	public T createEntityMeta(Element element) {
		TypeMirror typeMirror = element.asType();
		return createEntityMeta(typeMirror, getType(typeMirror));
	}

	/**
	 * 创建
	 */
	public T entityMeta(Element element) {
		if (element instanceof TypeElement) {
			return entityMeta((TypeElement) element);
		}
		return createEntityMeta(element);
	}

	/**
	 * 创建
	 */
	public T entityMeta(TypeElement element) {
		return createEntityMeta(element);
	}

	public boolean isBlockedElement(Element e) {
		return e.getModifiers().contains(Modifier.TRANSIENT) || e.getModifiers().contains(Modifier.STATIC);
	}

	public boolean isValidElement(Element e) {
		return !e.getModifiers().contains(Modifier.TRANSIENT) && !e.getModifiers().contains(Modifier.STATIC);
	}

	/**
	 * 处理方法
	 */
	public void handleMethodMeta(T t, List<? extends Element> elements) {
		handleElement(elements, (e) -> {
			return ElementFilter.methodsIn(elements);
		}, (f) -> {
			ExecutableElement e = (ExecutableElement) f.getValue();
			Parameter[] parameters = null;
			List<? extends VariableElement> variableElements = e.getParameters();
			if (variableElements != null && variableElements.size() > 0) {
				int size = variableElements.size();
				parameters = new Parameter[size];
				for (int i = 0; i < size; i++) {
					VariableElement variableElement = variableElements.get(i);
					parameters[i] = new Parameter(variableElement.getSimpleName().toString(),
							getType(variableElement.asType()));
				}
			}
			Type[] thrownType = null;
			// 异常
			List<? extends TypeMirror> typeMirrors = e.getThrownTypes();
			if (typeMirrors != null && typeMirrors.size() > 0) {
				int size = typeMirrors.size();
				thrownType = new Type[size];
				for (int i = 0; i < size; i++) {
					thrownType[i] = getType(typeMirrors.get(i));
				}
			}
			TypeMirror reTypeMirror = e.getReturnType();
			MethodMeta methodMeta = new MethodMeta(t, f.getKey(), getType(reTypeMirror), parameters, thrownType);
			t.addMethod(methodMeta);
			return methodMeta;
		});
	}

	/**
	 * 
	 * 属性处理
	 * 
	 */
	public void handlePropertyMeta(T t, List<? extends Element> elements) {
		handleElement(elements, (e) -> {
			return ElementFilter.fieldsIn(elements);
		}, (f) -> {
			PropertyMeta propertyMeta = new PropertyMeta(t, f.getKey(), getType(f.getValue().asType()));
			t.addProperty(propertyMeta);
			return propertyMeta;
		});
	}

	/**
	 * 处理field 和 method
	 */
	private void handleElement(List<? extends Element> elements,
			Function<List<? extends Element>, List<? extends Element>> eFunction,
			Function<Map.Entry<String, Element>, Meta> biFunction) {
		LinkedHashMap<String, Element> typeMap = new LinkedHashMap<String, Element>();
		Map<String, Map<String, ? extends AnnotationMapValue>> annotations = new HashMap<String, Map<String, ? extends AnnotationMapValue>>(
				8);
		for (Element element : eFunction.apply(elements)) {
			String name = element.getSimpleName().toString();
			if (isValidElement(element)) {
				annotations.put(name, getAnnotation(element));
				typeMap.put(name, element);
			}
		}
		for (Map.Entry<String, Element> entry : typeMap.entrySet()) {
			Meta meta = biFunction.apply(entry);
			meta.setAnnotations(annotations.get(entry.getKey()));
		}
	}

	static String java = "java";

	private Set<Type> getSupertypes(TypeMirror typeMirror, boolean inter) {
		Set<Type> superTypes = Collections.emptySet();
		typeMirror = normalize(typeMirror);
		if (typeMirror.getKind() == TypeKind.DECLARED) {
			TypeElement e = (TypeElement) toElement(typeMirror);
			// class
			if (e.getKind() == ElementKind.CLASS) {
				if (e.getSuperclass().getKind() != TypeKind.NONE) {
					if (!inter) {
						TypeMirror supertype = normalize(e.getSuperclass());
						Type superClass = getType(supertype);
						if (superClass == null) {
							error("Got no type for " + supertype);
						} else if (!superClass.getFullName().startsWith(java)) {
							superTypes = Collections.singleton(getType(supertype));
						}
					} else {
						superTypes = interfaceType(e);
					}
				} else {
					info(e.toString());
				}
			} else {
				superTypes = interfaceType(e);
			}
		} else {
			return Collections.emptySet();
		}
		return superTypes;
	}

	private Set<Type> interfaceType(TypeElement e) {
		Set<Type> superTypes = new LinkedHashSet<Type>(e.getInterfaces().size());
		for (TypeMirror interMirror : e.getInterfaces()) {
			Type interType = getType(interMirror);
			if (!interType.getFullName().startsWith(java)) {
				superTypes.add(interType);
			}
		}
		return superTypes;
	}

	private TypeMirror normalize(TypeMirror type) {
		if (type.getKind() == TypeKind.TYPEVAR) {
			TypeVariable typeVar = (TypeVariable) type;
			if (typeVar.getUpperBound() != null) {
				return typeVar.getUpperBound();
			}
		} else if (type.getKind() == TypeKind.WILDCARD) {
			WildcardType wildcard = (WildcardType) type;
			if (wildcard.getExtendsBound() != null) {
				return wildcard.getExtendsBound();
			}
		}
		return type;
	}

}
