package org.example.generator.typehandler;

import org.example.generator.ObjectFactory;
import org.example.generator.RandomUtils;
import org.example.generator.ResolvedType;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.function.Function;

public class PrimitiveTypeHandler implements TypeHandler {

    private final Map<Class<?>, Function<Random, Object>> generators = new HashMap<>();

    public PrimitiveTypeHandler() {
        register(boolean.class, Boolean.class, Random::nextBoolean);
        register(byte.class, Byte.class, r -> (byte) r.nextInt());
        register(short.class, Short.class, r -> (short) r.nextInt());
        register(int.class, Integer.class, Random::nextInt);
        register(long.class, Long.class, Random::nextLong);
        register(float.class, Float.class, Random::nextFloat);
        register(double.class, Double.class, Random::nextDouble);
        register(char.class, Character.class, r -> (char) ('a' + r.nextInt(26)));
    }

    private void register(Class<?> primitive, Class<?> wrapper, Function<Random, Object> generator) {
        generators.put(primitive, generator);
        generators.put(wrapper, generator);
    }

    @Override
    public boolean supports(Class<?> type) {
        return generators.containsKey(type);
    }

    @Override
    public Object create(ResolvedType type, ObjectFactory objectFactory, int remainLoops) {
        return generators.get(type.raw()).apply(RandomUtils.RANDOM);
    }

    @Override
    public boolean isRecursive() {
        return false;
    }
}