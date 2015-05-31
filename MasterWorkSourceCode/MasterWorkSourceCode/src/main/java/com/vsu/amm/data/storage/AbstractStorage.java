package com.vsu.amm.data.storage;

import com.vsu.amm.stat.ICounterSet;
import com.vsu.amm.stat.SimpleCounterSet;

import java.util.Map;
import java.util.Random;

/**
 * Created by Nikita Skornyakov on 11.05.2015.
 */
public abstract class AbstractStorage implements IDataStorage {

    protected ICounterSet counterSet = new SimpleCounterSet();
    protected IDataStorage innerStorage;
    protected int insertRate = 100;
    protected int size = -1;
    private  static final Random rnd = new Random();

    @Override
    public ICounterSet getCounterSet() {
        return counterSet;
    }

    @Override
    public void setCounterSet(ICounterSet counterSet) {
        this.counterSet = counterSet;
    }

    @Override
    public void setStorageParams(Map<String, Integer> params) {
        if (innerStorage != null)
            innerStorage.setStorageParams(params);
    }

    @Override
    public void clear() {
        if (innerStorage != null)
            innerStorage.clear();
        counterSet.clear();
    }

    public IDataStorage cloneDefault(){
		return null;
    }

    /**
     * проверяет нужно ли вставлять элемент в хранилище
     * @return
     */
    protected boolean shouldInsertItem() {
        if (insertRate == 100)
            return true;

        return insertRate < rnd.nextInt(100);
    }
}
