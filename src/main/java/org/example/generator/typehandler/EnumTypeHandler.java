package org.example.generator.typehandler;

import org.example.generator.GenerationException;
import org.example.generator.ObjectFactory;
import org.example.generator.annotation.Range;
import org.example.generator.utils.RandomUtils;
import org.example.generator.ResolvedType;

public class EnumTypeHandler implements TypeHandler {

    @Override
    public boolean supports(Class<?> type) {
        return type.isEnum();
    }

    @Override
    public Object create(ResolvedType type, ObjectFactory objectFactory, int remainLoops) {
        Object[] constants = type.raw().getEnumConstants();

        if (constants.length == 0) {
            throw new GenerationException(String.format("Enum %s has no constants", type.raw().getSimpleName()));
        }

        int i = type.getAnnotation(Range.class)
                .map(r -> (int) RandomUtils.randomIntegral(r, 0, constants.length))
                .orElse(RandomUtils.RANDOM.nextInt(constants.length));

        return constants[i];
    }

    @Override
    public boolean isRecursive() {
        return false;
    }
}
