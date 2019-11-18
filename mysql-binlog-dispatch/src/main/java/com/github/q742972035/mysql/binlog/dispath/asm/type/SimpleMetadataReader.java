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

package com.github.q742972035.mysql.binlog.dispath.asm.type;


import com.github.q742972035.mysql.binlog.dispath.asm.SimpleAnnotationMetadataReadingVisitor;
import org.objectweb.asm.ClassReader;

import java.io.IOException;

/**
 * {@link MetadataReader} implementation based on an ASM
 *
 * @author Juergen Hoeller
 * @author Costin Leau
 * @since 2.5
 */
public final class SimpleMetadataReader implements MetadataReader {

	private static final int PARSING_OPTIONS = ClassReader.SKIP_DEBUG
			| ClassReader.SKIP_CODE | ClassReader.SKIP_FRAMES;


	private final AnnotationMetadata annotationMetadata;


	public SimpleMetadataReader(Class type) throws IOException {
		SimpleAnnotationMetadataReadingVisitor visitor = new SimpleAnnotationMetadataReadingVisitor(type.getClassLoader());
		new ClassReader(type.getClassLoader().getResourceAsStream(type.getName().replaceAll("\\.","/")+".class")).accept(visitor, PARSING_OPTIONS);
		this.annotationMetadata = visitor.getMetadata();
	}


	@Override
	public ClassMetadata getClassMetadata() {
		return this.annotationMetadata;
	}

	@Override
	public AnnotationMetadata getAnnotationMetadata() {
		return this.annotationMetadata;
	}

}
