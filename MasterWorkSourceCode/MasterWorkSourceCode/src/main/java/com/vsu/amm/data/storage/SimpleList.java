package com.vsu.amm.data.storage;

import com.vsu.amm.stat.ICounterSet;
import com.vsu.amm.stat.ICounterSet.OperationType;
import com.vsu.amm.stat.SimpleCounterSet;

import java.util.Map;

/**
 * Created by VLAD on 26.03.14.
 */
public class SimpleList extends AbstractStorage {

    private Node root;

    @Override
	public IDataStorage cloneDefault() {
    	SimpleList s=new SimpleList();
    	s.setCounterSet(new SimpleCounterSet());
		return s;
	}

    @Override
    public void uncheckedInsert(int value) {
        Node n = new Node(value);
        n.next = root;
        root = n;
        counterSet.inc(OperationType.ASSIGN);
    }

    @Override
    public String getStorageName() {
        return "Simple List";
    }

    public SimpleList() {
        this.root = null;
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
	public boolean get(int value) {
		Node tmp = root;
		while (tmp != null) {
			counterSet.inc(OperationType.COMPARE);
			if (tmp.value == value) {
				return true;
			}
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
        Node tmp = root;
        while (tmp != null) {
            counterSet.inc(OperationType.COMPARE);
            if (tmp.value == value)
                return false;
            tmp = tmp.next;
        }
        counterSet.inc(OperationType.ASSIGN);
        Node n = new Node(value);
        n.next = root;
        root = n;
        return true;
    }

    @Override
    public boolean remove(int value) {
        Node curr = root;
        Node prev = null;
        while (curr != null) {
            counterSet.inc(OperationType.COMPARE);
            if (curr.value == value) {
                if (curr == root)
                    root = root.next;
                else
                    prev.next = curr.next;
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
