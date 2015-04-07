package com.vsu.amm.data.storage;

import com.vsu.amm.stat.ICounterSet;

import java.util.*;

public class CacheArray implements IDataStorage {
    private static final int DEFAULT_CACHE_SIZE = 3;
    ArrayList<Integer> array;
    ICounterSet counterSet;
    int cacheSize;
    List<Integer> cache;
    Map<String, String> params;

    public CacheArray() {
        array = new ArrayList<>();
        cacheSize = DEFAULT_CACHE_SIZE;
        cache = new LinkedList<>();
    }

    public CacheArray(Map<String, String> params) {
        array = new ArrayList<>();
        this.params = params;
        if (params.containsKey("cache_size")){
            cacheSize = Integer.parseInt(params.get("cache_size"));
        } else {
            cacheSize = DEFAULT_CACHE_SIZE;
        }
        cache = new LinkedList<>();
    }

    @Override
    public void setCounterSet(ICounterSet counterSet) {
        this.counterSet = counterSet;

    }

    @Override
    public ICounterSet getCounterSet() {
        return counterSet;
    }


    @Override
    public void setStorageParams(Map<String, String> params) {
        array = new ArrayList<>();
        this.params = params;
        if (params.containsKey("cache_size")){
            cacheSize = Integer.parseInt(params.get("cache_size"));
        } else {
            cacheSize = DEFAULT_CACHE_SIZE;
        }
        cache = new LinkedList<>();
    }

    @Override
    public Map<String, String> getStorageParams() {
        return params;
    }

    @Override
    public void clear() {
        array.clear();
        cache.clear();
    }

    @Override
    public void get(int value) {
        if (!cache.contains(value)){
            Iterator<Integer> iterator = array.iterator();
            while((iterator.hasNext()) && (iterator.next() != value)){
                counterSet.inc(ICounterSet.COMPARE);
            }
            counterSet.inc(ICounterSet.COMPARE);
            if (cache.size() == cacheSize){
                cache.remove(0);
            }
            cache.add(value);
        }
    }

    @Override
    public void set(int value) {
        counterSet.inc(ICounterSet.ASSIGN);
        array.add(value);
    }

    @Override
    public void remove(int value) {
        if (cache.contains(value)){
            Object removableObject = value;
            cache.remove(removableObject);
        }
        boolean end = false;
        int index;
        int first = 0;
        while(!end){
            for(index = first; index < array.size(); index++){
                if (array.get(index) == value){
                    counterSet.inc(ICounterSet.COMPARE);
                    counterSet.inc(ICounterSet.ASSIGN, array.size() - index);
                    break;
                } else {
                    counterSet.inc(ICounterSet.COMPARE);
                }
            }
            if (index != array.size()){
                first = index;
                array.remove(index);
            } else {
                end = true;
            }

        }
    }
}
