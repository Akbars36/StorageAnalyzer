package com.vsu.amm.data.cache;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by Nikita Skornyakov on 11.05.2015.
 */
public class LRUCache extends AbstractCache {

    public LRUCache(String sizeParam, String insertRateParam, Map<String, Integer> params) {
        super(sizeParam, insertRateParam, params);
    }

    public LRUCache() {
        super();
    }

    @Override
    protected void adjustCacheSize() {
        int countToRemove = cache.size() - size;
        if (countToRemove <= 0)
            return;
        List<CacheStructure> itemsToRemove = new ArrayList<>(countToRemove);
        for (CacheStructure cacheItem : cache) {
            int x = 0;
            while (x < itemsToRemove.size() && cacheItem.lastHit.after(itemsToRemove.get(x).lastHit))
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
        CacheStructure itemToRemove = cache.get(0);
        for (int i = 1; i < size; i++)
            if (itemToRemove.lastHit.after(cache.get(i).lastHit))
                itemToRemove = cache.get(i);
        cache.remove(itemToRemove);
        cache.add(new CacheStructure(value));
    }
}
