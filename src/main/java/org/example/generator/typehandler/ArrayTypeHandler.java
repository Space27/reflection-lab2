package org.example.generator.typehandler;

import org.example.generator.GenerationException;
import org.example.generator.ObjectFactory;
import org.example.generator.utils.RandomUtils;
import org.example.generator.ResolvedType;

import java.lang.reflect.Array;

public class ArrayTypeHandler implements TypeHandler {

    private static final int MIN_SIZE = 1;
    private static final int EXPONENTIAL_SCALE = 4;

    @Override
    public boolean supports(Class<?> type) {
        return type.isArray();
    }

    @Override
    public Object create(ResolvedType type, ObjectFactory objectFactory, int remainLoops) {
        int size = RandomUtils.randomExponentialSize(MIN_SIZE, EXPONENTIAL_SCALE);

        Object array = Array.newInstance(type.raw().getComponentType(), size);
        for (int i = 0; i < size; ++i) {
            Object value = objectFactory.create(type.componentType(), remainLoops);
            if (value == null) {
                break;
            }
            try {
                Array.set(array, i, value);
            } catch (RuntimeException e) {
                throw new GenerationException(String.format("Array %s denied element %s", type.raw().getSimpleName(), value), e);
            }
        }

        return array;
    }
}