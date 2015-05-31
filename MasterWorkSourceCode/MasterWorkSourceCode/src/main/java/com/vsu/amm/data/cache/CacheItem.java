package com.vsu.amm.data.cache;

import java.util.Date;

/**
 * Created by Nikita Skornyakov on 26.05.2015.
 */
public class CacheItem {

    public enum CacheItemComparator {
        LRU {
            @Override
            public int compare(CacheItem i1, CacheItem i2) {
                if (i1 == null)
                    if (i2 != null)
                        return -1;
                    else
                        return 0;
                if (i2 == null)
                    return 1;
                return i1.usageTime > i2.usageTime ? 1 : i1.usageTime == i2.usageTime ? 0 : -1;
            }
        },
        MRU {
            @Override
            public int compare(CacheItem i1, CacheItem i2) {
                if (i1 == null)
                    if (i2 != null)
                        return -1;
                    else
                        return 0;
                if (i2 == null)
                    return 1;
                return i1.usageTime > i2.usageTime ? -1 : i1.usageTime == i2.usageTime ? 0 : 1;
            }
        },
        LFU {
            @Override
            public int compare(CacheItem i1, CacheItem i2) {
                if (i1 == null)
                    if (i2 != null)
                        return -1;
                    else
                        return 0;
                if (i2 == null)
                    return 1;
                return i1.usageCount > i2.usageCount ? 1 : i1.usageCount == i2.usageCount ? 0 : -1;
            }
        };


        /**
         * Сравнивает два элемента кэша
         * @params i1, i2 – параметры для сравнения
         * @return >0 если первый элемент больше второго, 0 при равенстве элементов, <0 если второй больше первого
         */
        public int compare(CacheItem i1, CacheItem i2) {
            return 0;
        }
    }

    public enum ItemState {
        NORMAL,
        DELETED
    }

    private int value;
    private long usageTime;
    private int usageCount;
    private ItemState state = ItemState.NORMAL;

    public CacheItem(int value) {
        this.value = value;
        usageTime = new Date().getTime();
        usageCount = 0;
    }

    public int getValue() {
        return value;
    }

    public int compateTo(CacheItem other, CacheItemComparator comparator) {
        if (comparator == null)
            return CacheItemComparator.LRU.compare(this,other);
        return comparator.compare(this,other);
    }

    public void setValue(int value) {
        this.value = value;
        update();
    }

    public void update() {
        usageTime = new Date().getTime();
        usageCount++;
    }

    public ItemState getState() {
        return state;
    }

    public void setState(ItemState state) {
        this.state = state;
    }
}
