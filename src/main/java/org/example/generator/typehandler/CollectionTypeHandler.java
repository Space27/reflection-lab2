package org.example.generator.typehandler;

import org.example.generator.*;
import org.example.generator.utils.RandomUtils;
import org.example.generator.utils.ReflectionUtils;

import java.util.*;

public class CollectionTypeHandler implements TypeHandler {

    private static final List<Class<?>> IMPLEMENTATIONS =
            List.of(ArrayList.class, LinkedHashSet.class, ArrayDeque.class, TreeSet.class);

    private static final int MIN_SIZE = 1;
    private static final int EXPONENTIAL_SCALE = 3;

    @Override
    public boolean supports(Class<?> type) {
        return Collection.class.isAssignableFrom(type)
                && (ReflectionUtils.isConcrete(type) || defaultImplementation(type).isPresent());
    }

    @Override
    public Object create(ResolvedType type, ObjectFactory objectFactory, int remainLoops) {
        Collection<Object> result = newInstance(type.raw());
        ResolvedType element = type.argumentOrDefault(0, Integer.class);
        int size = RandomUtils.randomExponentialSize(MIN_SIZE, EXPONENTIAL_SCALE);

        for (int i = 0; i < size; ++i) {
            Object value = objectFactory.create(element, remainLoops);
            if (value == null) {
                break;
            }
            try {
                result.add(value);
            } catch (RuntimeException e) {
                throw new GenerationException(String.format("Collection %s denied element %s", type.raw().getSimpleName(), value), e);
            }
        }

        return result;
    }

    private static Optional<Class<?>> defaultImplementation(Class<?> type) {
        return IMPLEMENTATIONS.stream().filter(type::isAssignableFrom).findFirst();
    }

    private static Collection<Object> newInstance(Class<?> type) {
        Class<?> implementation = ReflectionUtils.isConcrete(type) ? type : defaultImplementation(type).orElseThrow();
        return ReflectionUtils.newNoArgInstance(implementation);
    }
}