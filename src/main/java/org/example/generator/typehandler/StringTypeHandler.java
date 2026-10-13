package org.example.generator.typehandler;

import org.example.generator.ObjectFactory;
import org.example.generator.annotation.Range;
import org.example.generator.utils.RandomUtils;
import org.example.generator.ResolvedType;

public class StringTypeHandler implements TypeHandler {

    private static final String ALPHABET =
            "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final int MIN_LENGTH = 1;
    private static final int EXPONENTIAL_SCALE = 4;

    @Override
    public boolean supports(Class<?> type) {
        return String.class.equals(type);
    }

    @Override
    public Object create(ResolvedType type, ObjectFactory objectFactory, int remainLoops) {
        int length = RandomUtils.randomContainerSize(
                type.getAnnotation(Range.class).orElse(null), MIN_LENGTH, EXPONENTIAL_SCALE
        );

        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; ++i) {
            sb.append(ALPHABET.charAt(RandomUtils.RANDOM.nextInt(ALPHABET.length())));
        }

        return sb.toString();
    }

    @Override
    public boolean isRecursive() {
        return false;
    }
}