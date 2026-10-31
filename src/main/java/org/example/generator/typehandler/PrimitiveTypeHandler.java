package org.example.generator.typehandler;

import org.example.generator.ObjectFactory;
import org.example.generator.ResolvedType;
import org.example.generator.annotation.Range;
import org.example.generator.utils.RandomUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class PrimitiveTypeHandler implements TypeHandler {

    private final Map<Class<?>, Function<ResolvedType, Object>> generators = new HashMap<>();

    public PrimitiveTypeHandler() {
        register(boolean.class, Boolean.class, PrimitiveTypeHandler::randomBoolean);
        register(byte.class, Byte.class, t -> (byte) randomIntegral(t, Byte.MIN_VALUE, Byte.MAX_VALUE));
        register(short.class, Short.class, t -> (short) randomIntegral(t, Short.MIN_VALUE, Short.MAX_VALUE));
        register(int.class, Integer.class, t -> (int) randomIntegral(t, Integer.MIN_VALUE, Integer.MAX_VALUE));
        register(long.class, Long.class, t -> randomIntegral(t, Long.MIN_VALUE, Long.MAX_VALUE));
        register(float.class, Float.class, t -> (float) randomFloating(t));
        register(double.class, Double.class, PrimitiveTypeHandler::randomFloating);
        register(char.class, Character.class, PrimitiveTypeHandler::randomChar);
    }

    private void register(Class<?> primitive, Class<?> wrapper, Function<ResolvedType, Object> generator) {
        generators.put(primitive, generator);
        generators.put(wrapper, generator);
    }

    @Override
    public boolean supports(Class<?> type) {
        return generators.containsKey(type);
    }

    @Override
    public Object create(ResolvedType type, ObjectFactory objectFactory, int remainLoops) {
        return generators.get(type.raw()).apply(type);
    }

    @Override
    public boolean isRecursive() {
        return false;
    }

    private static long randomIntegral(ResolvedType type, long typeMin, long typeMax) {
        return type.getAnnotation(Range.class)
                .map(r -> RandomUtils.randomIntegral(r, typeMin, typeMax))
                .orElse(RandomUtils.RANDOM.nextLong(typeMin, typeMax + (typeMax == Long.MAX_VALUE ? 0 : 1)));
    }

    private static double randomFloating(ResolvedType type) {
        return type.getAnnotation(Range.class)
                .map(RandomUtils::randomFloating)
                .orElse(RandomUtils.RANDOM.nextDouble());
    }

    private static char randomChar(ResolvedType type) {
        if (type.getAnnotation(Range.class).isPresent()) {
            throw new IllegalArgumentException("@Range is not applicable to character");
        }
        return (char) ('a' + RandomUtils.RANDOM.nextInt(26));
    }

    private static boolean randomBoolean(ResolvedType type) {
        if (type.getAnnotation(Range.class).isPresent()) {
            throw new IllegalArgumentException("@Range is not applicable to boolean");
        }
        return RandomUtils.RANDOM.nextBoolean();
    }
}
