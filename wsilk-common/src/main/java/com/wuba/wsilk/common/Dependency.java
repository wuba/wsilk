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

package com.wuba.wsilk.common;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import lombok.Getter;

/**
 * 依赖
 * 
 * @author mindashuang
 */

@Documented
@Repeatable(com.wuba.wsilk.common.Dependency.List.class)
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE })
public @interface Dependency {

	String groupId();

	String artifactId();

	String version();

	Type type() default Type.JAR;

	String classifier() default "";

	Scope scope() default Scope.RUNTIME;

	String systemPath() default "";

	Exclusion[] exclusions() default {};

	boolean optional() default false;

	@Target(ElementType.TYPE)
	@Retention(RetentionPolicy.RUNTIME)
	@Documented
	public @interface List {

		Dependency[] value();

	}

	@Getter
	static enum Scope {
		/** compile */
		COMPILE("compile"),
		/** provided */
		PROVIDED("provided"),
		/** runtime */
		RUNTIME("runtime"),
		/** test */
		TEST("test"),
		/** system */
		SYSTEM("system"),
		/** import */
		IMPORT("import");

		String name;

		Scope(String name) {
			this.name = name;
		}

		public String getName() {
			return name;
		}
	}

	@Getter
	static enum Type {
		/** pom */
		POM("pom"),
		/** war */
		WAR("war"),
		/** jar */
		JAR("jar"),
		/** test-jar */
		TEST_JAR("test-jar"),
		/** maven-plugin */
		MAVEN_PLUGIN("maven-plugin"),
		/** ejb */
		EJB("ejb"),
		/** ejb-client */
		EJB_CLIENT("ejb-client"),
		/** ear */
		EAR("ear"),
		/** rar */
		RAR("rar"),
		/** java-source */
		JAVA_SOURCE("java-source"),
		/** javadoc */
		JAVADOC("javadoc");

		String name;

		Type(String name) {
			this.name = name;
		}

		public String getName() {
			return name;
		}

	}

}
