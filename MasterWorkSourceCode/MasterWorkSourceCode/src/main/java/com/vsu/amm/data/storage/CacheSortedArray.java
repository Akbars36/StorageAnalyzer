package com.vsu.amm.data.storage;

import com.vsu.amm.stat.ICounterSet;

import java.util.*;

public class CacheSortedArray implements  IDataStorage {
    private static final int DEFAULT_CACHE_SIZE = 3;

    ArrayList<Integer> array;
    ICounterSet counterSet;
    int cacheSize;
    List<Integer> cache;
    Map<String, String> params;

    public CacheSortedArray(){
        array = new ArrayList<>();
        cacheSize = DEFAULT_CACHE_SIZE;
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
                counterSet.inc(ICounterSet.OperationType.COMPARE);
            }
            counterSet.inc(ICounterSet.OperationType.COMPARE);
            if (cache.size() == cacheSize){
                cache.remove(0);
            }
            cache.add(value);
        }
    }

    @Override
    public void set(int value) {
        int index = 0;
        if (array.size() == 0){
            array.add(value);
            counterSet.inc(ICounterSet.OperationType.ASSIGN);
            return;
        } else {
            while((index < array.size()) && (array.get(index) < value)){
                counterSet.inc(ICounterSet.OperationType.COMPARE);
                index++;
            }
            if (index != array.size()){
                counterSet.inc(ICounterSet.OperationType.COMPARE);
            }
            if (index == array.size()){
                array.add(value);
                counterSet.inc(ICounterSet.OperationType.ASSIGN);
            } else {
                array.add(index, value);
                counterSet.inc(ICounterSet.OperationType.ASSIGN, array.size() - index + 1);
            }

        }
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
                    counterSet.inc(ICounterSet.OperationType.COMPARE);
                    counterSet.inc(ICounterSet.OperationType.ASSIGN, array.size() - index);
                    break;
                } else {
                    counterSet.inc(ICounterSet.OperationType.COMPARE);
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
