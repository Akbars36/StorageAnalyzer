package com.vsu.amm.stat;

import java.util.Hashtable;

/**
 * Created with IntelliJ IDEA.
 * User: vlzo0513
 * Date: 12/19/13
 * Time: 12:06 PM
 * To change this template use File | Settings | File Templates.
 */
public class SimpleCounterSet implements ICounterSet {

    private final Hashtable<OperationType, Integer> counterSet;

    public SimpleCounterSet() {
        counterSet = new Hashtable<>();
        for (OperationType operation : OperationType.values())
            counterSet.put(operation, 0);
    }

    @Override
    public void inc(OperationType counterName) {
        int value;
        if (counterSet.containsKey(counterName)) {
            value = counterSet.get(counterName);
            counterSet.put(counterName, value + 1);
        }
    }

    @Override
    public void inc(OperationType counterName, int delta) {
        int value;
        if (counterSet.containsKey(counterName)) {
            value = counterSet.get(counterName);
            counterSet.put(counterName, value + delta);
        }
    }

    @Override
    public int get(OperationType counterName) {
        if (counterSet.containsKey(counterName)) {
            return counterSet.get(counterName);
        }
        return -1;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void clear() {
        if (counterSet == null)
            return;
        for (OperationType operation : counterSet.keySet())
            counterSet.put(operation, 0);
    }
}
