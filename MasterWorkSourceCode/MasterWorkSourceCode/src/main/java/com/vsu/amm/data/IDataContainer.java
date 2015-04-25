package com.vsu.amm.data;

/**
 * Created with IntelliJ IDEA.
 * User: Влад
 * Date: 13.11.13
 * Time: 20:14
 * Базовый интерфейс для контейнеров данных
 */
public interface IDataContainer {

    /**
     * Ищет заданное значение в хранилище
     *
     * @param value
     */
    void get(int value);

    /**
     * Добавляет выбранное значение в хранилище
     *
     * @param value
     */
    void set(int value);

    /**
     * удаляет выбранное значение из хранилища
     *
     * @param value
     */
    void remove(int value);
}
