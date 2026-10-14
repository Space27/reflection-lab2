package org.example.generator.typehandler;

import org.example.generator.ObjectFactory;
import org.example.generator.ResolvedType;

/**
 * Интерфейс для обработки и генерации объектов определенных типов в рамках фабрики {@link ObjectFactory}.
 * Реализации этого интерфейса отвечают за создание конкретных категорий типов.
 */
public interface TypeHandler {

    /**
     * Проверяет, поддерживает ли данный обработчик генерацию объектов указанного типа.
     *
     * @param type тип класса для проверки
     * @return {@code true}, если хэндлер может обработать этот тип, иначе {@code false}
     */
    boolean supports(Class<?> type);

    /**
     * Создает и возвращает экземпляр объекта для заданного типа.
     *
     * @param type          разрешенный тип {@link ResolvedType}
     * @param objectFactory фабрика объектов, используемая для рекурсивной генерации вложенных зависимостей
     * @param remainLoops   счетчик оставшихся шагов рекурсии
     * @return сгенерированный экземпляр объекта
     */
    Object create(ResolvedType type, ObjectFactory objectFactory, int remainLoops);

    /**
     * Определяет, участвует ли данный тип в рекурсивных цепочках генерации.
     *
     * @return {@code true} по умолчанию, если хэндлер поддерживает рекурсию
     */
    default boolean isRecursive() {
        return true;
    }
}
