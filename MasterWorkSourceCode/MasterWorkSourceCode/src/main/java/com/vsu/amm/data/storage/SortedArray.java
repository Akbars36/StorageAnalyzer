package com.vsu.amm.data.storage;

import com.vsu.amm.stat.ICounterSet.OperationType;
import com.vsu.amm.stat.SimpleCounterSet;


public class SortedArray extends AbstractStorage {

    private static final int DEFAULT_ARRAY_SIZE = 50;
    private int itemsCount = 0;
    private int[] array;

    public SortedArray() {
        array = new int[DEFAULT_ARRAY_SIZE];
    }

    public SortedArray(int initialSize) {
        array = new int[initialSize > 0 ? initialSize : DEFAULT_ARRAY_SIZE];
    }

    @Override
    public void clear() {
        super.clear();
        itemsCount = 0;
    }

    private void expandArray(int value, int position) {
        int[] tmpArray = new int[array.length * 2 + 1];
        for (int i = 0; i < position; i++) {
            tmpArray[i] = array[i];
            counterSet.inc(OperationType.ASSIGN);
        }
        counterSet.inc(OperationType.ASSIGN);
        tmpArray[position] = value;
        for (int i = position + 1; i < itemsCount; i++) {
            counterSet.inc(OperationType.ASSIGN);
            tmpArray[i] = array[i - 1];
        }
        array = tmpArray;
        itemsCount++;
    }

    private void insertAtPosition(int value, int position) {
        if (itemsCount == array.length) {
            expandArray(value, position);
            return;
        }

        for (int i = itemsCount; i > position; i--) {
            counterSet.inc(OperationType.ASSIGN);
            array[i] = array[i-1];
        }
        counterSet.inc(OperationType.ASSIGN);
        array[position] = value;
        itemsCount++;
    }

    @Override
    public IDataStorage cloneDefault() {
        SortedArray s = new SortedArray();
        s.setCounterSet(new SimpleCounterSet());
        return s;
    }

    @Override
    public void uncheckedInsert(int value) {
        set(value);
    }

    @Override
    public String getStorageName() {
        return "Sorted Array";
    }

    @Override
    public boolean get(int value) {
        counterSet.inc(OperationType.COMPARE);
        if (value < array[0])
            return false;
        counterSet.inc(OperationType.COMPARE);
        if (value > array[itemsCount - 1])
            return false;

        int first = 0;
        int last = itemsCount - 1;

        while (first < last) {
            counterSet.inc(OperationType.COMPARE);
            int mid = first + (last - first) / 2;
            if (array[mid] == value)
                return true;
            if (array[mid] < value)
                last = mid;
            else first = mid + 1;
        }
        return false;
    }

    @Override
    public boolean set(int value) {
        if (itemsCount == 0) {
            insertAtPosition(value, 0);
            return true;
        }

        counterSet.inc(OperationType.COMPARE);
        if (value < array[0]) {
            insertAtPosition(value, 0);
            return true;
        }

        counterSet.inc(OperationType.COMPARE);
        if (value > array[itemsCount - 1]) {
            insertAtPosition(value, itemsCount);
            return true;
        }

        int first = 0;
        int last = itemsCount - 1;

        while (first < last) {
            counterSet.inc(OperationType.COMPARE);
            int mid = first + (last - first) / 2;
            if (array[mid] == value)
                return false;
            if (array[mid] < value)
                last = mid;
            else first = mid + 1;
        }
        insertAtPosition(value, first);
        return true;
    }

    @Override
    public boolean remove(int value) {
        counterSet.inc(OperationType.COMPARE);
        if (value < array[0])
            return false;

        counterSet.inc(OperationType.COMPARE);
        if (value > array[itemsCount - 1]) {
            return false;
        }

        int first = 0;
        int last = itemsCount - 1;
        int remIndex = -1;

        while (first < last) {
            counterSet.inc(OperationType.COMPARE);
            int mid = first + (last - first) / 2;
            if (array[mid] == value) {
                remIndex = mid;
                break;
            }
            if (array[mid] < value)
                last = mid;
            else first = mid + 1;
        }
        if (remIndex == -1)
            return false;

        for (int i = remIndex + 1; i < itemsCount; i++) {
            counterSet.inc(OperationType.ASSIGN);
            array[i - 1] = array[i];
        }

        itemsCount--;
        return true;
    }
}
