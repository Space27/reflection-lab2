package org.example.generator.typehandler;

import org.example.generator.ResolvedType;
import org.example.generator.annotation.Range;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.lang.annotation.Annotation;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("Генерация строк")
class StringTypeHandlerTest {

    private static final int SAMPLES = 1_000;

    private final StringTypeHandler handler = new StringTypeHandler();

    private String create() {
        return (String) handler.create(ResolvedType.of(String.class), null, 0);
    }

    private String createWithRange(long min, long max) {
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
        return (String) handler.create(ResolvedType.of(String.class, List.of(range)), null, 0);
    }

    private List<String> samples() {
        return IntStream.range(0, SAMPLES).mapToObj(_ -> create()).toList();
    }

    @Test
    @DisplayName("Поддерживается String")
    void stringTypeHandler_supportsString() {
        assertThat(handler.supports(String.class)).isTrue();
    }

    @ParameterizedTest(name = "{0} не поддерживается")
    @ValueSource(classes = {Object.class, CharSequence.class, StringBuilder.class, char[].class, int.class})
    @DisplayName("Не поддерживаются сторонние типы")
    void stringTypeHandler_doesNotSupportOtherTypes(Class<?> type) {
        assertThat(handler.supports(type)).isFalse();
    }

    @Test
    @DisplayName("Строки непустые")
    void stringTypeHandler_stringNotEmpty() {
        assertThat(samples()).allSatisfy(s -> assertThat(s).isNotEmpty());
    }

    @Test
    @DisplayName("Строка состоит только из букв и цифр")
    void stringTypeHandler_containsOnlyAlphanumericCharacters() {
        assertThat(samples())
                .allSatisfy(s -> assertThat(s).isAlphanumeric());
    }

    @Test
    @DisplayName("Строки разные")
    void stringTypeHandler_producesVariedValues() {
        Set<String> distinct = new HashSet<>(samples());

        assertThat(distinct).hasSizeGreaterThan(SAMPLES / 100);
    }

    @Test
    @DisplayName("Встречаются строки разной длины")
    void stringTypeHandler_producesDifferentLengths() {
        Set<Integer> lengths = new HashSet<>();
        samples().forEach(s -> lengths.add(s.length()));

        assertThat(lengths).hasSizeGreaterThan(5);
    }

    @Test
    @DisplayName("Генерирует строку с длиной в пределах @Range")
    void stringTypeHandler_respectsRangeLength() {
        for (int i = 0; i < 100; ++i) {
            String s = createWithRange(5, 10);
            assertThat(s).hasSizeBetween(5, 10);
        }
    }

    @Test
    @DisplayName("Выбрасывает IllegalArgumentException, если min > max в @Range")
    void stringTypeHandler_throwsOnInvalidRange() {
        assertThatThrownBy(() -> createWithRange(6, 5))
                .isInstanceOf(IllegalArgumentException.class);
    }
}