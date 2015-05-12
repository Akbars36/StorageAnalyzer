package com.vsu.amm.data.cache;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by Nikita Skornyakov on 11.05.2015.
 */
public class LFUCache extends AbstractCache {

    public LFUCache(String sizeParam, String insertRateParam, Map<String, Integer> params) {
        super(sizeParam, insertRateParam, params);
    }

    public LFUCache() {
        super();
    }

    @Override
    protected void adjustCacheSize() {
        int countToRemove = cache.size() - size;
        if (countToRemove <= 0)
            return;
        List<AbstractCache.CacheStructure> itemsToRemove = new ArrayList<>(countToRemove);
        for (AbstractCache.CacheStructure cacheItem : cache) {
            int x = 0;
            while (x < itemsToRemove.size() && cacheItem.hitCount > itemsToRemove.get(x).hitCount)
                x++;
            if (x > countToRemove)
                continue;
            itemsToRemove.add(x, cacheItem);
            if (itemsToRemove.size() > countToRemove)
                itemsToRemove.remove(countToRemove);
        }

        cache.removeAll(itemsToRemove);
    }

    @Override
    protected void replaceItem(int value) {
        AbstractCache.CacheStructure itemToRemove = cache.get(0);
        for (int i = 1; i < size; i++)
            if (itemToRemove.hitCount > cache.get(i).hitCount)
                itemToRemove = cache.get(i);
        cache.remove(itemToRemove);
        cache.add(new AbstractCache.CacheStructure(value));
    }
}
