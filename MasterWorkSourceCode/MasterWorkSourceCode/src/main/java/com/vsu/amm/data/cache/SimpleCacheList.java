package com.vsu.amm.data.cache;

import com.vsu.amm.data.cache.CacheItem.CacheItemComparator;
import com.vsu.amm.data.cache.CacheItem.ItemState;
import com.vsu.amm.data.storage.IDataStorage;
import com.vsu.amm.stat.ICounterSet;
import com.vsu.amm.stat.ICounterSet.OperationType;
import com.vsu.amm.stat.SimpleCounterSet;

import java.util.Map;

/**
 * Кэш на основе связного списка.
 * Элементы в кэше отсортированы в соответствии с политикой замены элемента.
 * Таким образом как только размер кэша начнёт превышать максимальный размер, достаточно будет удалить первые элементы
 * Created by Nikita Skornyakov on 25.05.15
 */
public class SimpleCacheList extends AbstractCache {

    private Node head;
    private Node tail;

    @Override
	public IDataStorage cloneDefault() {
    	SimpleCacheList s=new SimpleCacheList();
    	s.setCounterSet(new SimpleCounterSet());
		return s;
	}

    @Override
    public String getStorageName() {
        String name = "";
        if (storage != null)
            name = storage.getStorageName() + " with ";

        return name + "Simple List " + comparator.toString() + "-cache";
    }

    @Override
    public void uncheckedInsert(int value) {
        if (storage != null)
            storage.uncheckedInsert(value);
    }

    @Override
    protected void shrinkCache() {

    }

    public SimpleCacheList() {
        this.tail = null;
        this.head = null;
    }

    @Override
    public void setStorageParams(Map<String, Integer> params) {

    }

    @Override
    public void clear() {
        super.clear();
        head = null;
	}

	@Override
	public boolean get(int value) {
        if (cacheSize <= 0)
            return storage != null && storage.get(value);

        waitForThread();

        Node prev = null;
		Node tmp = head;
		while (tmp != null) {
			counterSet.inc(OperationType.DEFERRED_COMPARE);
			if (tmp.getValue() == value) {
                update(prev);
                return true;
			}
            prev = tmp;
			tmp = tmp.next;
		}
        //элемент не найден
        boolean containsInStorage = false;
        if (storage != null)
            containsInStorage = storage.get(value);

        //если элемента в хранилище нет, то выходим.

        if (!containsInStorage)
            return false;

        insertInCache(value);
        return true;

	}

    @Override
    public boolean set(int value) {
        if (cacheSize <= 0)
            return storage != null && storage.set(value);

        waitForThread();

        waitingThread = new Thread() {
            @Override
            public void run() {
                //кэш пуст
                if (head == null)
                    return;

                //в кэше есть элементы, поэтому сначала ищем среди них
                if (head.getValue() == value) {
                    if (head.value.getState() == ItemState.DELETED)
                        head.value.setState(ItemState.NORMAL);
                    update(null);
                    return;
                }
                Node tmp = head;
                while (tmp.next != null) {
                    counterSet.inc(OperationType.DEFERRED_COMPARE);
                    //если элемент найден в кэше
                    if (tmp.next.getValue() == value) {
                        if (tmp.next.value.getState() == ItemState.DELETED)
                            tmp.next.value.setState(ItemState.NORMAL);
                        counterSet.inc(OperationType.DEFERRED_ASSIGN);
                        update(tmp);
                        return;
                    }
                    tmp = tmp.next;
                }
            }
        };
        waitingThread.start();
        insertInCache(value);
        return storage != null && storage.set(value);
    }

    @Override
    public boolean remove(int value) {
        waitForThread();

        if (head == null)
            return storage != null && storage.remove(value);

        Node tmp = head;
        Node prev = null;
        while (tmp != null) {
            if (tmp.getValue() == value) {
                if (tmp.value.getState() == ItemState.DELETED) {
                    update(prev);
                    return true;
                }
                else {
                    tmp.value.setState(ItemState.DELETED);
                    update(prev);
                    return storage != null && storage.remove(value);
                }
            }
            prev = tmp;
            tmp = tmp.next;
        }

        return storage != null && storage.remove(value);
    }

    @Override
    public void clearCache() {
        super.clearCache();
        head = null;
        tail = null;
    }

    //вставка элемента которого не существовало в кэше
    private void insertInCache(int value) {
        if (!shouldValueBeInserted())
            return;

        waitForThread();

        counterSet.inc(OperationType.DEFERRED_ASSIGN);
        //в отдельном потоке вставляем элемент
        waitingThread = new Thread() {
            @Override
            public void run() {

                while (cacheSize < itemsCount) {
                    if (head == tail) {
                        head = new Node(value);
                        tail = head;
                        return;
                    }
                    head = head.next;
                }

                if (head == null) {
                    counterSet.inc(OperationType.DEFERRED_ASSIGN);
                    head = new Node(value);
                    tail = head;
                    itemsCount = 1;
                    return;
                }
                //вставляем элемент в соотвествии с с политикой записи
                //для MRU и LFU в начало
                //для LRU в конец
                if (comparator == CacheItemComparator.LRU)
                    tail.next = new Node(value);
                else {
                    Node n = new Node(value);
                    n.next = head;
                    head = n;
                }
                if (cacheSize > itemsCount)
                    itemsCount++;
            }
        };
        waitingThread.start();
    }

    /**
     *перемещение узла в соответствии с политикой записи кэша
     *@param prev элемент перед перемещаемым
     */
    private void update (Node prev) {
        waitForThread();

        waitingThread = new Thread() {
            @Override
            public void run() {

                Node p = prev;

                //если элемент один, то не трогаем его
                if (head == tail) {
                    head.value.update();
                    return;
                }
                //перемещаем голову
                if (p == null) {
                    head.value.update();
                    if (comparator == CacheItemComparator.MRU)
                        return;
                    if (comparator == CacheItemComparator.LRU) {
                        Node tmp = head;
                        tail.next = tmp;
                        head = head.next;
                        tmp.next = null;
                        return;
                    }
                    counterSet.inc(OperationType.DEFERRED_COMPARE);
                    if (comparator.compare(head.value, head.next.value) < 0)
                        return;
                    //вытаскиваем узел из списка
                    Node tmp = head;
                    head = head.next;
                    Node n = head;
                    //тащим его на свою позицию
                    while (n.next != null && comparator.compare(tmp.value, n.next.value) > 0) {
                        counterSet.inc(OperationType.DEFERRED_COMPARE);
                        n = n.next;
                    }
                    tmp.next = n.next;
                    n.next = tmp;
                    return;
                }

                Node tmp = p.next;
                p.next = tmp.next;

                //элемент внутри списка
                //перемещаем в начало
                if (comparator == CacheItemComparator.MRU) {
                    tmp.next = head;
                    head = tmp;
                    return;
                }

                //перемещаем в конец
                if (comparator == CacheItemComparator.LRU) {
                    tmp.next = null;
                    tail.next = tmp;
                    tail = tmp;
                    return;
                }

                //перемещаем внутри списка
                while (p.next != null && comparator.compare(tmp.value, p.next.value) > 0) {
                    counterSet.inc(OperationType.DEFERRED_COMPARE);
                    p = p.next;
                }
                tmp.next = p.next;
                p.next = tmp;
            }
        };

        waitingThread.start();
    }

    class Node {
        CacheItem value;
        Node next;

        public Node(int value) {
            this.value = new CacheItem(value);
            this.next = null;
        }

        public int getValue() {
            return value.getValue();
        }

        public void setValue(int value) {
            this.value.setValue(value);
        }

        public Node getNext() {
            return next;
        }

        public void setNext(Node next) {
            this.next = next;
        }
    }
}
