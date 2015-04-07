package com.vsu.amm.data.storage;

import com.vsu.amm.Storage;
import com.vsu.amm.stat.ICounterSet;
import com.vsu.amm.stat.SimpleCounterSet;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by VLAD on 26.03.14.
 */
public class HashTable implements IDataStorage{
    private static final String HASH_TABLE_STORAGE_PARAM_PREFIX = "storage.";
    private static final String HASH_TABLE_STORAGE_CLASS_PARAM_NAME = "class";
    private static final String HASH_TABLE_SIZE_PARAM_NAME = "hash_table_size";
    private static final int DEFAULT_HASH_TABLE_SIZE = 100;
    private static final String DEFAULT_STORAGE_CLASS = "com.vsu.amm.data.storage.SimpleList";

    Map<Integer, IDataStorage> hashTable;
    int hashTableSize;

    Map<String, String> params;
    ICounterSet counterSet;
    String storageClass;
    Map<String, String> storageParams;

    public HashTable(){
        this.hashTableSize = DEFAULT_HASH_TABLE_SIZE;
        this.storageClass = DEFAULT_STORAGE_CLASS;
        this.storageParams = new HashMap<>();
        this.hashTable = new HashMap<>(hashTableSize);
    }

    public HashTable(Map<String, String> params){
        this.params = params;

        if(params.containsKey(HASH_TABLE_SIZE_PARAM_NAME)){
            hashTableSize = Integer.parseInt(params.get(HASH_TABLE_SIZE_PARAM_NAME));
        } else {
            hashTableSize = DEFAULT_HASH_TABLE_SIZE;
        }

        this.storageClass = DEFAULT_STORAGE_CLASS;
        this.storageParams = new HashMap<>();
        for (String param : params.keySet()){
            if (param.startsWith(HASH_TABLE_STORAGE_PARAM_PREFIX)){
                if (param.endsWith(HASH_TABLE_STORAGE_CLASS_PARAM_NAME)){
                    storageClass = params.get(param);
                } else {
                    storageParams.put(param.substring(param.indexOf(".")), params.get(param));
                }
            }
        }
        this.hashTable = new HashMap<>(hashTableSize);
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
        this.params = params;

    }

    @Override
    public Map<String, String> getStorageParams() {
        return params;
    }

    @Override
    public void clear() {
        hashTable.clear();
        counterSet = new SimpleCounterSet();
    }

    @Override
    public void get(int value) {
        int key = value / hashTableSize;
        if (hashTable.containsKey(key)){
            hashTable.get(key).get(value);
        }
    }

    @Override
    public void set(int value) {
        int key = value / hashTableSize;
        if (hashTable.containsKey(key)){
            hashTable.get(key).set(value);
        } else {
            Storage storage = new Storage(storageClass, storageParams);
            IDataStorage temp = storage.getStorage();
            temp.setCounterSet(counterSet);
            temp.set(value);
            hashTable.put(key, temp);
        }

    }

    @Override
    public void remove(int value) {
        int key = value / hashTableSize;
        if (hashTable.containsKey(key)){
            hashTable.get(key).remove(value);
        }
    }
}
