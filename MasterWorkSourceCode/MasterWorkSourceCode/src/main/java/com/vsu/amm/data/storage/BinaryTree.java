package com.vsu.amm.data.storage;

import com.vsu.amm.stat.ICounterSet.OperationType;
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

	@Override
	public void uncheckedInsert(int value) {
		//вставка элемента без проверки на существование не отличается от обычной вставки
		insert(root, value);
	}

	@Override
	public String getStorageName() {
		return "Binary Tree";
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

	private boolean find(Node node, int value) {
		if (node == null)
			return false;

		counterSet.inc(OperationType.COMPARE);

		if (node.value == value)
			return true;

		return find(value < node.value ? node.left : node.right,
				value);
	}

	@Override
	public boolean get(int value) {
		return find(root, value);
	}

	private boolean insert(Node node, int value) {
		counterSet.inc(OperationType.COMPARE);
		if (node.value == value)
			return false;

		if (node.value < value)
			if (node.left == null) {
				node.left = new Node(value);
				node.left.parent = node;
				counterSet.inc(OperationType.ASSIGN);
				return true;
			} else
				return insert(node.left, value);

		if (node.right == null) {
			node.right = new Node(value);
			node.right.parent = node;
			counterSet.inc(OperationType.ASSIGN);
			return true;
		} else
			return insert(node.right, value);
	}

	@Override
	public boolean set(int value) {
		if (root == null) {
			counterSet.inc(OperationType.ASSIGN);
			root = new Node(value);
			return true;
		}
		return insert(root, value);
	}

	Node next(Node node) {
		if (node == null)
			return null;

		Node tmp = node;
		while (tmp.left != null)
			tmp = tmp.left;


		return node;
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
	private boolean delete(Node node, int value) {

		Node parent = null;
		boolean isLeft = false;

		while (node != null && node.value != value) {
			counterSet.inc(OperationType.COMPARE);
			parent = node;
			isLeft = node.value > value;
			if (isLeft)
				node = node.left;
			else
				node = node.right;
		}

		//узла нет – искомый элемент отсутствует в дереве
		if (node == null)
			return false;

		//узел – лист
		if (node.left == null && node.right == null) {
			if (parent == null)
				root = null;
			else
				if (isLeft)
					parent.left = null;
				else
					parent.right = null;
			return true;
		}

		if (node.left != null && node.right != null) {
			//ищем элемент на замену
			Node x = next(node.right);
			if (x == null)
				return false;
			//удаляем найденный элемент из своего поддерева
			if (x.parent.right == x)
				x.parent.right = x.right;
			else
				x.parent.left = x.right;
			x.left = node.left;
			x.right = node.right;
			x.parent = node.parent;
			if (parent != null)
				if (isLeft)
					parent.left = x;
				else
					parent.right = x;
			else
				root = x;
			return true;
		}

		if (node.left != null) {
			if (parent != null)
				if (isLeft)
					parent.left = node.left;
				else
					parent.right = node.left;
			else
				root = node.left;

			node.left.parent = parent;

			return true;
		} else {
			if (parent != null)
				if (isLeft)
					parent.left = node.right;
				else
					parent.right = node.right;
			else
				root = node.right;

			node.right.parent = parent;

			return true;
		}
	}

	@Override
	public boolean remove(int value) {
		return delete(root, value);
	}

	class Node {
		int value;
		Node right;
		Node left;
		Node parent;

		public Node(int value) {
			this.value = value;
			right = null;
			left = null;
			parent = null;
		}
	}
}
