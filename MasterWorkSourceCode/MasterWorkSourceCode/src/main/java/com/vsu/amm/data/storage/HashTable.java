package com.vsu.amm.data.storage;

import java.util.HashMap;
import java.util.Map;

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
    private final Map<Integer, IDataStorage> hashTable;
    private final int hashTableSize;
    private Map<String, String> storageParams;
    private String storageClass;

    public HashTable() {
        this.hashTableSize = DEFAULT_HASH_TABLE_SIZE;
        this.storageClass = DEFAULT_STORAGE_CLASS;
        this.hashTable = new HashMap<>(hashTableSize);
    }
    
    @Override
	public IDataStorage cloneDefault() {
    	HashTable s=new HashTable();
    	s.setCounterSet(new SimpleCounterSet());
		return s;
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
        this.hashTable = new HashMap<>(hashTableSize);
    }

    @Override
    public void clear() {
        super.clear();
        hashTable.clear();
    }

    @Override
    public void get(int value) {
        if (getFromCache(value))
            return;
        int key = value / hashTableSize;
        if (hashTable.containsKey(key)) {
            hashTable.get(key).get(value);
        }
    }

    @Override
    public void set(int value) {
        super.set(value);
        int key = value / hashTableSize;
        if (hashTable.containsKey(key)) {
            hashTable.get(key).set(value);
        } else {
            IDataStorage temp = StorageGenerator.getDataStorage(storageClass, null, null);
            temp.setCounterSet(counterSet);
            temp.set(value);
            hashTable.put(key, temp);
        }

    }

    @Override
    public void remove(int value) {
        super.remove(value);
        int key = value / hashTableSize;
        if (hashTable.containsKey(key)) {
            hashTable.get(key).remove(value);
        }
    }
}
