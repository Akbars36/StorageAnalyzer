package com.vsu.amm.data.storage;

import com.vsu.amm.stat.ICounterSet;

import java.util.Map;

/**
 * Created by VLAD on 26.03.14.
 */
public class SortedList implements IDataStorage {
    private ICounterSet counterSet;
    private Node root;

    class Node {
        int value;
        Node next;

        public Node(int value){
            this.value = value;
            this.next = null;
        }

        public int getValue() {
            return value;
        }

        public void setValue(int value) {
            this.value = value;
        }

        public Node getNext() {
            return next;
        }

        public void setNext(Node next) {
            this.next = next;
        }
    }

    public SortedList(){
        this.root = null;
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

    }

    @Override
    public Map<String, String> getStorageParams() {
        return null;
    }

    @Override
    public void clear() {
        Node tmp = root.next;
        Node next;
        while(tmp != null){
            next = tmp.next;
            tmp = next;
        }
        root = null;

    }

    @Override
    public void get(int value) {
        Node tmp = root;
        boolean found = false;
        while ((tmp != null) && !found ){
            counterSet.inc(ICounterSet.COMPARE);
            if (tmp.value == value){
                found = true;
            }
            tmp = tmp.next;
        }
    }

    @Override
    public void set(int value) {
        if (root != null){
            Node curr = root;
            Node prev = null;
            while ((curr != null) && (curr.value < value)){
                counterSet.inc(ICounterSet.COMPARE);
                prev = curr;
                curr = curr.next;
            }
            counterSet.inc(ICounterSet.COMPARE);
            counterSet.inc(ICounterSet.ASSIGN);
            Node node = new Node(value);
            if (prev == null){
                node.next = root;
                root = node;
            } else {
                node.next = curr;
                prev.next = node;
            }
        } else {
            counterSet.inc(ICounterSet.ASSIGN);
            root = new Node(value);
        }
    }

    @Override
    public void remove(int value) {
        Node curr = root;
        Node prev = null;
        while (curr != null){
            counterSet.inc(ICounterSet.COMPARE);
            if (curr.value == value){
                if (curr == root){
                    root = root.next;
                    curr = root;
                    prev = null;
                } else {
                    prev.next = curr.next;
                    curr = prev.next;
                }
            } else {
                prev = curr;
                curr = curr.next;
            }
        }
    }
}
