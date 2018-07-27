/*
 * Copyright 2017-2018 original authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.micronaut.core.annotation;

import java.lang.annotation.Annotation;

/**
 * An interface for a type that provides {@link AnnotationMetadata}.
 *
 * @author Graeme Rocher
 * @since 1.0
 */
public interface AnnotationMetadataProvider extends AnnotationSource {

    /**
     * Supplies the metadata. Defaults to {@link AnnotationMetadata#EMPTY_METADATA}.
     *
     * @return The {@link AnnotationMetadata}
     */
    default AnnotationMetadata getAnnotationMetadata() {
        return AnnotationMetadata.EMPTY_METADATA;
    }

    @Override
    default <T extends Annotation> T synthesize(Class<T> annotationClass) {
        return getAnnotationMetadata().synthesize(annotationClass);
    }

    @Override
    default Annotation[] synthesizeAll() {
        return getAnnotationMetadata().synthesizeAll();
    }

    @Override
    default Annotation[] synthesizeDeclared() {
        return getAnnotationMetadata().synthesizeDeclared();
    }

    @Override
    default boolean isAnnotationPresent(Class<? extends Annotation> annotationClass) {
        return getAnnotationMetadata().isAnnotationPresent(annotationClass);
    }

    @Override
    default boolean isDeclaredAnnotationPresent(Class<? extends Annotation> annotationClass) {
        return getAnnotationMetadata().isDeclaredAnnotationPresent(annotationClass);
    }

}
