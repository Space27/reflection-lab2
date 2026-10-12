package org.example.generator.typehandler;

import org.example.generator.Generatable;
import org.example.generator.ObjectFactory;
import org.example.generator.ReflectionUtils;
import org.example.generator.ResolvedType;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.List;

public class ClassTypeHandler implements TypeHandler {

    @Override
    public boolean supports(Class<?> type) {
        return !type.isInterface() && !Modifier.isAbstract(type.getModifiers())
                && !type.isPrimitive() && !type.isEnum()
                && !type.isArray()
                && !(type.isMemberClass() && Modifier.isStatic(type.getModifiers()))
                && !type.isLocalClass() && !type.isAnonymousClass();
    }

    @Override
    public Object create(ResolvedType type, ObjectFactory objectFactory, int remainLoops) {
        Class<?> clazz = type.raw();
        List<Constructor<?>> constructors = findConstructors(clazz);
        if (constructors.size() != 1) {
            throw new RuntimeException(String.format("%s has 0 or more than 1 constructors", clazz.getSimpleName()));
        }
        Constructor<?> constructor = constructors.getFirst();

        Object[] args = Arrays.stream(constructor.getParameters())
                .map(p -> objectFactory.create(ResolvedType.of(p.getParameterizedType()), remainLoops))
                .toArray();

        try {
            constructor.setAccessible(true);
            return constructor.newInstance(args);
        } catch (InvocationTargetException e) {
            throw new RuntimeException("Constructor of " + clazz.getSimpleName() + " throw exception: "
                    + e.getCause(), e.getCause());
        } catch (ReflectiveOperationException | RuntimeException e) {
            throw new RuntimeException("Cannot create " + clazz.getName(), e);
        }
    }

    private List<Constructor<?>> findConstructors(Class<?> clazz) {
        List<Constructor<?>> all = Arrays.asList(clazz.getDeclaredConstructors());
        if (all.size() > 1) {
            all = all.stream()
                    .filter(c -> c.isAnnotationPresent(Generatable.class)).toList();
        }

        return all;
    }
}