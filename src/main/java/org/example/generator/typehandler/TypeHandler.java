package org.example.generator.typehandler;

import org.example.generator.ObjectFactory;
import org.example.generator.ResolvedType;

public interface TypeHandler {

    boolean supports(Class<?> type);

    Object create(ResolvedType type, ObjectFactory objectFactory, int remainLoops);

    default boolean isRecursive() {
        return true;
    }
}
