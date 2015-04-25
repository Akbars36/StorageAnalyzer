package com.vsu.amm.data.storage;

import com.vsu.amm.data.IDataContainer;
import com.vsu.amm.stat.ICounterSet;

import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: Влад
 * Date: 13.11.13
 * Time: 20:25
 * To change this template use File | Settings | File Templates.
 */
public interface IDataStorage extends IDataContainer {
    public void setCounterSet(ICounterSet counterSet);
    public ICounterSet getCounterSet();
    public void setStorageParams(Map<String, String> params);
    public Map<String, String> getStorageParams();
    public void clear();
}
