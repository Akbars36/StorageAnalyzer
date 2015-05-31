package com.vsu.amm.data.storage;

import com.vsu.amm.stat.ICounterSet;
import com.vsu.amm.stat.ICounterSet.OperationType;
import com.vsu.amm.stat.SimpleCounterSet;

/**
 * Created by VLAD on 26.03.14.
 */
public class SortedList extends AbstractStorage {
    private Node root;

    public SortedList() {
        this.root = null;
    }

    @Override
    public void clear() {
        super.clear();
        root = null;
    }

    @Override
    public boolean get(int value) {
        Node tmp = root;
        while (tmp != null) {
            counterSet.inc(OperationType.COMPARE);
            if (tmp.value == value)
                return true;
            tmp = tmp.next;
        }
        return false;
    }

    @Override
    public boolean set(int value) {
        if (root == null) {
            counterSet.inc(OperationType.ASSIGN);
            root = new Node(value);
            return true;
        }

        Node curr = root;
        Node prev = null;
        while ((curr != null) && (curr.value < value)) {
            counterSet.inc(OperationType.COMPARE);
            prev = curr;
            curr = curr.next;
        }

        counterSet.inc(OperationType.ASSIGN);
        if (curr != null && curr.value == value)
            return false;

        Node node = new Node(value);
        if (prev == null) {
            node.next = root;
            root = node;
        } else {
            node.next = curr;
            prev.next = node;
        }
        return true;
    }

    @Override
	public IDataStorage cloneDefault() {
    	SortedList s = new SortedList();
    	s.setCounterSet(new SimpleCounterSet());
		return s;
	}

    @Override
    public void uncheckedInsert(int value) {
        set(value);
    }

    @Override
    public String getStorageName() {
        return "Sorted List";
    }

    @Override
    public boolean remove(int value) {
        Node curr = root;
        Node prev = null;
        while (curr != null) {
            counterSet.inc(OperationType.COMPARE);
            if (curr.value == value) {
                if (curr == root) {
                    root = root.next;
                    curr = root;
                    prev = null;
                } else {
                    prev.next = curr.next;
                    curr = prev.next;
                }
                return true;
            } else {
                prev = curr;
                curr = curr.next;
            }
        }
        return false;
    }

    class Node {
        int value;
        Node next;

        public Node(int value) {
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
}
