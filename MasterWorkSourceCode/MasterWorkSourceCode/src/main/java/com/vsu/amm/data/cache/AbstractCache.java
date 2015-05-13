package com.vsu.amm.data.cache;

import java.util.*;

/**
 * Created by Nikita Skornyakov on 22.04.2015.
 */
public abstract class AbstractCache {

    protected static final int DEFAULT_CACHE_SIZE = 5;
    protected Integer size = DEFAULT_CACHE_SIZE;
    protected String sizeParamName = null;
    protected Integer insertRate = 50;
    protected String insertRateParamName = null;
    List<CacheStructure> cache = new ArrayList<>(DEFAULT_CACHE_SIZE);
    private Random rnd = new Random();

    public AbstractCache() {
    }

    public AbstractCache(String sizeParam, String insertRateParam, Map<String, Integer> params) {
        if (sizeParam != null) {
            if (sizeParam.startsWith("%") && sizeParam.endsWith("%")) {
                sizeParamName = sizeParam.substring(1, sizeParam.length() - 1);
                if (params != null)
                    if (params.get(sizeParamName) != null)
                        size = params.get(sizeParamName);
            } else
                try {
                    size = Integer.parseInt(sizeParam);
                } catch (Exception ignored) {
                }
        }

        if (insertRateParam != null) {
            if (insertRateParam.startsWith("%") && insertRateParam.endsWith("%")) {
                insertRateParamName = insertRateParam.substring(1, insertRateParam.length() - 1);
                if (params != null)
                    if (params.get(insertRateParamName) != null)
                        insertRate = params.get(insertRateParamName);
            } else
                try {
                    insertRate = Integer.parseInt(insertRateParam);
                } catch (Exception ignored) {
                }
        }
    }

    /**
     * get cache size
     *
     * @return cache size
     */
    public int getSize() {
        return size;
    }

    /**
     * устанавливает размер кэша, если значение >= 0
     *
     * @param newSize new cache size
     */
    void setSize(int newSize) {
        if (newSize < 0)
            return;

        int oldSize = size;
        size = newSize;
        if (oldSize > size)
            adjustCacheSize();
    }

    protected abstract void adjustCacheSize();

    public void clear() {
        cache.clear();
    }

    /**
     * check if item in cache
     *
     * @return true if item in cache, false otherwise
     */
    public boolean getItem(int item) {
        for (CacheStructure cs : cache)
            if (cs.value == item)
                return true;
        add(item);
        return false;
    }

    /**
     * remove item from cache
     *
     * @param item item to remove
     */
    public void remove(int item) {
        if (cache == null)
            return;
        for (int i = 0; i < cache.size(); i++) {
            CacheStructure cs = cache.get(i);
            if (cs.value == item) {
                cache.remove(i);
                return;
            }
        }
    }

    public void setParams(Map<String, Integer> params) {
        cache.clear();

        if (params == null)
            return;

        if (sizeParamName != null) {
            Integer sz = params.get(sizeParamName);
            if (sz != null)
                setSize(sz);
        }

        if (insertRateParamName != null) {
            Integer repCh = params.get(sizeParamName);
            if (repCh != null)
                insertRate = repCh;
        }
    }

    public void add(int value) {
        int r = rnd.nextInt(100);
        if (r > insertRate)
            return;

        if (cache.size() < size)
            cache.add(new CacheStructure(value));
        else
            replaceItem(value);
    }

    protected abstract void replaceItem(int value);

    protected class CacheStructure {
        public Date lastHit;
        public int hitCount;
        public Integer value;

        public CacheStructure(Integer value) {
            this.value = value;
            hitCount = 1;
            lastHit = new Date();
        }
    }
}
