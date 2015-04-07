package com.vsu.amm.data.storage;

import com.vsu.amm.stat.ICounterSet;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: Влад
 * Date: 13.11.13
 * Time: 20:21
 * To change this template use File | Settings | File Templates.
 */
public class SimpleArray implements IDataStorage {

    ArrayList<Integer> array;
    Map<String, String> params;

    ICounterSet counterSet;

    public SimpleArray(){
        array = new ArrayList<>();
    }

    public SimpleArray(Map<String, String> params){
        array = new ArrayList<>();
        this.params = params;

    }

    @Override
    public void setCounterSet(ICounterSet counterSet) {
        this.counterSet = counterSet;
    }

    @Override
    public ICounterSet getCounterSet() {
        return counterSet;
    }

    @Override
    public void setStorageParams(Map<String, String> params) {
        this.params = params;
    }

    @Override
    public Map<String, String> getStorageParams() {
        return params;
    }

    @Override
    public void clear() {
        array.clear();
    }

    @Override
    public void get(int value) {
        Iterator<Integer> iterator = array.iterator();
        while((iterator.hasNext()) && (iterator.next() != value)){
            counterSet.inc(ICounterSet.COMPARE);
        }
        counterSet.inc(ICounterSet.COMPARE);
    }

    @Override
    public void set(int value) {
        counterSet.inc(ICounterSet.ASSIGN);
        array.add(value);
    }

    @Override
    public void remove(int value) {
        int index = 0;
        int first = 0;
        boolean end = false;
        while(!end){
            for(index = first; index < array.size(); index++){
                if (array.get(index) == value){
                    counterSet.inc(ICounterSet.COMPARE);
                    counterSet.inc(ICounterSet.ASSIGN, array.size() - index);
                    break;
                } else {
                    counterSet.inc(ICounterSet.COMPARE);
                }
            }
            if (index != array.size()){
                first = index;
                array.remove(index);
            } else {
                end = true;
            }
        }
    }

}
