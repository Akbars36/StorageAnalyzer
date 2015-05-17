package com.vsu.amm.data.storage;

import com.vsu.amm.stat.ICounterSet;
import com.vsu.amm.stat.SimpleCounterSet;

import java.util.Map;

/**
 * Created by VLAD on 26.03.14.
 */
public class BTree extends AbstractStorage {
    private static final String CHILDREN_PAMAN_NAME = "children_count";
    private static final int MAX_CHILDREN_COUNT = 4;    // max children per B-tree node = MAX_CHILDREN_COUNT-1

    private Node root;
    private int treeHeight;
    private int elementsCount;
    private int childrenCount = MAX_CHILDREN_COUNT;


    // constructor
    public BTree() {
        root = new Node(0, childrenCount);
    }
    
    @Override
	public IDataStorage cloneDefault() {
    	BTree s=new BTree();
    	s.setCounterSet(new SimpleCounterSet());
		return s;
	}


    public BTree(Map<String, Integer> params) {
        super();
        root = new Node(0, childrenCount);
        if (params != null && params.containsKey(CHILDREN_PAMAN_NAME)) {
            childrenCount = params.get(CHILDREN_PAMAN_NAME);
            if (childrenCount > MAX_CHILDREN_COUNT) {
                childrenCount = MAX_CHILDREN_COUNT;
            }
        } else {
            childrenCount = MAX_CHILDREN_COUNT;
        }
    }

    @Override
    public void setStorageParams(Map<String, Integer> params) {
        super.setStorageParams(params);
        if (params != null && params.containsKey(CHILDREN_PAMAN_NAME)) {
            childrenCount = params.get(CHILDREN_PAMAN_NAME);
            if (childrenCount > MAX_CHILDREN_COUNT) {
                childrenCount = MAX_CHILDREN_COUNT;
            }
        }
    }

    @Override
    public void clear() {
        super.clear();
        root = new Node(0, childrenCount);
        elementsCount = 0;
        treeHeight = 0;
        
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
        if (getFromCache(value))
            return;
        search(root, value, treeHeight);
    }

    private void search(Node x, int value, int ht) {
        Entry[] children = x.children;

        // external node
        if (ht == 0) {
            for (int j = 0; j < x.m; j++) {
                counterSet.inc(ICounterSet.OperationType.COMPARE);
                if (value == children[j].value) {
                    return;
                }
            }
        }
        // internal node
        else {
            for (int j = 0; j < x.m; j++) {
                if (j + 1 == x.m || value < children[j + 1].value) {
                    if (j + 1 != x.m) {
                        counterSet.inc(ICounterSet.OperationType.COMPARE);
                    }
                    search(children[j].next, value, ht - 1);
                }
            }
        }
    }

    public void set(int value) {
        super.set(value);
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
        super.remove(value);
    }

    private Node insert(Node h, int value, int ht) {
        int j;
        Entry t = new Entry(value, null);

        // external node
        if (ht == 0) {
            for (j = 0; j < h.m; j++) {
                counterSet.inc(ICounterSet.OperationType.COMPARE);
                if (value < h.children[j].value)
                    break;
            }
        }
        // internal node
        else {
            for (j = 0; j < h.m; j++) {
                if ((j + 1 == h.m) || value < h.children[j + 1].value) {
                    if (j + 1 != h.m) {
                        counterSet.inc(ICounterSet.OperationType.COMPARE);
                    }
                    Node u = insert(h.children[j++].next, value, ht - 1);
                    if (u == null) {
                        return null;
                    }
                    t.value = u.children[0].value;
                    t.next = u;
                    break;
                }
            }
        }

        System.arraycopy(h.children, j, h.children, j + 1, h.m - j);
        counterSet.inc(ICounterSet.OperationType.ASSIGN, h.m - j);
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
        Node t = new Node(childrenCount / 2, childrenCount);
        h.m = childrenCount / 2;
        System.arraycopy(h.children, childrenCount / 2, t.children, 0, childrenCount / 2);
        return t;
    }

    private static final class Node {
        private final Entry[] children;// = new Entry[MAX_CHILDREN_COUNT];   // the array of children
        private int m;                             // number of children

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
            this.next = next;
        }
    }


}
