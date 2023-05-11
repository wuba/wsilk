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

package com.wuba.wsilk.core.tf.visitor;

import javax.lang.model.element.TypeElement;
import javax.lang.model.type.ArrayType;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.ErrorType;
import javax.lang.model.type.ExecutableType;
import javax.lang.model.type.NoType;
import javax.lang.model.type.NullType;
import javax.lang.model.type.PrimitiveType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeVariable;
import javax.lang.model.type.WildcardType;

import com.wuba.wsilk.codegen.model.Type;
import com.wuba.wsilk.codegen.model.TypeExtends;
import com.wuba.wsilk.codegen.model.TypeSuper;
import com.wuba.wsilk.codegen.model.Types;
import com.wuba.wsilk.core.tf.AbstractEntityTypeFactory;

/**
 * 得到类型
 * 
 * @author mindashuang
 */
public class TypeVisitor extends SimpleTypeVisitorAdapter<Type, Boolean> {

	private AbstractEntityTypeFactory<?> entityTypeFactory;

	public TypeVisitor(AbstractEntityTypeFactory<?> entityTypeFactory) {
		this.entityTypeFactory = entityTypeFactory;
	}

	public final static Type DEFAULT_TYPE = new TypeExtends(Types.OBJECT);

	public final static Type VOID_TYPE = new TypeExtends(Types.VOID);

	@Override
	public Type visitPrimitive(PrimitiveType primitiveType, Boolean p) {
		switch (primitiveType.getKind()) {
		case BOOLEAN:
			return Types.BOOLEAN_P;
		case BYTE:
			return Types.BYTE_P;
		case SHORT:
			return Types.SHORT_P;
		case INT:
			return Types.INT;
		case LONG:
			return Types.LONG_P;
		case CHAR:
			return Types.CHAR;
		case FLOAT:
			return Types.FLOAT_P;
		case DOUBLE:
			return Types.DOUBLE_P;
		default:
			return null;
		}
	}

	private Type getPrimitive(PrimitiveType primitiveType) {
		switch (primitiveType.getKind()) {
		case BOOLEAN:
			return Types.BOOLEAN_P;
		case BYTE:
			return Types.BYTE_P;
		case SHORT:
			return Types.SHORT_P;
		case INT:
			return Types.INT;
		case LONG:
			return Types.LONG_P;
		case CHAR:
			return Types.CHAR;
		case FLOAT:
			return Types.FLOAT_P;
		case DOUBLE:
			return Types.DOUBLE_P;
		default:
			return null;
		}
	}

	@Override
	public Type visitDeclared(DeclaredType declaredType, Boolean p) {
		if (declaredType.asElement() instanceof TypeElement) {
			TypeElement typeElement = (TypeElement) declaredType.asElement();
			switch (typeElement.getKind()) {
			case ENUM:
				return entityTypeFactory.createEnumType(declaredType, typeElement);
			case ANNOTATION_TYPE:
				/** 注解类型 */
			case CLASS:
				return entityTypeFactory.createClassType(declaredType, typeElement);
			case INTERFACE:
				return entityTypeFactory.createInterfaceType(declaredType, typeElement);
			default:
				throw new IllegalArgumentException("Illegal type " + typeElement);
			}
		} else {
			throw new IllegalArgumentException("Unsupported element type " + declaredType.asElement());
		}
	}

	@Override
	public Type visitNull(NullType nullType, Boolean p) {
		throw new IllegalStateException();
	}

	@Override
	public Type visitArray(ArrayType arrayType, Boolean p) {
		if (arrayType.getComponentType() instanceof PrimitiveType) {
			Type type = getPrimitive((PrimitiveType) arrayType.getComponentType());
			if (type != null) {
				return type.asArrayType();
			}
		}
		return visit(arrayType.getComponentType(), p).asArrayType();
	}

	@Override
	public Type visitError(ErrorType errorType, Boolean p) {
		return visitDeclared(errorType, p);
	}

	@Override
	public Type visitTypeVariable(TypeVariable typeVariable, Boolean p) {
		String varName = typeVariable.toString();
		if (typeVariable.getUpperBound() != null) {
			Type type = visit(typeVariable.getUpperBound(), p);
			return new TypeExtends(varName, type);
		} else if (typeVariable.getLowerBound() != null && !(typeVariable.getLowerBound() instanceof NullType)) {
			Type type = visit(typeVariable.getLowerBound(), p);
			return new TypeSuper(varName, type);
		} else {
			return null;
		}
	}

	@Override
	public Type visitWildcard(WildcardType wildcardType, Boolean p) {
		if (wildcardType.getExtendsBound() != null) {
			Type type = visit(wildcardType.getExtendsBound(), p);
			return new TypeExtends(type);
		} else if (wildcardType.getSuperBound() != null) {
			Type type = visit(wildcardType.getSuperBound(), p);
			return new TypeSuper(type);
		} else {
			return null;
		}
	}

	@Override
	public Type visitExecutable(ExecutableType t, Boolean p) {
		throw new IllegalStateException();
	}

	@Override
	public Type visitNoType(NoType t, Boolean p) {
		if (t.getKind() == TypeKind.PACKAGE) {
			return entityTypeFactory.createPackageType(t);
		} else if (t.getKind() == TypeKind.VOID) {
			return VOID_TYPE;
		}
		return DEFAULT_TYPE;
	}

}
