package org.example.generator.typehandler;

import org.example.generator.*;
import org.example.generator.annotation.Generatable;
import org.example.generator.utils.ReflectionUtils;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

public class ImplementationTypeHandler implements TypeHandler {


    @Override
    public boolean supports(Class<?> type) {
        return (type.isInterface() || Modifier.isAbstract(type.getModifiers()));
    }

    @Override
    public Object create(ResolvedType type, ObjectFactory objectFactory, int remainLoops) {
        Class<?> implClass = getImplClass(type.raw());

        return objectFactory.create(ResolvedType.of(implClass), remainLoops + 1);
    }

    private Class<?> getImplClass(Class<?> ifc) {
        List<Class<?>> all = ReflectionUtils.findImplClasses(ifc);

        if (all.isEmpty()) {
            throw new GenerationException(String.format("Abstract (interface) %s has no impls", ifc.getSimpleName()));
        } else if (all.size() > 1) {
            var tmp = new ArrayList<>(all);
            all = all.stream()
                    .filter(c -> c.isAnnotationPresent(Generatable.class))
                    .toList();
            if (all.isEmpty()) {
                throw new GenerationException(
                        String.format("Abstract (interface) %s has more than 1 implementations without @Generatable: %s",
                                ifc.getSimpleName(), tmp.stream().map(Class::getSimpleName).toList()));
            }
        }
        if (all.size() > 1) {
            throw new GenerationException(
                    String.format("Abstract (interface) %s has more than 1 implementations with @Generatable: %s",
                            ifc.getSimpleName(), all.stream().map(Class::getSimpleName).toList()));
        }

        return all.getFirst();
    }
}