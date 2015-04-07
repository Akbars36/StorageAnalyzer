package com.vsu.amm.data.storage;

import com.vsu.amm.stat.ICounterSet;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;


public class SortedArray implements IDataStorage {

    ArrayList<Integer> array;
    Map<String, String> params;

    ICounterSet counterSet;

    public SortedArray(){
        array = new ArrayList<>();
    }

    public SortedArray(Map<String, String> params){
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
        int index = 0;
        if (array.size() == 0){
            array.add(value);
            counterSet.inc(ICounterSet.ASSIGN);
            return;
        } else {
            while((index < array.size()) && (array.get(index) < value)){
                counterSet.inc(ICounterSet.COMPARE);
                index++;
            }
            if (index != array.size()){
                counterSet.inc(ICounterSet.COMPARE);
            }
            if (index == array.size()){
                array.add(value);
                counterSet.inc(ICounterSet.ASSIGN);
            } else {
                array.add(index, value);
                counterSet.inc(ICounterSet.ASSIGN, array.size() - index + 1);
            }

        }
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
