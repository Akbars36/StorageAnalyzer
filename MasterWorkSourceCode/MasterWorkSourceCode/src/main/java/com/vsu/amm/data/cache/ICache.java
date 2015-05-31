package com.vsu.amm.data.cache;

import com.vsu.amm.data.storage.IDataStorage;

/**
 * Created by Nikita Skornyakov on 17.05.2015.
 */
public interface ICache extends IDataStorage {

    int getSize();
    void setSize(int size);
    void clearCache();
    int getInsertionRate();
    void setInsertionRate(int insertionRate);
    IDataStorage getInnerStorage();
    void setInnerStorage(IDataStorage storage);
}
