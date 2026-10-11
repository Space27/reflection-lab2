package org.example.generator.typehandler;

import org.example.generator.ObjectFactory;
import org.example.generator.RandomUtils;
import org.example.generator.ReflectionUtils;
import org.example.generator.ResolvedType;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class MapTypeHandler implements TypeHandler {

    private static final List<Class<?>> IMPLEMENTATIONS =
            List.of(LinkedHashMap.class, TreeMap.class, ConcurrentHashMap.class);

    private static final int MIN_SIZE = 1;
    private static final int EXPONENTIAL_SCALE = 3;

    @Override
    public boolean supports(Class<?> type) {
        return Map.class.isAssignableFrom(type)
                && (defaultImplementation(type).isPresent() || ReflectionUtils.isConcrete(type));
    }

    @Override
    public Object create(ResolvedType type, ObjectFactory objectFactory, int remainLoops) {
        Map<Object, Object> result = newInstance(type.raw());
        ResolvedType keyType = type.argumentOrDefault(0, Integer.class);
        ResolvedType valueType = type.argumentOrDefault(1, String.class);
        int size = RandomUtils.randomExponentialSize(MIN_SIZE, EXPONENTIAL_SCALE);

        for (int i = 0; i < size; ++i) {
            Object key = objectFactory.create(keyType, remainLoops);
            if (key == null) {
                break;
            }
            Object value = objectFactory.create(valueType, remainLoops);
            try {
                result.put(key, value);
            } catch (RuntimeException e) {
                throw new RuntimeException(String.format("Map %s denied entry %s->%s", type.raw().getSimpleName(), key, value), e);
            }
        }

        return result;
    }

    private static Optional<Class<?>> defaultImplementation(Class<?> type) {
        return IMPLEMENTATIONS.stream().filter(type::isAssignableFrom).findFirst();
    }

    private static Map<Object, Object> newInstance(Class<?> type) {
        Class<?> implementation = ReflectionUtils.isConcrete(type) ? type : defaultImplementation(type).orElseThrow();
        return ReflectionUtils.newNoArgInstance(implementation);
    }
}