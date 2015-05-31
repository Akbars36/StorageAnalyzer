package com.vsu.amm.data.cache;

import com.vsu.amm.data.cache.CacheItem.CacheItemComparator;
import com.vsu.amm.data.storage.IDataStorage;
import com.vsu.amm.stat.ICounterSet.OperationType;
import com.vsu.amm.stat.SimpleCounterSet;

/**
 * Кэш на основе массива
 */
public class SimpleCacheArray extends AbstractCache {
    private CacheItem[] array;

    public SimpleCacheArray() {
        array = new CacheItem[cacheSize];
    }

    @Override
    public void clear() {
        super.clear();
        itemsCount = 0;
    }

    @Override
    public void setSize(int size) {
        super.setSize(size);
        array = new CacheItem[size];
    }

    @Override
	public IDataStorage cloneDefault() {
    	SimpleCacheArray s=new SimpleCacheArray();
    	s.setCounterSet(new SimpleCounterSet());
		return s;
	}

    @Override
    public String getStorageName() {
        String name = "";
        if (storage != null)
            name = storage.getStorageName() + " with ";

        return name + "Simple Array " + comparator.toString() + "-cache";
    }

    @Override
    public void uncheckedInsert(int value) {
        if (storage != null)
            storage.uncheckedInsert(value);
    }

    @Override
    protected void shrinkCache() {
    }

    @Override
    public boolean get(int value) {
        waitForThread();
        for (int i = 0; i < itemsCount; i++) {
            counterSet.inc(OperationType.DEFERRED_COMPARE);
            if (array[i].getValue() == value)
                return true;
        }

        boolean itemInStorage = false;
        if (storage != null)
            itemInStorage = storage.get(value);

        if (itemInStorage)
            insertInCache(value);

        return itemInStorage;
    }

    @Override
    public boolean set(int value) {
        waitForThread();
        for (int i = 0; i < itemsCount; i++) {
            counterSet.inc(OperationType.DEFERRED_COMPARE);
            if (array[i].getValue() == value) {
                update(i);
                if (storage != null)
                    storage.set(value);
                return true;
            }
        }

        insertInCache(value);
        return storage != null && storage.set(value);
    }

    @Override
    public boolean remove(int value) {
        int index = -1;
        waitForThread();
        for (int i = 0; i < itemsCount; i++) {
            counterSet.inc(OperationType.DEFERRED_COMPARE);
            //элемент найден
            if (array[i].getValue() == value) {
                index = i;
                break;
            }
        }

        if (index == -1)
            return storage.remove(value);

        final int ind = index;
        waitingThread = new Thread() {
            @Override
            public void run() {
                for (int i = ind + 1; i < itemsCount; i++) {
                    counterSet.inc(OperationType.DEFERRED_ASSIGN);
                    array[i - 1] = array[i];
                }
            }
        };
        waitingThread.start();
        itemsCount--;
        return storage.remove(value);
    }

    //вставка элемента которого не существовало в кэше
    private void insertInCache(int value) {
        waitForThread();
        if (!shouldValueBeInserted())
            return;

        counterSet.inc(OperationType.DEFERRED_ASSIGN);
        //в отдельном потоке вставляем элемент
        waitingThread = new Thread() {
            @Override
            public void run() {
                int insPosition = cacheSize <= itemsCount ? itemsCount - 1 : itemsCount;
                array[insPosition] = new CacheItem(value);
                if (cacheSize > itemsCount)
                    itemsCount++;
            }
        };
        waitingThread.start();
    }

    /**
     * обновление элемента в кэше
     * @param index индекс обновляемого элемента
     */
    private void update(int index) {
        waitForThread();
        waitingThread = new Thread() {
            @Override
            public void run() {
                array[index].update();
                if (comparator == CacheItemComparator.MRU) {
                    CacheItem ci = array[index];
                    for (int i = index + 1; i < itemsCount; i++) {
                        counterSet.inc(OperationType.DEFERRED_ASSIGN);
                        array[i - 1] = array[i];
                    }
                    array[itemsCount - 1] = ci;
                    counterSet.inc(OperationType.DEFERRED_ASSIGN);
                    return;
                }
                if (comparator == CacheItemComparator.LRU) {
                    CacheItem ci = array[index];
                    for (int i = index; i > 0; i--) {
                        counterSet.inc(OperationType.DEFERRED_ASSIGN);
                        array[i] = array[i - 1];
                    }
                    array[0] = ci;
                    counterSet.inc(OperationType.DEFERRED_ASSIGN);
                    return;
                }

                if (comparator == CacheItemComparator.LFU) {
                    CacheItem ci = array[index];
                    //сдвигаем все элементы, ищем место вставки обновлённого элемента
                    int i = index;
                    counterSet.inc(OperationType.DEFERRED_COMPARE);
                    while (i > 0 && comparator.compare(ci, array[index]) < 0) {
                        counterSet.inc(OperationType.DEFERRED_ASSIGN);
                        counterSet.inc(OperationType.DEFERRED_COMPARE);
                        array[i] = array[i - 1];
                        i--;
                    }
                    counterSet.inc(OperationType.DEFERRED_ASSIGN);
                    array[i] = ci;
                }
            }
        };
        waitingThread.start();
    }
}
