package org.example.generator;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.List;

public record ResolvedType(Class<?> raw, List<ResolvedType> arguments) {

    public static ResolvedType of(Type type) {
        return switch (type) {
            case Class<?> c -> new ResolvedType(c, List.of());
            case ParameterizedType p -> new ResolvedType(
                    (Class<?>) p.getRawType(),
                    Arrays.stream(p.getActualTypeArguments()).map(ResolvedType::of).toList()
            );
            default -> throw new RuntimeException("Type does not supported: " + type.getTypeName());
        };
    }

    public ResolvedType argumentOrDefault(int index, Class<?> defaultValue) {
        return index < arguments.size() ? arguments.get(index) : new ResolvedType(defaultValue, List.of());
    }

    public ResolvedType componentType() {
        return of(raw.getComponentType());
    }
}
