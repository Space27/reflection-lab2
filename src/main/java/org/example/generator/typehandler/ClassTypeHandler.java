package org.example.generator.typehandler;

import org.example.generator.GenerationException;
import org.example.generator.ObjectFactory;
import org.example.generator.ResolvedType;
import org.example.generator.annotation.Generatable;

import java.lang.reflect.*;
import java.util.Arrays;
import java.util.List;

public class ClassTypeHandler implements TypeHandler {

    @Override
    public boolean supports(Class<?> type) {
        return !type.isInterface() && !Modifier.isAbstract(type.getModifiers())
                && !type.isPrimitive() && !type.isEnum()
                && !type.isArray()
                && !(type.isMemberClass() && !Modifier.isStatic(type.getModifiers()))
                && !type.isLocalClass() && !type.isAnonymousClass();
    }

    @Override
    public Object create(ResolvedType type, ObjectFactory objectFactory, int remainLoops) {
        Constructor<?> constructor = findConstructor(type.raw());

        Parameter[] parameters = constructor.getParameters();
        Object[] args = new Object[parameters.length];
        for (int i = 0; i < parameters.length; ++i) {
            try {
                args[i] = objectFactory.create(ResolvedType.of(parameters[i]), remainLoops);
            } catch (RuntimeException e) {
                throw new GenerationException(String.format("Error while creating %s %s for %s constructor",
                        parameters[i].getParameterizedType().getTypeName(), parameters[i].getName(), type.raw().getSimpleName()), e);
            }
        }

        return getInstance(constructor, args, type.raw());
    }

    private Constructor<?> findConstructor(Class<?> clazz) {
        List<Constructor<?>> all = Arrays.asList(clazz.getDeclaredConstructors());
        if (all.size() > 1) {
            all = all.stream()
                    .filter(c -> c.isAnnotationPresent(Generatable.class)).toList();
            if (all.size() > 1) {
                throw new GenerationException(clazz.getSimpleName() + " has more than 1 constructor with Generatable");
            } else if (all.isEmpty()) {
                throw new GenerationException(clazz.getSimpleName() + " has more than 1 constructor without Generatable");
            }
        } else if (all.isEmpty()) {
            throw new GenerationException(clazz.getSimpleName() + " has no constructors");
        }

        return all.getFirst();
    }

    private Object getInstance(Constructor<?> constructor, Object[] args, Class<?> clazz) {
        try {
            constructor.setAccessible(true);
            return constructor.newInstance(args);
        } catch (InaccessibleObjectException e) {
            throw new GenerationException("Cannot get access to constructor of " + clazz.getSimpleName(), e);
        } catch (InvocationTargetException e) {
            throw new GenerationException(String.format("Constructor of %s throw exception: %s", clazz.getSimpleName(), e.getCause()), e);
        } catch (ReflectiveOperationException | RuntimeException e) {
            throw new GenerationException("Cannot create " + clazz.getName(), e);
        }
    }
}
