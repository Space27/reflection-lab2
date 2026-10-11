package org.example.generator;

import org.reflections.Reflections;

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

public final class ReflectionUtils {

    private static final Map<Class<?>, List<Class<?>>> cache = new ConcurrentHashMap<>();

    private ReflectionUtils() {
    }

    public static boolean isConcrete(Class<?> type) {
        if (type.isInterface() || Modifier.isAbstract(type.getModifiers())) {
            return false;
        }
        try {
            type.getDeclaredConstructor();
            return true;
        } catch (NoSuchMethodException e) {
            return false;
        }
    }

    @SuppressWarnings("unchecked")
    public static <T> T newNoArgInstance(Class<?> type) {
        try {
            Constructor<?> constructor = type.getDeclaredConstructor();
            constructor.setAccessible(true);
            return (T) constructor.newInstance();
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException("Cannot create " + type.getName(), e);
        }
    }

    public static List<Class<?>> findImplClasses(Class<?> ifc) {
        if (cache.containsKey(ifc)) {
            return cache.get(ifc);
        }

        Reflections scanner = new Reflections(ifc.getPackageName());

        List<Class<?>> candidates = scanner.getSubTypesOf(ifc).stream()
                .filter(c -> !c.isInterface() && !Modifier.isAbstract(c.getModifiers()))
                .<Class<?>>map(Function.identity())
                .toList();

        cache.put(ifc, candidates);

        return candidates;
    }
}
