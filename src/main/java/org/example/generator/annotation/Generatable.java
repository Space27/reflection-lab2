package org.example.generator.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Помечает необходимую реализацию интерфейса/конструктора при возникновении неопределенности генерации
 * Формально является аналогом @Autowired для конструкторов и @Primary для реализаций интерфейсов
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.CONSTRUCTOR})
public @interface Generatable {
}
