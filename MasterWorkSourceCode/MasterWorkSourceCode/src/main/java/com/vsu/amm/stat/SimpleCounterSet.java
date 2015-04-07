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

    Hashtable<String, Integer> counterSet;

    public SimpleCounterSet(){
        counterSet = new Hashtable<>();
        counterSet.put(COMPARE, 0);
        counterSet.put(ASSIGN, 0);
    }

    @Override
    public void inc(String counterName) {
        //To change body of implemented methods use File | Settings | File Templates.
        int value;
        if (counterSet.containsKey(counterName)){
            value = counterSet.get(counterName);
            counterSet.put(counterName,  value + 1);
        }
    }

    @Override
    public void inc(String counterName, int delta) {
        //To change body of implemented methods use File | Settings | File Templates.
        int value;
        if (counterSet.containsKey(counterName)){
            value = counterSet.get(counterName);
            counterSet.put(counterName,  value + delta);
        }
    }

    @Override
    public int get(String counterName) {
        if (counterSet.containsKey(counterName)){
            return counterSet.get(counterName);
        }
        return -1;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
