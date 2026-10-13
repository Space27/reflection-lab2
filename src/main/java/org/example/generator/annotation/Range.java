package org.example.generator.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Ограничивает диапазон генерируемого значения или размер контейнера.
 * <p>
 * В зависимости от целевого типа:
 * <ul>
 *   <li>Числовой диапазон для примитивов и оберток (например, {@code int}, {@code long}, {@code double})</li>
 *   <li>Ограничение длины строки  {@code String}</li>
 *   <li>Диапазон порядковых номеров {@code ordinal()} для перечислений {@code Enum}</li>
 *   <li>Ограничение размера для массивов, коллекций ({@code List}, {@code Set}) и словарей ({@code Map})<br>
 *   <b>Важно для {@code Set}:</b> из-за уникальности элементов итоговый размер может оказаться меньше {@link min}
 *   </li>
 * </ul>
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.PARAMETER)
public @interface Range {

    double min() default Double.NEGATIVE_INFINITY;

    double max() default Double.POSITIVE_INFINITY;
}