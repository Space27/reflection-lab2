package org.example.generator;

import java.lang.annotation.Annotation;
import java.lang.reflect.Parameter;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public record ResolvedType(Class<?> raw, List<ResolvedType> arguments, List<Annotation> annotations) {

    public static ResolvedType of(Type type) {
        return of(type, List.of());
    }

    public static ResolvedType of(Type type, List<Annotation> annotations) {
        return switch (type) {
            case Class<?> c -> new ResolvedType(c, List.of(), annotations);
            case ParameterizedType p -> new ResolvedType(
                    (Class<?>) p.getRawType(),
                    Arrays.stream(p.getActualTypeArguments()).map(ResolvedType::of).toList(),
                    annotations
            );
            default -> throw new GenerationException("Type does not supported: " + type.getTypeName());
        };
    }

    public static ResolvedType of(Parameter parameter) {
        return of(parameter.getParameterizedType(), List.of(parameter.getAnnotations()));
    }

    public <T extends Annotation> Optional<T> getAnnotation(Class<T> annotationClass) {
        return annotations.stream()
                .filter(annotationClass::isInstance)
                .map(annotationClass::cast)
                .findFirst();
    }

    public ResolvedType argumentOrDefault(int index, Class<?> defaultValue) {
        return index < arguments.size() ? arguments.get(index) : new ResolvedType(defaultValue, List.of(), List.of());
    }

    public ResolvedType componentType() {
        return of(raw.getComponentType());
    }
}
