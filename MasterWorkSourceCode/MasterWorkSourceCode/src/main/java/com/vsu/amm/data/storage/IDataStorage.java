package com.vsu.amm.data.storage;

import com.vsu.amm.data.IDataContainer;
import com.vsu.amm.data.cache.AbstractCache;
import com.vsu.amm.stat.ICounterSet;

import java.util.Map;

/**
 * Created with IntelliJ IDEA. User: Влад Date: 13.11.13 Time: 20:25 To change
 * this template use File | Settings | File Templates.
 */
public interface IDataStorage extends IDataContainer {
	ICounterSet getCounterSet();

	void setCounterSet(ICounterSet counterSet);

	void setCache(AbstractCache cache);

	void setStorageParams(Map<String, Integer> params);

	void clear();

	/**
	 * Метод создающий копию со значениями по умолчанию
	 * 
	 * @return
	 */
	IDataStorage cloneDefault();
}
