package com.vsu.amm.data.storage;

import com.vsu.amm.stat.ICounterSet;
import com.vsu.amm.stat.SimpleCounterSet;

import java.util.Map;

/**
 * Created by VLAD on 26.03.14.
 */
public class BTree implements IDataStorage  {
    private static final String CHILDREN_PAMAN_NAME = "children_count";
    private static final int MAX_CHILDREN_COUNT = 4;    // max children per B-tree node = MAX_CHILDREN_COUNT-1


    ICounterSet counterSet;
    Map<String, String> storageParams;

    private Node root;
    private int treeHeight;
    private int elementsCount;
    private int childrenCount;


    private static final class Node {
        private int m;                             // number of children
        private Entry[] children;// = new Entry[MAX_CHILDREN_COUNT];   // the array of children
        private Node(int k, int maxChildren) {
            children = new Entry[maxChildren];
            m = k;
        }             // create a node with k children
    }

    // internal nodes: only use key and next
    // external nodes: only use key and value
    private static class Entry {
        private int value;
        private Node next;     // helper field to iterate over array entries
        public Entry(int value, Node next) {
            this.value = value;
            this.next  = next;
        }
    }

    // constructor
    public BTree() {
        root = new Node(0, childrenCount);
    }

    public BTree(Map<String, String> params) {
        root = new Node(0, childrenCount);
        this.storageParams = params;
        if (storageParams != null && storageParams.containsKey(CHILDREN_PAMAN_NAME)){
            childrenCount = Integer.parseInt(storageParams.get(CHILDREN_PAMAN_NAME));
            if (childrenCount > MAX_CHILDREN_COUNT){
                childrenCount = MAX_CHILDREN_COUNT;
            }
        } else {
            childrenCount = MAX_CHILDREN_COUNT;
        }
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
        this.storageParams = params;
        if (storageParams != null && storageParams.containsKey(CHILDREN_PAMAN_NAME)){
            childrenCount = Integer.parseInt(storageParams.get(CHILDREN_PAMAN_NAME));
            if (childrenCount > MAX_CHILDREN_COUNT){
                childrenCount = MAX_CHILDREN_COUNT;
            }
        }
    }

    @Override
    public Map<String, String> getStorageParams() {
        return storageParams;
    }

    @Override
    public void clear() {
        root = new Node(0, childrenCount);
        counterSet = new SimpleCounterSet();
    }


    // return number of key-value pairs in the B-tree
    public int size() {
        return elementsCount;
    }

    // return height of B-tree
    public int height() {
        return treeHeight;
    }

    public void get(int value) {
        search(root, value, treeHeight);
    }

    private void search(Node x, int value, int ht) {
        Entry[] children = x.children;

        // external node
        if (ht == 0) {
            for (int j = 0; j < x.m; j++) {
                counterSet.inc(ICounterSet.COMPARE);
                if (value == children[j].value){
                    return;
                }
            }
        }
        // internal node
        else {
            for (int j = 0; j < x.m; j++) {
                if (j+1 == x.m || value < children[j+1].value){
                    if (j+1 != x.m){
                        counterSet.inc(ICounterSet.COMPARE);
                    }
                    search(children[j].next, value, ht-1);
                }
            }
        }
    }

    public void set(int value) {
        Node u = insert(root, value, treeHeight);
        elementsCount++;
        if (u == null) {
            return;
        }

        // need to split root
        Node t = new Node(2, childrenCount);
        t.children[0] = new Entry(root.children[0].value, root);
        t.children[1] = new Entry(u.children[0].value, u);
        root = t;
        treeHeight++;
    }

    @Override
    public void remove(int value) {

    }


    private Node insert(Node h, int value, int ht) {
        int j;
        Entry t = new Entry(value, null);

        // external node
        if (ht == 0) {
            for (j = 0; j < h.m; j++) {
                counterSet.inc(ICounterSet.COMPARE);
                if (value < h.children[j].value)
                    break;
            }
        }
        // internal node
        else {
            for (j = 0; j < h.m; j++) {
                if ((j+1 == h.m) || value < h.children[j+1].value) {
                    if (j+1 != h.m) {
                        counterSet.inc(ICounterSet.COMPARE);
                    }
                    Node u = insert(h.children[j++].next, value, ht-1);
                    if (u == null){
                        return null;
                    }
                    counterSet.inc(ICounterSet.ASSIGN);
                    t.value = u.children[0].value;
                    t.next = u;
                    break;
                }
            }
        }

        for (int i = h.m; i > j; i--) {
            h.children[i] = h.children[i-1];
        }

        h.children[j] = t;
        h.m++;
        if (h.m < childrenCount) {
            return null;
        } else {
            return split(h);
        }
    }

    // split node in half
    private Node split(Node h) {
        Node t = new Node(childrenCount /2, childrenCount);
        h.m = childrenCount /2;
        for (int j = 0; j < childrenCount /2; j++)
            t.children[j] = h.children[childrenCount /2+j];
        return t;
    }


}
