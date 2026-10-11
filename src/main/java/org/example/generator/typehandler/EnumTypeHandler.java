package org.example.generator.typehandler;

import org.example.generator.ObjectFactory;
import org.example.generator.RandomUtils;
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
            throw new RuntimeException(String.format("Enum %s has no constants", type.raw().getSimpleName()));
        }

        return constants[RandomUtils.RANDOM.nextInt(constants.length)];
    }

    @Override
    public boolean isRecursive() {
        return false;
    }
}
