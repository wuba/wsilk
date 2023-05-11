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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.lang.model.type.ArrayType;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.ErrorType;
import javax.lang.model.type.ExecutableType;
import javax.lang.model.type.NoType;
import javax.lang.model.type.NullType;
import javax.lang.model.type.PrimitiveType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.type.TypeVariable;
import javax.lang.model.type.WildcardType;

import com.wuba.wsilk.codegen.Tokens;
import com.wuba.wsilk.common.Symbols;

/**
 * 获得 EntityMeta 的名字
 * 
 * @author mindashuang
 */
public class NameVisitor extends SimpleTypeVisitorAdapter<List<String>, Boolean> {

	private final List<String> defaultValue = Collections.singletonList("Object");

	private final List<String> voidValue = Collections.singletonList("Void");

	private List<String> visitBase(TypeMirror t) {
		List<String> rv = new ArrayList<String>();
		String name = t.toString();
		if (name.contains(Tokens.LT)) {
			name = name.substring(0, name.indexOf(Tokens.LT));
		}
		rv.add(name);
		return rv;
	}

	@Override
	public List<String> visitPrimitive(PrimitiveType t, Boolean p) {
		return Collections.singletonList(t.toString());
	}

	@Override
	public List<String> visitNull(NullType t, Boolean p) {
		return defaultValue;
	}

	@Override
	public List<String> visitArray(ArrayType t, Boolean p) {
		List<String> rv = new ArrayList<String>(visit(t.getComponentType()));
		rv.add(Symbols.ARRAY);
		return rv;
	}

	@Override
	public List<String> visitDeclared(DeclaredType t, Boolean p) {
		List<String> rv = visitBase(t);
		for (TypeMirror arg : t.getTypeArguments()) {
			if (p != null && p == true) {
				rv.addAll(visit(arg, false));
			} else {
				rv.add(arg.toString());
			}
		}
		return rv;
	}

	@Override
	public List<String> visitError(ErrorType t, Boolean p) {
		return visitDeclared(t, p);
	}

	@Override
	public List<String> visitTypeVariable(TypeVariable t, Boolean p) {
		List<String> rv = visitBase(t);
		if (t.getUpperBound() != null) {
			rv.addAll(visit(t.getUpperBound(), p));
		}
		if (t.getLowerBound() != null) {
			rv.addAll(visit(t.getLowerBound(), p));
		}
		return rv;
	}

	@Override
	public List<String> visitWildcard(WildcardType t, Boolean p) {
		List<String> rv = visitBase(t);
		if (t.getExtendsBound() != null) {
			rv.addAll(visit(t.getExtendsBound(), p));
		}
		if (t.getSuperBound() != null) {
			rv.addAll(visit(t.getSuperBound(), p));
		}
		return rv;
	}

	@Override
	public List<String> visitExecutable(ExecutableType t, Boolean p) {
		throw new IllegalStateException();
	}

	@Override
	public List<String> visitNoType(NoType t, Boolean p) {
		if (t.getKind() == TypeKind.PACKAGE) {
			return visitBase(t);
		} else if (t.getKind() == TypeKind.VOID) {
			return voidValue;
		}
		return defaultValue;
	}

}
