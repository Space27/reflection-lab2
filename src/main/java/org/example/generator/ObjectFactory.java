package org.example.generator;

import org.example.generator.typehandler.*;

import java.lang.invoke.MethodType;
import java.lang.reflect.Array;
import java.util.*;

public class ObjectFactory {

    private final List<TypeHandler> typeHandlers;

    private static final int MAX_RECURSION_DEPTH = 5;

    public ObjectFactory() {
        typeHandlers = new ArrayList<>();
        typeHandlers.add(new PrimitiveTypeHandler());
        typeHandlers.add(new StringTypeHandler());
        typeHandlers.add(new EnumTypeHandler());
        typeHandlers.add(new ImplementationTypeHandler());
        typeHandlers.add(new ClassTypeHandler());
    }

    public <T> T create(Class<T> clazz) {
        return wrap(clazz).cast(create(ResolvedType.of(clazz), MAX_RECURSION_DEPTH));
    }

    public Object create(ResolvedType type, int remainLoops) {
        TypeHandler handler = findHandler(type);
        if (remainLoops <= 0 && handler.isRecursive()) {
            return defaultValue(type.raw());
        }

        return handler.create(type, this, remainLoops - 1);
    }

    private TypeHandler findHandler(ResolvedType type) {
        return typeHandlers.stream().filter(typeHandler -> typeHandler.supports(type.raw()))
                .findFirst()
                .orElseThrow(() -> new RuntimeException(type.raw().getSimpleName() + " does not supported"));
    }

    private static Object defaultValue(Class<?> type) {
        if (type.isArray()) return Array.newInstance(type.getComponentType(), 0);
        if (type == List.class || type == Collection.class) return List.of();
        if (type == Set.class) return Set.of();
        if (type == Map.class) return Map.of();
        return null;
    }

    @SuppressWarnings("unchecked")
    private static <T> Class<T> wrap(Class<T> type) {
        return (Class<T>) MethodType.methodType(type).wrap().returnType();
    }
}
