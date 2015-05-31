package com.vsu.amm.data.storage;

import java.util.HashMap;
import java.util.Map;

import com.vsu.amm.stat.ICounterSet;
import com.vsu.amm.stat.ICounterSet.OperationType;
import com.vsu.amm.stat.SimpleCounterSet;

/**
 * Created by VLAD on 26.03.14.
 */
public class HashTable extends AbstractStorage {
    private static final String HASH_TABLE_STORAGE_PARAM_PREFIX = "storage.";
    private static final String HASH_TABLE_STORAGE_CLASS_PARAM_NAME = "class";
    private static final String HASH_TABLE_SIZE_PARAM_NAME = "hash_table_size";
    private static final int DEFAULT_HASH_TABLE_SIZE = 100;
    private static final String DEFAULT_STORAGE_CLASS = "com.vsu.amm.data.storage.SimpleList";
    private final IDataStorage[] hashTable;
    private final int hashTableSize;
    private Map<String, String> storageParams;
    private String storageClass;



    public HashTable() {
        this.hashTableSize = DEFAULT_HASH_TABLE_SIZE;
        this.storageClass = DEFAULT_STORAGE_CLASS;
        this.hashTable = new IDataStorage[hashTableSize];
    }
    
    @Override
	public IDataStorage cloneDefault() {
    	HashTable s=new HashTable();
    	s.setCounterSet(new SimpleCounterSet());
		return s;
	}

    @Override
    public void uncheckedInsert(int value) {
        set(value);
    }

    @Override
    public String getStorageName() {
        return "Hash Table of " + StorageGenerator.getDataStorage(storageClass, null).getStorageName();
    }

    public HashTable(Map<String, String> params) {

        if (params.containsKey(HASH_TABLE_SIZE_PARAM_NAME)) {
            hashTableSize = Integer.parseInt(params.get(HASH_TABLE_SIZE_PARAM_NAME));
        } else {
            hashTableSize = DEFAULT_HASH_TABLE_SIZE;
        }

        this.storageClass = DEFAULT_STORAGE_CLASS;
        this.storageParams = new HashMap<>();
        params.keySet().stream().filter(param -> param.startsWith(HASH_TABLE_STORAGE_PARAM_PREFIX)).forEach(param -> {
            if (param.endsWith(HASH_TABLE_STORAGE_CLASS_PARAM_NAME)) {
                storageClass = params.get(param);
            } else {
                storageParams.put(param.substring(param.indexOf(".")), params.get(param));
            }
        });
        this.hashTable = new IDataStorage[hashTableSize];
    }

    @Override
    public void clear() {
        super.clear();
        for (int i = 0; i < hashTableSize; i++)
            if (hashTable[i] != null)
                hashTable[i].clear();
    }

    @Override
    public boolean get(int value) {
        int key = new Integer(value).hashCode() % hashTableSize;
        counterSet.inc(OperationType.CALCULATION);
        if (hashTable[key] == null)
            return false;
        return hashTable[key].get(value);
    }

    @Override
    public boolean set(int value) {
        int key = new Integer(value).hashCode() % hashTableSize;
        counterSet.inc(OperationType.CALCULATION);
        //элемента не существует в хранилище
        if (hashTable[key] == null) {
            hashTable[key] = StorageGenerator.getDataStorage(storageClass, null);
            hashTable[key].setCounterSet(counterSet);
        }
        return hashTable[key].set(value);
    }

    @Override
    public boolean remove(int value) {
        int key = new Integer(value).hashCode() % hashTableSize;
        counterSet.inc(OperationType.CALCULATION);
        if (hashTable[key] == null)
            return false;
        return hashTable[key].remove(value);
    }

}
