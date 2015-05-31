package com.vsu.amm.data.cache;

import com.vsu.amm.Utils;
import com.vsu.amm.data.cache.CacheItem.CacheItemComparator;
import com.vsu.amm.data.storage.IDataStorage;
import com.vsu.amm.stat.ICounterSet;
import com.vsu.amm.stat.ICounterSet.OperationType;
import com.vsu.amm.stat.SimpleCounterSet;

import java.util.Map;
import java.util.Random;

/**
 * Created by Nikita Skornyakov on 17.05.2015.
 */
public abstract class AbstractCache implements ICache {

    public static final int DEFAULT_CACHE_SIZE = 10;
    protected int cacheSize = DEFAULT_CACHE_SIZE;
    protected IDataStorage storage;
    protected int insertionRate = 75;
    protected ICounterSet counterSet = new SimpleCounterSet();
    protected int itemsCount = 0;
    private String id;
    private final Random rnd = new Random();
    protected CacheItemComparator comparator = CacheItemComparator.LRU;
    protected volatile Thread waitingThread = null;

    protected void waitForThread() {
        if (waitingThread != null)
            if (Thread.currentThread().equals(waitingThread))
                waitingThread.interrupt();
            else
                try {
                    waitingThread.join();
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }
        waitingThread = null;
    }

    public AbstractCache() {
    }

    public AbstractCache(String id){
        this.id = id;
    }

    public AbstractCache(IDataStorage storage) {
        this.storage = storage;
    }

    public AbstractCache(String id, IDataStorage storage) {
        this.storage = storage;
    }

    public AbstractCache(IDataStorage storage, Map<String, Integer> params) {
        this.storage = storage;
        setStorageParams(params);
    }

    @Override
    public ICounterSet getCounterSet() {
        ICounterSet res = new SimpleCounterSet();
        if (counterSet != null) {
            res.inc(OperationType.DEFERRED_ASSIGN, counterSet.get(OperationType.DEFERRED_ASSIGN));
            res.inc(OperationType.DEFERRED_COMPARE, counterSet.get(OperationType.DEFERRED_COMPARE));
        }

        if (storage == null)
            return res;

        ICounterSet ics = storage.getCounterSet();
        if (ics == null)
            return res;

        res.inc(OperationType.DEFERRED_ASSIGN, ics.get(OperationType.DEFERRED_ASSIGN));
        res.inc(OperationType.DEFERRED_COMPARE, ics.get(OperationType.DEFERRED_COMPARE));
        res.inc(OperationType.COMPARE, ics.get(OperationType.COMPARE));
        res.inc(OperationType.ASSIGN, ics.get(OperationType.ASSIGN));
        return res;
    }

    @Override
    public void setCounterSet(ICounterSet counterSet) {
        this.counterSet = counterSet;
    }

    @Override
    public void setInnerStorage(IDataStorage innerStorage) {
        this.storage = innerStorage;
    }

    @Override
    public void setStorageParams(Map<String, Integer> params) {
        if (params == null)
            return;

        String prefix = Utils.isNullOrEmpty(id) ? "" : id + ".";
        Integer param = params.get(prefix + "cacheSize");
        if (param != null)
            setSize(cacheSize);

        param = params.get(prefix + "insertRate");
        if (param != null)
            setInsertionRate(param);

        if (storage != null)
            storage.setStorageParams(params);
    }

    @Override
    public void clear() {
        itemsCount = 0;
        if (storage != null)
            storage.clear();
    }

    @Override
    public IDataStorage cloneDefault() {
        return null;
    }

    @Override
    public String getStorageName() {
        return null;
    }

    @Override
    public int getSize() {
        return cacheSize;
    }

    @Override
    public void setSize(int size) {
        if (size < 0)
            size = 0;
        cacheSize = size;
        if (size < itemsCount)
            shrinkCache();
    }

    protected abstract void shrinkCache();

    @Override
    public int getInsertionRate() {
        return insertionRate;
    }

    @Override
    public void setInsertionRate(int insertionRate) {
        this.insertionRate = insertionRate;
    }

    @Override
    public IDataStorage getInnerStorage() {
        return storage;
    }

    protected boolean shouldValueBeInserted() {
        return insertionRate >= 100 || insertionRate > 0 && rnd.nextInt(100) < insertionRate;

    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public CacheItemComparator getComparator() {
        return comparator;
    }

    public void setComparator(CacheItemComparator comparator) {
        this.comparator = comparator;
    }

    @Override
    public void clearCache() {
        itemsCount = 0;
    }

}
