package com.vsu.amm.data.storage;

import com.vsu.amm.stat.ICounterSet;
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
        if (root == null)
        	return;
        
        Node tmp = root.next;
        Node next;
        while (tmp != null) {
            next = tmp.next;
            tmp = next;
        }
        root = null;

    }

    @Override
    public void get(int value) {
        if (getFromCache(value))
            return;
        Node tmp = root;
        boolean found = false;
        while ((tmp != null) && !found) {
            counterSet.inc(ICounterSet.OperationType.COMPARE);
            if (tmp.value == value) {
                found = true;
            }
            tmp = tmp.next;
        }
    }

    @Override
    public void set(int value) {
        super.set(value);
        if (root != null) {
            Node curr = root;
            Node prev = null;
            while ((curr != null) && (curr.value < value)) {
                counterSet.inc(ICounterSet.OperationType.COMPARE);
                prev = curr;
                curr = curr.next;
            }
            counterSet.inc(ICounterSet.OperationType.COMPARE);
            counterSet.inc(ICounterSet.OperationType.ASSIGN);
            Node node = new Node(value);
            if (prev == null) {
                node.next = root;
                root = node;
            } else {
                node.next = curr;
                prev.next = node;
            }
        } else {
            counterSet.inc(ICounterSet.OperationType.ASSIGN);
            root = new Node(value);
        }
    }

    @Override
	public IDataStorage cloneDefault() {
    	SortedList s=new SortedList();
    	s.setCounterSet(new SimpleCounterSet());
		return s;
	}
    @Override
    public void remove(int value) {
        super.remove(value);
        Node curr = root;
        Node prev = null;
        while (curr != null) {
            counterSet.inc(ICounterSet.OperationType.COMPARE);
            if (curr.value == value) {
                if (curr == root) {
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
