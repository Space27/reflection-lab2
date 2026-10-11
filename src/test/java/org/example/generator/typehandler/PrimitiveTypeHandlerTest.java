package org.example.generator.typehandler;

import org.example.generator.ResolvedType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

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
}