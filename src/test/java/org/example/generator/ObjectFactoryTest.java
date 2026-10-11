package org.example.generator;

import org.assertj.core.api.InstanceOfAssertFactories;
import org.assertj.core.data.Offset;
import org.example.classes.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.lang.reflect.Array;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import static org.assertj.core.api.Assertions.*;

class ObjectFactoryTest {

    interface Empty {
    }

    interface One {
    }

    class OneImpl implements One {
    }

    interface Two {
    }

    @Generatable
    class FirstTwoImpl implements Two {
    }

    @Generatable
    class SecondTwoImpl implements Two {
    }

    abstract class AbstractMultiple {
    }

    @Generatable
    class ConcreteFirst extends AbstractMultiple {
    }

    class ConcreteSecond extends AbstractMultiple {
    }

    private final ObjectFactory factory = new ObjectFactory();

    @ParameterizedTest(name = "Создаёт тип {0}")
    @ValueSource(classes = {Integer.class, int.class, Boolean.class, boolean.class, Double.class, double.class, char.class})
    @DisplayName("Создает обертки и примитивы")
    <T> void objectFactory_createsPrimitiveAndWrapperTypes(Class<T> type) {
        T object = factory.create(type);

        assertThat(object)
                .isNotNull();
    }

    @Test
    @DisplayName("Создает строки")
    void objectFactory_createsStrings() {
        String object = factory.create(String.class);

        assertThat(object)
                .isNotNull()
                .isInstanceOf(String.class)
                .isNotBlank()
                .isAlphanumeric();
    }

    @Test
    @DisplayName("Создает перечисления")
    void objectFactory_createsEnums() {
        Color object = factory.create(Color.class);

        assertThat(object)
                .isNotNull()
                .isInstanceOf(Color.class)
                .isIn(Color.RED, Color.GREEN, Color.BLUE);
    }

    @ParameterizedTest(name = "Создаёт массив оберток типа {0}")
    @ValueSource(classes = {Integer.class, Double.class, String.class, Color.class, Boolean.class, Character.class})
    @DisplayName("Создает массивы оберток")
    @SuppressWarnings("unchecked")
    <T> void objectFactory_createsWrapperArrays(Class<T> type) {
        T[] object = (T[]) factory.create(type.arrayType());

        assertThat(object)
                .isNotNull()
                .isInstanceOf(type.arrayType())
                .isNotEmpty()
                .allSatisfy(element -> {
                    assertThat(element)
                            .isNotNull()
                            .isInstanceOf(type);

                    if (element instanceof String s) {
                        assertThat(s).isNotBlank().isAlphanumeric();
                    }
                });
    }

    @ParameterizedTest(name = "Создаёт массив примитивов типа {0}")
    @ValueSource(classes = {int.class, double.class, boolean.class, char.class, long.class, short.class})
    @DisplayName("Создает примитивные массивы")
    void objectFactory_createsPrimitiveArrays(Class<?> type) {
        Class<?> arrayType = type.arrayType();
        Object arrayObject = factory.create(arrayType);

        assertThat(arrayObject)
                .isNotNull()
                .isInstanceOf(arrayType);

        int length = Array.getLength(arrayObject);
        assertThat(length).isGreaterThan(0);
    }

    @Test
    @DisplayName("Создает многомерные массивы")
    void objectFactory_createsMultidimensionalArrays() {
        int[][] matrix = factory.create(int[][].class);

        assertThat(matrix)
                .isNotNull()
                .isNotEmpty();
        assertThat(matrix[0])
                .isNotNull()
                .isNotEmpty();
    }

    @ParameterizedTest(name = "Создаёт коллекцию типа {0}")
    @ValueSource(classes = {List.class, Queue.class, Deque.class, Set.class, ArrayList.class, LinkedList.class, SortedSet.class})
    @DisplayName("Создает коллекции без generic")
    void objectFactory_createsCollectionsWithoutGenerics(Class<?> type) {
        Collection<?> collection = (Collection<?>) factory.create(type);

        assertThat(collection)
                .isNotNull()
                .isInstanceOf(Collection.class)
                .isInstanceOf(type)
                .isNotEmpty()
                // без generic создается Integer
                .hasOnlyElementsOfType(Integer.class)
                .allSatisfy(element -> assertThat(element).isNotNull());
    }

    @ParameterizedTest(name = "Создаёт map типа {0}")
    @ValueSource(classes = {Map.class, SortedMap.class, NavigableMap.class, ConcurrentMap.class, HashMap.class, ConcurrentHashMap.class})
    @DisplayName("Создает map без generic")
    void objectFactory_createsMapsWithoutGenerics(Class<?> type) {
        Map<?, ?> collection = (Map<?, ?>) factory.create(type);

        assertThat(collection)
                .isNotNull()
                .isInstanceOf(Map.class)
                .isInstanceOf(type)
                .isNotEmpty()
                .allSatisfy((key, value) -> {
                    assertThat(key).isNotNull().isInstanceOf(Integer.class);
                    assertThat(value).isNotNull().isInstanceOf(String.class);
                });
    }

    @Test
    @DisplayName("Создает объект с одним простым конструктором")
    void objectFactory_createsSimpleObjectWithOneConstructor() {
        Rectangle object = factory.create(Rectangle.class);

        assertThat(object)
                .isNotNull()
                .extracting(Rectangle::getArea, as(InstanceOfAssertFactories.DOUBLE))
                .isCloseTo(object.getLength() * object.getWidth(), Offset.offset(0.0001))
                .isNotCloseTo(0., Offset.offset(0.0001));
    }

    @Test
    @DisplayName("Создает объект с несколькими конструкторами и Generatable")
    void objectFactory_createsSimpleObjectWithSeveralConstructorsGeneratable() {
        Product object = factory.create(Product.class);

        object.setPrice(10.);

        assertThat(object)
                .isNotNull()
                .extracting(Product::getName, as(InstanceOfAssertFactories.STRING))
                .isNotBlank()
                .isAlphanumeric();

        assertThat(object.getPrice()).isEqualTo(10.);
    }

    @Test
    @DisplayName("Не создает объект с несколькими конструкторами без Generatable")
    void objectFactory_notCreatesSimpleObjectWithSeveralConstructorsWithoutGeneratable() {
        assertThatThrownBy(() -> factory.create(Person.class)).
                isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Person")
                .hasMessageContaining("constructor");
    }

    @Test
    @DisplayName("Создает объект с вложенным списком")
    void objectFactory_createsObjectWithList() {
        Cart object = factory.create(Cart.class);

        assertThat(object)
                .isNotNull()
                .extracting(Cart::getItems, as(InstanceOfAssertFactories.LIST))
                .isNotEmpty()
                .hasOnlyElementsOfType(Product.class)
                .allSatisfy(element -> assertThat((Product) element)
                        .isNotNull()
                        .extracting(Product::getName, as(InstanceOfAssertFactories.STRING))
                        .isNotBlank()
                        .isAlphanumeric()
                );
    }

    @Test
    @DisplayName("Создает рекурсивный объект BinaryTreeNode")
    void objectFactory_createsRecursiveObject() {
        BinaryTreeNode root = factory.create(BinaryTreeNode.class);

        assertThat(root)
                .isNotNull()
                .extracting(BinaryTreeNode::getData, as(InstanceOfAssertFactories.INTEGER))
                .isNotNull()
                .isNotEqualTo(0);

        if (root.getLeft() != null) {
            assertThat(root.getLeft())
                    .isNotNull()
                    .extracting(BinaryTreeNode::getData, as(InstanceOfAssertFactories.INTEGER))
                    .isNotNull()
                    .isNotEqualTo(0);
        }

        if (root.getRight() != null) {
            assertThat(root.getRight())
                    .isNotNull()
                    .extracting(BinaryTreeNode::getData, as(InstanceOfAssertFactories.INTEGER))
                    .isNotNull()
                    .isNotEqualTo(0);
        }
    }

    @Test
    @DisplayName("Создает объект со сложными вложенными дженериками")
    void objectFactory_createsComplexNestedGenerics() {
        Company company = factory.create(Company.class);

        assertThat(company)
                .isNotNull()
                .extracting(Company::getDepartmentProducts, as(InstanceOfAssertFactories.MAP))
                .isNotNull()
                .isNotEmpty();
        assertThat(company.getDepartmentProducts().values())
                .isNotEmpty()
                .allSatisfy(element -> assertThat((List<?>) element).isNotEmpty())
                .hasOnlyElementsOfType(List.class)
                .extracting(List::getFirst)
                .isNotEmpty()
                .hasOnlyElementsOfType(Product.class)
                .allSatisfy(element -> assertThat(element).isNotNull());
    }

    @Test
    @DisplayName("Создает интерфейс с двумя реализациями с Generatable")
    void objectFactory_createsInterfaceWithTwoImplsWithGeneratable() {
        Shape object = factory.create(Shape.class);

        assertThat(object)
                .isNotNull()
                .isInstanceOf(Shape.class)
                .isInstanceOf(Triangle.class)
                .extracting(Shape::getPerimeter, as(InstanceOfAssertFactories.DOUBLE))
                .isNotCloseTo(0., Offset.offset(0.0001));
    }

    @Test
    @DisplayName("Создает абстрактный класс с двумя реализациями с Generatable")
    void objectFactory_createsAbstractClassWithTwoImplsWithGeneratable() {
        AbstractMultiple object = factory.create(AbstractMultiple.class);

        assertThat(object)
                .isNotNull()
                .isInstanceOf(AbstractMultiple.class)
                .isInstanceOf(ConcreteFirst.class);
    }

    @Test
    @DisplayName("Создает интерфейс с одной реализацией")
    void objectFactory_createsInterfaceWithOneImplementation() {
        One object = factory.create(One.class);

        assertThat(object)
                .isNotNull()
                .isInstanceOf(One.class)
                .isInstanceOf(OneImpl.class);
    }

    @Test
    @DisplayName("Не создает интерфейс без реализаций")
    void objectFactory_createsInterfaceWithoutImplementation() {
        assertThatThrownBy(() -> factory.create(Empty.class))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Empty")
                .hasMessageContaining("has no impls");
    }

    @Test
    @DisplayName("Не создает интерфейс с несколькими реализациями с Generatable")
    void objectFactory_createsInterfaceWithTwoImplsWithTwoGeneratable() {
        assertThatThrownBy(() -> factory.create(Two.class))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Two")
                .hasMessageContaining("has more than 1 implementations")
                .hasMessageContaining("FirstTwoImpl")
                .hasMessageContaining("SecondTwoImpl");
    }
}
