package com.vsu.amm.data.storage;

import com.vsu.amm.data.cache.AbstractCache;
import com.vsu.amm.stat.ICounterSet;

import java.util.Map;

/**
 * Created by Nikita Skornyakov on 11.05.2015.
 */
public abstract class AbstractStorage implements IDataStorage {

    protected ICounterSet counterSet;
    protected AbstractCache cache;

    @Override
    public ICounterSet getCounterSet() {
        return counterSet;
    }

    @Override
    public void setCounterSet(ICounterSet counterSet) {
        this.counterSet = counterSet;
    }

    @Override
    public void setStorageParams(Map<String, Integer> params) {
        if (cache != null)
            cache.setParams(params);
    }

    @Override
    public void clear() {
        cache.clear();
        counterSet.clear();
    }

    @Override
    public void setCache(AbstractCache cache) {
        this.cache = cache;
    }

    /**
     * gets item from cache if exists
     *
     * @param value value to get from cache
     * @return true if value found in cache, false otherwise
     */
    protected boolean getFromCache(int value) {
        if (cache != null)
            return cache.getItem(value);
        return false;
    }

    @Override
    public void remove(int value) {
        if (cache == null)
            return;
        cache.remove(value);
    }

    @Override
    public void set(int value) {
        if (cache == null)
            return;
        cache.add(value);
    }
}
