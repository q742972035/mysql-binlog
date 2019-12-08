/*
 * Copyright 2002-2019 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.github.q742972035.mysql.binlog.dispatch.asm.type;


import com.github.q742972035.mysql.binlog.dispatch.asm.annotation.MergedAnnotations;
import org.objectweb.asm.Opcodes;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * {@link AnnotationMetadata} created from a
 * {@link SimpleAnnotationMetadataReadingVisitor}.
 *
 * @author Phillip Webb
 * @since 5.2
 */
public final class SimpleAnnotationMetadata implements AnnotationMetadata {

	private final String className;

	private final int access;

	
	private final String enclosingClassName;

	
	private final String superClassName;

	private final boolean independentInnerClass;

	private final String[] interfaceNames;

	private final String[] memberClassNames;

	private final MethodMetadata[] annotatedMethods;

	private final MergedAnnotations annotations;

	
	private Set<String> annotationTypes;


	public SimpleAnnotationMetadata(String className, int access,  String enclosingClassName,
                              String superClassName, boolean independentInnerClass, String[] interfaceNames,
                             String[] memberClassNames, MethodMetadata[] annotatedMethods, MergedAnnotations annotations) {

		this.className = className;
		this.access = access;
		this.enclosingClassName = enclosingClassName;
		this.superClassName = superClassName;
		this.independentInnerClass = independentInnerClass;
		this.interfaceNames = interfaceNames;
		this.memberClassNames = memberClassNames;
		this.annotatedMethods = annotatedMethods;
		this.annotations = annotations;
	}

	@Override
	public String getClassName() {
		return this.className;
	}

	@Override
	public boolean isInterface() {
		return (this.access & Opcodes.ACC_INTERFACE) != 0;
	}

	@Override
	public boolean isAnnotation() {
		return (this.access & Opcodes.ACC_ANNOTATION) != 0;
	}

	@Override
	public boolean isAbstract() {
		return (this.access & Opcodes.ACC_ABSTRACT) != 0;
	}

	@Override
	public boolean isFinal() {
		return (this.access & Opcodes.ACC_FINAL) != 0;
	}

	@Override
	public boolean isIndependent() {
		return (this.enclosingClassName == null || this.independentInnerClass);
	}

	@Override
	
	public String getEnclosingClassName() {
		return this.enclosingClassName;
	}

	@Override
	
	public String getSuperClassName() {
		return this.superClassName;
	}

	@Override
	public String[] getInterfaceNames() {
		return this.interfaceNames.clone();
	}

	@Override
	public String[] getMemberClassNames() {
		return this.memberClassNames.clone();
	}

	@Override
	public Set<String> getAnnotationTypes() {
		Set<String> annotationTypes = this.annotationTypes;
		if (annotationTypes == null) {
			annotationTypes = Collections.unmodifiableSet(
					AnnotationMetadata.super.getAnnotationTypes());
			this.annotationTypes = annotationTypes;
		}
		return annotationTypes;
	}

	@Override
	public Set<MethodMetadata> getAnnotatedMethods(String annotationName) {
		Set<MethodMetadata> annotatedMethods = null;
		for (int i = 0; i < this.annotatedMethods.length; i++) {
			if (this.annotatedMethods[i].isAnnotated(annotationName)) {
				if (annotatedMethods == null) {
					annotatedMethods = new LinkedHashSet<>(4);
				}
				annotatedMethods.add(this.annotatedMethods[i]);
			}
		}
		return annotatedMethods != null ? annotatedMethods : Collections.emptySet();
	}

	@Override
	public MergedAnnotations getAnnotations() {
		return this.annotations;
	}



}
