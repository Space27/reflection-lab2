package org.example.generator.typehandler;

import org.example.generator.GenerationException;
import org.example.generator.ResolvedType;
import org.example.generator.annotation.Range;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.lang.annotation.Annotation;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("Генерация перечислений")
class EnumTypeHandlerTest {

    private static final int SAMPLES = 100;

    enum Color {RED, GREEN, BLUE}

    enum Single {ONLY}

    enum Empty {}

    enum Operation {
        PLUS {
            @Override
            int apply(int a, int b) {
                return a + b;
            }
        },
        MINUS {
            @Override
            int apply(int a, int b) {
                return a - b;
            }
        };

        abstract int apply(int a, int b);
    }

    private final EnumTypeHandler handler = new EnumTypeHandler();

    private Object create(Class<?> type) {
        return handler.create(ResolvedType.of(type, List.of()), null, 0);
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

    @ParameterizedTest(name = "{0} поддерживается")
    @ValueSource(classes = {Color.class, Single.class, Empty.class, Operation.class})
    @DisplayName("Поддерживает enum")
    void enumTypeHandler_supportsEnums(Class<?> type) {
        assertThat(handler.supports(type)).isTrue();
    }

    @ParameterizedTest(name = "{0} не поддерживается")
    @ValueSource(classes = {String.class, int.class, Object.class, Enum.class, Color[].class})
    @DisplayName("Не поддерживает сторонние типы")
    void enumTypeHandler_doesNotSupportOtherTypes(Class<?> type) {
        assertThat(handler.supports(type)).isFalse();
    }

    @ParameterizedTest(name = "{0} приводится к нужному типу")
    @ValueSource(classes = {Color.class, Single.class, Operation.class})
    @DisplayName("Результат соответствует поданному типу")
    void enumTypeHandler_resultIsTypedAtCompileTime(Class<?> type) {
        Object color = create(type);

        assertThat(color)
                .isNotNull()
                .isInstanceOf(type);
    }

    @Test
    @DisplayName("Встречаются все константы перечисления")
    void enumTypeHandler_producesEveryConstant() {
        Set<Color> seen = IntStream.range(0, SAMPLES)
                .mapToObj(_ -> (Color) create(Color.class))
                .collect(Collectors.toCollection(() -> EnumSet.noneOf(Color.class)));

        assertThat(seen)
                .hasSize(Color.values().length)
                .containsExactlyInAnyOrder(Color.values());
    }

    @Test
    @DisplayName("Единственная константа возвращается всегда")
    void enumTypeHandler_singleConstantIsAlwaysReturned() {
        IntStream.range(0, SAMPLES).forEach(_ ->
                assertThat(create(Single.class)).isEqualTo(Single.ONLY));
    }

    @Test
    @DisplayName("Перечисление без констант даёт осмысленное исключение")
    void enumTypeHandler_throwsForEnumWithoutConstants() {
        assertThatThrownBy(() -> create(Empty.class))
                .isInstanceOf(GenerationException.class)
                .hasMessageContaining("Empty");
    }

    @Test
    @DisplayName("Учитывает аннотацию @Range при выборе констант перечисления")
    void enumTypeHandler_respectsRangeForEnums() {
        Set<Color> seen = IntStream.range(0, SAMPLES)
                .mapToObj(_ -> (Color) createWithRange(Color.class, 0, 1))
                .collect(Collectors.toCollection(() -> EnumSet.noneOf(Color.class)));

        assertThat(seen)
                .isNotEmpty()
                .allSatisfy(color -> assertThat(color.ordinal()).isBetween(0, 1))
                .doesNotContain(Color.BLUE);
    }

    @Test
    @DisplayName("Выбрасывает IllegalArgumentException, если min > max в @Range для перечисления")
    void enumTypeHandler_throwsOnInvalidRange() {
        assertThatThrownBy(() -> createWithRange(Color.class, 1, 0))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
