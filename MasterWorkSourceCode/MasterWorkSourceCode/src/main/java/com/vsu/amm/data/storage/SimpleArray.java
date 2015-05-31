package com.vsu.amm.data.storage;

import com.vsu.amm.stat.ICounterSet;
import com.vsu.amm.stat.ICounterSet.OperationType;
import com.vsu.amm.stat.SimpleCounterSet;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created with IntelliJ IDEA.
 * User: Влад
 * Date: 13.11.13
 * Time: 20:21
 * To change this template use File | Settings | File Templates.
 */
public class SimpleArray extends AbstractStorage {

    private static final int DEFAULT_ARRAY_SIZE = 50;
    private int itemsCount = 0;
    private int[] array;


    public SimpleArray() {
        array = new int[DEFAULT_ARRAY_SIZE];
    }

    public SimpleArray(int initialSize) {
        if (initialSize > 0)
            array = new int[initialSize];
        else
            array = new int[DEFAULT_ARRAY_SIZE];
    }

    @Override
    public void clear() {
        super.clear();
        itemsCount = 0;
    }

    private void expandArray() {
        int[] tmpArray = new int[array.length * 2 + 1];
        for (int i = 0; i < array.length; i++) {
            tmpArray[i] = array[i];
            counterSet.inc(OperationType.ASSIGN);
        }
        array = tmpArray;
    }
    
    @Override
	public IDataStorage cloneDefault() {
    	SimpleArray s=new SimpleArray();
    	s.setCounterSet(new SimpleCounterSet());
		return s;
	}

    @Override
    public void uncheckedInsert(int value) {
        if (itemsCount == array.length)
            expandArray();
        array[itemsCount] = value;
        itemsCount++;
        counterSet.inc(OperationType.ASSIGN);
    }

    @Override
    public String getStorageName() {
        return "Simple Array";
    }

    @Override
    public boolean get(int value) {
        for (int i = 0; i < itemsCount; i++) {
            counterSet.inc(OperationType.COMPARE);
            if (array[i] == value)
                return true;
        }
        return false;
    }

    @Override
    public boolean set(int value) {
        for (int i = 0; i < itemsCount; i++) {
            counterSet.inc(OperationType.COMPARE);
            if (array[i] == value)
                return false;
        }
        //элемент не найден, добавляем его в конец массива
        if (itemsCount == array.length)
            expandArray();
        array[itemsCount] = value;
        counterSet.inc(OperationType.ASSIGN);
        itemsCount++;
        return true;
    }

    @Override
    public boolean remove(int value) {
        int index = -1;
        int first = 0;
        for (int i = 0; i < itemsCount; i++) {
            counterSet.inc(OperationType.COMPARE);
            //элемент найден
            if (array[i] == value) {
                index = i;
                break;
            }
        }

        if (index == -1)
            return false;

        for (int i = index + 1; i < itemsCount; i++) {
            counterSet.inc(OperationType.ASSIGN);
            array[i - 1] = array[i];
        }
        itemsCount--;
        return true;
    }

}
