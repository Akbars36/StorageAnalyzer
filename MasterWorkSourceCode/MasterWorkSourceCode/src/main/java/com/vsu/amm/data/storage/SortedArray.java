package com.vsu.amm.data.storage;

import com.vsu.amm.stat.ICounterSet;
import com.vsu.amm.stat.SimpleCounterSet;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;


public class SortedArray extends AbstractStorage {

    private final ArrayList<Integer> array;

    public SortedArray() {
        array = new ArrayList<>();
    }

    @Override
	public IDataStorage cloneDefault() {
    	SortedArray s=new SortedArray();
    	s.setCounterSet(new SimpleCounterSet());
		return s;
	}
    
    public SortedArray(Map<String, String> params) {
        array = new ArrayList<>();
    }

    @Override
    public void clear() {
        super.clear();
        array.clear();
    }


    @Override
    public void get(int value) {
        if (getFromCache(value))
            return;
        Iterator<Integer> iterator = array.iterator();
        while ((iterator.hasNext()) && (iterator.next() != value)) {
            counterSet.inc(ICounterSet.OperationType.COMPARE);
        }
        counterSet.inc(ICounterSet.OperationType.COMPARE);
    }

    @Override
    public void set(int value) {
        super.set(value);
        int index = 0;
        if (array.size() == 0) {
            array.add(value);
            counterSet.inc(ICounterSet.OperationType.ASSIGN);
        } else {
            while ((index < array.size()) && (array.get(index) < value)) {
                counterSet.inc(ICounterSet.OperationType.COMPARE);
                index++;
            }
            if (index != array.size()) {
                counterSet.inc(ICounterSet.OperationType.COMPARE);
            }
            if (index == array.size()) {
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
        super.remove(value);
        int index;
        int first = 0;
        boolean end = false;
        while (!end) {
            for (index = first; index < array.size(); index++) {
                if (array.get(index) == value) {
                    counterSet.inc(ICounterSet.OperationType.COMPARE);
                    counterSet.inc(ICounterSet.OperationType.ASSIGN, array.size() - index);
                    break;
                } else {
                    counterSet.inc(ICounterSet.OperationType.COMPARE);
                }
            }
            if (index != array.size()) {
                first = index;
                array.remove(index);
            } else {
                end = true;
            }
        }
    }
}
