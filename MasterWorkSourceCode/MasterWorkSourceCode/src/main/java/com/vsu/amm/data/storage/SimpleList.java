package com.vsu.amm.data.storage;

import com.vsu.amm.stat.ICounterSet;

import java.util.Map;

/**
 * Created by VLAD on 26.03.14.
 */
public class SimpleList extends AbstractStorage {

    private Node root;

    public SimpleList() {
        this.root = null;
    }

    @Override
    public ICounterSet getCounterSet() {
        return counterSet;
    }

    @Override
    public void setCounterSet(ICounterSet counterSet) {
        this.counterSet = counterSet;
    }

    @Override
    public void setStorageParams(Map<String, Integer> params) {

    }

    @Override
    public void clear() {
    	if (root == null)
    		return;
    	
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
        if (root != null) {
            counterSet.inc(ICounterSet.OperationType.ASSIGN);
            Node node = new Node(value);
            node.next = root;
            root = node;
        } else {
            counterSet.inc(ICounterSet.OperationType.ASSIGN);
            root = new Node(value);
        }
    }

    @Override
    public void remove(int value) {
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
