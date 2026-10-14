package org.example.generator;

import java.lang.annotation.Annotation;
import java.lang.reflect.*;
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
            case WildcardType w -> of(
                    w.getLowerBounds().length > 0 ? w.getLowerBounds()[0] : w.getUpperBounds()[0], annotations
            );
            case GenericArrayType g -> {
                ResolvedType component = of(g.getGenericComponentType(), annotations);
                Class<?> arrayClass = Array.newInstance(component.raw(), 0).getClass();
                yield new ResolvedType(arrayClass, List.of(component), annotations);
            }
            case TypeVariable<?> v -> of(v.getBounds()[0], annotations);
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
        boolean known = index < arguments.size() && arguments.get(index).raw() != Object.class;
        return known ? arguments.get(index) : new ResolvedType(defaultValue, List.of(), List.of());
    }

    public ResolvedType componentType() {
        if (!arguments.isEmpty()) {
            return arguments.getFirst();
        }
        return of(raw.getComponentType());
    }
}
