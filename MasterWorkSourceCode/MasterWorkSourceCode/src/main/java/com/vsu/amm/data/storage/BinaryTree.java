package com.vsu.amm.data.storage;

import com.vsu.amm.stat.ICounterSet;
import com.vsu.amm.stat.SimpleCounterSet;

import java.util.Map;

/**
 * Created by VLAD on 26.03.14.
 */
public class BinaryTree extends AbstractStorage {
	private Node root;

	public BinaryTree() {
		this.root = null;
	}

	@Override
	public IDataStorage cloneDefault() {
		BinaryTree s = new BinaryTree();
		s.setCounterSet(new SimpleCounterSet());
		return s;
	}

	public BinaryTree(Map<String, String> params) {
		super();
		this.root = null;
	}

	@Override
	public void clear() {
		super.clear();
		root = null;
	}

	private Node find(Node node, int value) {
		if (node == null) {
			return null;
		}
		if (node.getValue() == value) {
			counterSet.inc(ICounterSet.OperationType.COMPARE);
			return node;
		}
		return find(value < node.getValue() ? node.getLeft() : node.getRight(),
				value);
	}

	@Override
	public void get(int value) {
		if (getFromCache(value))
			return;

		find(root, value);
	}

	private Node insert(Node node, int value, Node parent) {
		if (node == null) {
			node = new Node(value);
			node.setParent(parent);
			counterSet.inc(ICounterSet.OperationType.ASSIGN);
		} else {
			counterSet.inc(ICounterSet.OperationType.COMPARE);
			if (value < node.getValue()) {
				node.setLeft(insert(node.getLeft(), value, node));
			} else {
				node.setRight(insert(node.getRight(), value, node));
			}
		}
		return node;
	}

	@Override
	public void set(int value) {
		super.set(value);
		root = insert(root, value, null);
	}

	private Node findMin(Node node) {
		Node min = node;
		if (min == null)
			return null;
		while (min.getLeft() != null) {
			min = min.getLeft();
		}
		return min;
	}

	/**
	 * 1. Удаление элемента без детей – просто освобождаем память. 2. Удаление
	 * элемента с одним ребенком – смена указателя родителя указывать директно к
	 * ребенку удаляемого элемента и освобождение памяти. 3. Удаление элемента с
	 * только одним ребенком и это КОРЕНЬ – перемещение ребенка на место корня и
	 * освобождение памяти. 4 Удаление элемента с двумя детьми – это самая
	 * сложная операция. Самый подходящий способ исполнения это разменять
	 * стоимости удаляемого элемента и максимальную стоимость левого поддерева
	 * или минимальную правого поддерева (потому что это сохранит характеристики
	 * дерева) и тогда удаляем элемент без или с одним ребенком.
	 *
	 * @param node
	 * @param value
	 * @return
	 */
	private Node delete(Node node, int value) {
		Node element;// = find(node, value);
		while ((element = find(node, value)) != null) {
			if (element == null)
				return node;

			boolean hasParent = element.getParent() != null;
			boolean isLeft = hasParent
					&& element.getValue() < element.getParent().getValue();
			counterSet.inc(ICounterSet.OperationType.COMPARE);

			if (element.getLeft() == null && element.getRight() == null) {
				if (hasParent) {
					if (isLeft) {
						element.getParent().setLeft(null);
					} else {
						element.getParent().setRight(null);
					}
					counterSet.inc(ICounterSet.OperationType.ASSIGN);
				}
			} else if (element.getLeft() != null && element.getRight() == null) {
				if (hasParent) {
					if (isLeft) {
						element.getParent().setLeft(element.getLeft());
					} else {
						element.getParent().setRight(element.getLeft());
					}
					counterSet.inc(ICounterSet.OperationType.ASSIGN);
				} else {
					node = element.getLeft();
				}
			} else if (element.getLeft() == null && element.getRight() != null) {
				if (hasParent) {
					if (isLeft) {
						element.getParent().setLeft(element.getRight());
					} else {
						element.getParent().setRight(element.getRight());
					}
				} else {
					node = element.getRight();
				}
				counterSet.inc(ICounterSet.OperationType.ASSIGN);
			} else {
				Node rightMin = findMin(element.getRight());
				element.setValue(rightMin.getValue());
				counterSet.inc(ICounterSet.OperationType.ASSIGN);
				return delete(rightMin, rightMin.getValue());
			}
		}
		return node;
	}

	@Override
	public void remove(int value) {
		super.remove(value);
		root = delete(root, value);
	}

	class Node {
		int value;
		Node right;
		Node left;
		Node parent;
		int height;

		public Node(int value) {
			this.value = value;
			right = null;
			left = null;
			parent = null;
		}

		public int getValue() {
			return value;
		}

		public void setValue(int value) {
			this.value = value;
		}

		public Node getLeft() {
			return left;
		}

		public void setLeft(Node left) {
			this.left = left;
		}

		public Node getRight() {
			return right;
		}

		public void setRight(Node right) {
			this.right = right;
		}

		public int getHeight() {
			return height;
		}

		public void setHeight(int height) {
			this.height = height;
		}

		public Node getParent() {
			return parent;
		}

		public void setParent(Node parent) {
			this.parent = parent;
		}
	}
}
