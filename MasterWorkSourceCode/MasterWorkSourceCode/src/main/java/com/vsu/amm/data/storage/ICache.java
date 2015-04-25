package com.vsu.amm.data.storage;

/**
 * Created by Kzarw on 22.04.2015.
 */
public interface ICache {

    /**
     * get cache size
     *
     * @return cache size
     */
    int getSize();

    /**
     * устанавливает размер кэша, если значение
     *
     * @param size new cache size
     */
    void setSize(int size);

    /**
     * check if item in cache
     *
     * @return true if item in cache, false otherwise
     */
    boolean getItem(int item);
}
