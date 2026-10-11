package org.example.generator.typehandler;

import org.example.generator.ResolvedType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Генерация строк")
class StringTypeHandlerTest {

    private static final int SAMPLES = 1_000;

    private final StringTypeHandler handler = new StringTypeHandler();

    private String create() {
        return (String) handler.create(ResolvedType.of(String.class), null, 0);
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

        assertThat(distinct.size()).isGreaterThan(SAMPLES / 100);
    }

    @Test
    @DisplayName("Встречаются строки разной длины")
    void stringTypeHandler_producesDifferentLengths() {
        Set<Integer> lengths = new HashSet<>();
        samples().forEach(s -> lengths.add(s.length()));

        assertThat(lengths.size()).isGreaterThan(5);
    }
}