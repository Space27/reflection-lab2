package org.example.generator.typehandler;

import org.assertj.core.api.InstanceOfAssertFactories;
import org.example.generator.ResolvedType;
import org.example.generator.annotation.Range;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.lang.annotation.Annotation;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.*;

@DisplayName("Генерация примитивов и их оберток")
class PrimitiveTypeHandlerTest {

    private static final int SAMPLES = 1_000;
    private final PrimitiveTypeHandler handler = new PrimitiveTypeHandler();

    static Stream<Arguments> primitiveAndWrapper() {
        return Stream.of(
                Arguments.of(boolean.class, Boolean.class),
                Arguments.of(byte.class, Byte.class),
                Arguments.of(short.class, Short.class),
                Arguments.of(int.class, Integer.class),
                Arguments.of(long.class, Long.class),
                Arguments.of(float.class, Float.class),
                Arguments.of(double.class, Double.class),
                Arguments.of(char.class, Character.class));
    }

    private Object create(Class<?> type) {
        return handler.create(ResolvedType.of(type), null, 0);
    }

    private Object createWithRange(Class<?> type, long min, long max) {
        Range range = new Range() {
            @Override
            public Class<? extends Annotation> annotationType() {
                return Range.class;
            }

            @Override
            public double min() {
                return min;
            }

            @Override
            public double max() {
                return max;
            }
        };
        return handler.create(ResolvedType.of(type, List.of(range)), null, 0);
    }

    @ParameterizedTest(name = "{0} и {1} поддерживаются")
    @MethodSource("primitiveAndWrapper")
    @DisplayName("Поддерживает примитивные типы и их обертки")
    void primitiveTypeHandler_supportsPrimitivesAndWrappers(Class<?> primitive, Class<?> wrapper) {
        assertThat(handler.supports(primitive)).isTrue();
        assertThat(handler.supports(wrapper)).isTrue();
    }

    @ParameterizedTest(name = "{0} не поддерживается")
    @ValueSource(classes = {String.class, Object.class, void.class, Void.class, List.class, int[].class})
    @DisplayName("Не поддерживает сторонние типы")
    void primitiveTypeHandler_doesNotSupportOtherTypes(Class<?> type) {
        assertThat(handler.supports(type)).isFalse();
    }

    @ParameterizedTest(name = "{0} и {1} дают значение типа {1}")
    @MethodSource("primitiveAndWrapper")
    @DisplayName("Возвращает оберточные типы для оберток и примитивов")
    void primitiveTypeHandler_createsValueOfWrapperType(Class<?> primitive, Class<?> wrapper) {
        assertThat(create(primitive)).isInstanceOf(wrapper);
        assertThat(create(wrapper)).isInstanceOf(wrapper);
    }

    @ParameterizedTest(name = "{0}: значения разные")
    @MethodSource("primitiveAndWrapper")
    @DisplayName("Создает различные значения")
    void primitiveTypeHandler_producesVariedValues(Class<?> primitive) {
        Set<Object> distinct = new HashSet<>();
        IntStream.range(0, SAMPLES).forEach(_ -> distinct.add(create(primitive)));

        assertThat(distinct.size()).isGreaterThan(1);
    }

    @ParameterizedTest(name = "Генерирует {0} в заданных диапазонах")
    @ValueSource(classes = {int.class, long.class, byte.class, short.class, Integer.class, Long.class})
    @DisplayName("Генерирует целочисленные значения в строгих пределах @Range")
    void primitiveTypeHandler_respectsRangeForIntegralTypes(Class<?> type) {
        Set<Integer> distinct = new HashSet<>();
        for (int i = 0; i < 100; ++i) {
            Object value = createWithRange(type, 10, 20);
            assertThat(value)
                    .isInstanceOf(Number.class)
                    .extracting(n -> ((Number) n).intValue(), as(InstanceOfAssertFactories.INTEGER))
                    .isBetween(10, 20);
            distinct.add(((Number) value).intValue());
        }
        assertThat(distinct).hasSizeGreaterThan(9);
    }

    @ParameterizedTest(name = "Генерирует {0} в заданных диапазонах")
    @ValueSource(classes = {double.class, float.class, Double.class, Float.class})
    @DisplayName("Генерирует числа с плавающей точкой в пределах @Range")
    void primitiveTypeHandler_respectsRangeForFloatingTypes(Class<?> type) {
        for (int i = 0; i < 100; ++i) {
            Object value = createWithRange(type, 5, 15);
            assertThat(value)
                    .isInstanceOf(Number.class)
                    .extracting(n -> ((Number) n).floatValue(), as(InstanceOfAssertFactories.FLOAT))
                    .isBetween(5.0f, 15.0f);
        }
    }

    @Test
    @DisplayName("Выбрасывает IllegalArgumentException, если min > max в @Range")
    void primitiveTypeHandler_throwsOnInvalidRange() {
        assertThatThrownBy(() -> createWithRange(int.class, 11, 10))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @ParameterizedTest(name = "Выбрасывает исключение для {0}")
    @ValueSource(classes = {char.class, boolean.class, Character.class, Boolean.class})
    @DisplayName("Выбрасывает исключение при попытке применить @Range к char или boolean")
    void primitiveTypeHandler_throwsWhenRangeAppliedToBooleanOrChar(Class<?> type) {
        assertThatThrownBy(() -> createWithRange(type, 1, 5))
                .isInstanceOf(IllegalArgumentException.class);
    }
}