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

import static com.wuba.wsilk.common.Symbols.*;

import java.lang.annotation.Annotation;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;

import org.apache.commons.lang3.StringUtils;

import com.google.common.collect.Lists;

import com.wuba.wsilk.codegen.AnnotationMapValue;
import com.wuba.wsilk.codegen.model.SimpleType;
import com.wuba.wsilk.codegen.model.Type;
import com.wuba.wsilk.codegen.model.TypeCategory;
import com.wuba.wsilk.codegen.model.TypeExtends;
import com.wuba.wsilk.codegen.model.Types;
import com.wuba.wsilk.common.Symbols;
import com.wuba.wsilk.core.WsilkConfiguration;
import com.wuba.wsilk.core.tf.visitor.NameVisitor;
import com.wuba.wsilk.core.tf.visitor.TypeVisitor;
import com.wuba.wsilk.core.utils.AstReflectUtils;
import com.wuba.wsilk.codegen.model.TypeThis;

import lombok.Getter;
import lombok.Setter;

/**
 * 
 * 类型解释器
 * 
 * @author mindashuang
 */
public abstract class AbstractTypeFactory extends TypeFactory {

	@Getter
	private TypeMapper typeMapper = TypeMapper.getInstance();

	@Setter
	@Getter
	private Class<? extends Annotation> annotation;

	/**
	 * 处理注解信息
	 */
	private AnnotationTypeFactory annotationTypeFactory;

	protected final Type defaultType = new TypeExtends(Types.OBJECT);

	protected final TypeMirror objectType, numberType, comparableType, collectionType, setType, listType, mapType;

	public AbstractTypeFactory(WsilkConfiguration configuration) {
		super(configuration);
		this.annotationTypeFactory = new AnnotationTypeFactory(configuration, this);
		this.objectType = getErasedType(Object.class);
		this.numberType = getErasedType(Number.class);
		this.comparableType = getErasedType(Comparable.class);
		this.collectionType = getErasedType(Collection.class);
		this.listType = getErasedType(List.class);
		this.setType = getErasedType(Set.class);
		this.mapType = getErasedType(Map.class);
	}

	protected TypeMirror getErasedType(Class<?> clazz) {
		return typeUtils.erasure(elementUtils.getTypeElement(clazz.getName()).asType());
	}

	/** 名字访问器 */
	public NameVisitor getNameVisitor() {
		return new NameVisitor();
	}

	/**
	 * 生成元数据类型
	 * 
	 * @param typeMirror TypeMirror
	 * 
	 * @return 返回类型
	 */
	protected abstract Type createEntityMeta(TypeMirror typeMirror);

	/**
	 * 生成元数据类型
	 * 
	 * @param asType   TypeMirror
	 * 
	 * @param enumType Type
	 * 
	 * @return 返回类型
	 */
	protected abstract Type createEntityMeta(TypeMirror asType, Type enumType);

	/**
	 * 类型访问器
	 * 
	 * @return 返回类型访问器
	 */
	public abstract TypeVisitor getTypeVisitor();

	/**
	 * entitMeta 的名字
	 */
	public List<String> name(TypeMirror typeMirror) {
		return getNameVisitor().visit(typeMirror);
	}

	/**
	 * entitMeta 的名字
	 */
	public List<String> name(Element element) {
		return name(element.asType());
	}

	/**
	 * 获得Type
	 */
	public Type type(TypeMirror typeMirror) {
		return getTypeVisitor().visit(typeMirror);
	}

	public Type type(Element element) {
		return type(element.asType());
	}

	/**
	 * 获得 Type
	 */
	public Type getType(TypeMirror typeMirror) {
		return typeMapper.getType(typeMirror, this::name, this::type);
	}

	public Type getType(TypeElement typeElement) {
		return getType(typeElement.asType());
	}

	public TypeElement getTypeElement(String clsName) {
		/**
		 * 解决内部类的问题
		 */
		clsName = clsName.replace(DOLLAR, DOT);
		return processingEnv.getElementUtils().getTypeElement(clsName);
	}

	public TypeElement getTypeElement(Class<?> cls) {
		return getTypeElement(cls.getName());
	}

	@SuppressWarnings("unused")
	private Type createType(TypeMirror typeMirror, List<String> key, boolean deep) {
		Type type = getTypeVisitor().visit(typeMirror, deep);
		boolean canEntity = type != null
				&& (type.getCategory() == TypeCategory.ENTITY || type.getCategory() == TypeCategory.CUSTOM);
		if (canEntity) {
			return createEntityMeta(typeMirror);
		} else {
			return type;
		}
	}

	private Type createType(TypeElement typeElement, TypeCategory category, List<? extends TypeMirror> typeArgs) {
		String name = typeElement.getQualifiedName().toString();
		String simpleName = typeElement.getSimpleName().toString();
		String packageName = elementUtils.getPackageOf(typeElement).getQualifiedName().toString();
		Type[] params = new Type[typeArgs.size()];
		/** 存在死循环 */
		for (int i = 0; i < params.length; i++) {
			TypeMirror typeMirror = typeArgs.get(i);
			/** 如果是这个类型 */
			if (typeMirror.getKind() == TypeKind.TYPEVAR) {
				/** 拿到存类型 */
				TypeMirror upper = AstReflectUtils.getUpperBound(typeMirror);
				String upperName = upper.toString();
				/** 清理掉泛型内容 */
				if (upperName.endsWith(Symbols.GT)) {
					upperName = upperName.substring(0, upperName.lastIndexOf(Symbols.LT));
				}
				/** 防止死循环，如果泛型类和本类相同 */
				if (name.equals(upperName)) {
					typeMirror = (TypeMirror) upper;
					params[i] = new TypeThis();
				} else {
					params[i] = getType(typeMirror);
				}
			} else {
				params[i] = getType(typeMirror);
			}
		}
		return new SimpleType(category, name, packageName, simpleName, false,
				typeElement.getModifiers().contains(Modifier.FINAL), params);

	}

	public Type createEnumType(DeclaredType declaredType, TypeElement typeElement) {
		Type enumType = createType(typeElement, TypeCategory.ENUM, declaredType.getTypeArguments());
		Class<? extends Annotation> entityAnn = getAnnotation();
		if (typeElement.getAnnotation(entityAnn) != null) {
			return createEntityMeta(typeElement.asType(), enumType);
		}
		return enumType;
	}

	public Type createInterfaceType(DeclaredType declaredType, TypeElement typeElement) {
		Iterator<? extends TypeMirror> i = declaredType.getTypeArguments().iterator();
		if (isAssignable(declaredType, mapType)) {
			return createMapType(i);

		} else if (isAssignable(declaredType, listType)) {
			return createCollectionType(Types.LIST, i);

		} else if (isAssignable(declaredType, setType)) {
			return createCollectionType(Types.SET, i);

		} else if (isAssignable(declaredType, collectionType)) {
			return createCollectionType(Types.COLLECTION, i);

		} else {
			String name = typeElement.getQualifiedName().toString();
			return createType(typeElement, TypeCategory.get(name), declaredType.getTypeArguments());
		}

	}

	private final static String JAVA_PREFIX = "java.";

	public Type createClassType(DeclaredType declaredType, TypeElement typeElement) {

		String name = typeElement.getQualifiedName().toString();

		if (name.startsWith(JAVA_PREFIX)) {
			Iterator<? extends TypeMirror> i = declaredType.getTypeArguments().iterator();

			if (isAssignable(declaredType, mapType)) {
				return createMapType(i);

			} else if (isAssignable(declaredType, listType)) {

				return createCollectionType(Types.LIST, i);

			} else if (isAssignable(declaredType, setType)) {
				return createCollectionType(Types.SET, i);

			} else if (isAssignable(declaredType, collectionType)) {
				return createCollectionType(Types.COLLECTION, i);
			}
		}

		TypeCategory typeCategory = TypeCategory.get(name);

		if (typeCategory != TypeCategory.NUMERIC && isAssignable(typeElement.asType(), comparableType)
				&& isSubType(typeElement.asType(), numberType)) {
			typeCategory = TypeCategory.NUMERIC;

		} else if (!typeCategory.isSubCategoryOf(TypeCategory.COMPARABLE)
				&& isAssignable(typeElement.asType(), comparableType)) {
			typeCategory = TypeCategory.COMPARABLE;

		} else if (typeCategory == TypeCategory.SIMPLE) {
			typeCategory = TypeCategory.ENTITY;
		}

		List<? extends TypeMirror> arguments = declaredType.getTypeArguments();

		if (StringUtils.EMPTY.equals(name)) {
			TypeMirror type = objectType;
			if (typeCategory == TypeCategory.COMPARABLE) {
				type = comparableType;
			}
			List<? extends TypeMirror> superTypes = typeUtils.directSupertypes(declaredType);
			for (TypeMirror superType : superTypes) {
				if (typeUtils.isSubtype(superType, type)) {
					type = superType;
				}
			}
			typeElement = (TypeElement) typeUtils.asElement(type);
			if (type instanceof DeclaredType) {
				arguments = ((DeclaredType) type).getTypeArguments();
			}
		}

		return createType(typeElement, typeCategory, arguments);
	}

	/**
	 * collection Type
	 */
	private Type createCollectionType(Type baseType, Iterator<? extends TypeMirror> typeMirrors) {
		if (!typeMirrors.hasNext()) {
			return new SimpleType(baseType, defaultType);
		}
		Type componentType = getType(typeMirrors.next());
		if (componentType == null) {
			componentType = defaultType;
		} else if (componentType.getParameters().isEmpty()) {
			TypeElement element = elementUtils.getTypeElement(componentType.getFullName());
			if (element != null) {
				Type type = getType(element.asType());
				if (!type.getParameters().isEmpty()) {
					componentType = new SimpleType(componentType, new Type[type.getParameters().size()]);
				}
			}
		}
		return new SimpleType(baseType, componentType);
	}

	public Type createPackageType(Object t) {
		PackageElement element;
		if (t != null && t instanceof TypeMirror) {
			element = (PackageElement) AstReflectUtils.asElement((TypeMirror) t);
			String fullName = element.getQualifiedName().toString() + ".";
			String packageName = elementUtils.getPackageOf(element).getQualifiedName().toString();
			List<Type> list = Lists.newArrayList();
			return new SimpleType(TypeCategory.CUSTOM, fullName, packageName, StringUtils.EMPTY, false, false, list);
		}
		return null;
	}

	/**
	 * map Type
	 */
	private Type createMapType(Iterator<? extends TypeMirror> typeMirrors) {
		if (!typeMirrors.hasNext()) {
			return new SimpleType(Types.MAP, defaultType, defaultType);
		}

		Type keyType = getType(typeMirrors.next());
		if (keyType == null) {
			keyType = defaultType;
		}

		Type valueType = getType(typeMirrors.next());
		if (valueType == null) {
			valueType = defaultType;
		} else if (valueType.getParameters().isEmpty()) {
			TypeElement element = elementUtils.getTypeElement(valueType.getFullName());
			if (element != null) {
				Type type = getType(element.asType());
				if (!type.getParameters().isEmpty()) {
					valueType = new SimpleType(valueType, new Type[type.getParameters().size()]);
				}
			}
		}
		return new SimpleType(Types.MAP, keyType, valueType);
	}

	public Element toElement(TypeMirror typeMirror) {
		return AstReflectUtils.asElement(typeMirror);
	}

	private boolean isAssignable(TypeMirror type, TypeMirror iface) {
		return typeUtils.isAssignable(type, iface) || typeUtils.erasure(type).toString().equals(iface.toString());
	}

	private boolean isSubType(TypeMirror type1, TypeMirror clazz) {
		return typeUtils.isSubtype(type1, clazz) || typeUtils.directSupertypes(type1).contains(clazz);
	}

	/**
	 * 获得注解
	 */
	protected Map<String, AnnotationMapValue> getAnnotation(Element e) {
		return annotationTypeFactory.create(e);
	}

}
