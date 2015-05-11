package com.vsu.amm.data.storage;

import com.vsu.amm.stat.ICounterSet;

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

    private final ArrayList<Integer> array;

    public SimpleArray() {
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
        counterSet.inc(ICounterSet.OperationType.ASSIGN);
        array.add(value);
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
