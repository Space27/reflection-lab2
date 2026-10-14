package org.example.generator.annotation;

import org.example.generator.ObjectFactory;

import java.lang.annotation.*;

/**
 * Помечает класс как Singleton. Фабрика {@link ObjectFactory} создаст его экземпляр только один раз
 * и будет возвращать его при всех последующих запросах.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Inherited
public @interface Singleton {
}
