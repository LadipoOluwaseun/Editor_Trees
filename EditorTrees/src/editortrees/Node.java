package editortrees;

import java.util.ArrayList;

import editortrees.EditTree.Container;

// A node in a height-balanced binary tree with rank.
// Except for the NULL_NODE (if you choose to use one), one node cannot
// belong to two different trees.

public class Node {

	enum Code {
		SAME, LEFT, RIGHT;
		// Used in the displayer and debug string
		public String toString() {
			switch (this) {
			case LEFT:
				return "/";
			case SAME:
				return "=";
			case RIGHT:
				return "\\";
			default:
				throw new IllegalStateException();
			}
		}
	}

	// The fields would normally be private, but for the purposes of this class,
	// we want to be able to test the results of the algorithms in addition to
	// the
	// "publicly visible" effects

	char element;
	Node left, right; // subtrees
	int rank; // inorder position of this node within its own subtree.
	Code balance;
	int size;
	int height;
	Node parent;

	// Feel free to add other fields that you find useful
	public static final Node NULL_NODE = new Node();

	public Node(Character data) {
		this.element = data;
		this.left = NULL_NODE;
		this.right = NULL_NODE;
		this.rank = 0;
		this.balance = Code.SAME;
		this.size = 1;
		this.height = 0;
		this.parent = NULL_NODE;
	}
	// You will probably want to add several other methods

	public Node() {
		this.left = null;
		this.right = null;
		this.height = -1;
		this.size = 0;

	}

	// For the following methods, you should fill in the details so that they
	// work correctly
	//TODO: update using balance codes
	public int height() {
		if (this == NULL_NODE) {
			return -1;
		}
		int leftHeight = this.left.height();
		int rightHeight = this.right.height();
		if (leftHeight > rightHeight) {
			return leftHeight + 1;
		} else {
			return rightHeight + 1;
		}
	}


	public int size() {
		if (this == NULL_NODE) {
			return 0;
		}
		return this.rank + this.right.size();
	}

	public ArrayList<Character> inOrder(ArrayList<Character> list) {
		if (left != NULL_NODE) {
			left.inOrder(list);
		}
		list.add(this.element);
		if (right != NULL_NODE) {
			right.inOrder(list);
		}
		return list;
	}

	public ArrayList<String> inOrderDebug(ArrayList<String> list) {
		String toAdd = new String();
		toAdd += this.element;
		toAdd += this.rank;
		toAdd += this.balance;
		list.add(toAdd);
		if (left != NULL_NODE) {
			left.inOrderDebug(list);
		}
		if (right != NULL_NODE) {
			right.inOrderDebug(list);
		}
		return list;
	}

	public Node add(char ch, int pos, Container container) throws IndexOutOfBoundsException {
		if (this == NULL_NODE) {
			Node newNode = new Node(ch);
			return newNode;
		}
		// TODO: add second case for position
		if (pos < 0) {
			throw new IndexOutOfBoundsException();
		}

		// add to right
		else if (pos > this.rank) {
			this.right = this.right.add(ch, (pos - rank - 1), container);
		}
		// add to left
		else if (pos <= this.rank) {
			rank++;
			this.left = this.left.add(ch, pos, container);
		}
		// call balance method
		// three cases left, right, and equal
//		this.height = 1 + Math.max(this.left.height(), this.right.height());
//
		this.updateBalanceCodes();
		this.checkBalance(container);
		return this;
	}

	public void add(char ch, Container container) {
		if (this.right == NULL_NODE) {
			this.right = new Node(ch);
		} else {
			this.right.add(ch, container);
		}
		// this.height = 1 + Math.max(this.left.height,
		// this.right.height);
		//
		this.updateBalanceCodes();
		this.checkBalance(container);
		

	}

	public Node singleRightRotate(Node node) {
		Node newRootNode = node.left;
		Node rightChildOfLeft = newRootNode.right;
		newRootNode.right = node;
		node.left = rightChildOfLeft;
		newRootNode.rank = newRootNode.left.size();
		newRootNode.height = newRootNode.height();
		node.height = node.height();
		newRootNode.balance = Code.SAME;
		node.balance = Code.SAME;
		return newRootNode;

	}

	public Node singleLeftRotate(Node node) {
		Node newRootNode = node.right;
		Node leftChildOfRight = newRootNode.left;
		newRootNode.left = node;
		node.right = leftChildOfRight;
		newRootNode.rank = newRootNode.left.size();
		newRootNode.height = newRootNode.height();
		node.height = node.height();
		newRootNode.balance = Code.SAME;
		node.balance = Code.SAME;
		return newRootNode;
	}

	// right-left rotation
	public Node doubleRightRotate(Node node) {
		node.right = singleRightRotate(node.right);
		return singleLeftRotate(node);
	}

	// left-right rotation
	public Node doubleLeftRotate(Node node) {
		node.left = singleRightRotate(node.left);
		return singleRightRotate(node);
	}

	public int getBalance(Node node) {
		if (node == NULL_NODE) {
			return 0;
		}
		return node.left.height - node.right.height;
	}
	public void updateBalanceCodes() {
		if (right.height - left.height == 1) {
			this.balance = Code.RIGHT;
		}
		else if (left.height - right.height == 0) {
			balance = Code.SAME;
		}
		else if (right.height - left.height == -1) {
			this.balance = Code.LEFT;
		}
//		Special Cases 
//		if (right.height - left.height < -1) {
//		this.balance = Code.SP_LEFT;
//	}
//	if (right.height - left.height > 1) {
//		this.balance = Code.SP_RIGHT;
//	}
	}
	public void checkBalance(Container container) {
		//Left Case
		if (this.balance.equals(Code.RIGHT) && this.right.balance.equals(Code.RIGHT)) {
			container.rotationCount++;
			singleRightRotate(this);
		}
		// Right Case
		if (this.balance.equals(Code.LEFT)  && this.left.balance.equals(Code.LEFT)) {
			container.rotationCount++;
			singleLeftRotate(this);
		}
		// Left Right Case
		if (this.balance.equals(Code.RIGHT) && this.rank > this.left.rank) {
			container.rotationCount = container.rotationCount + 2;
			doubleLeftRotate(this);
		}

		// Right Left Case
		if (this.balance.equals(Code.LEFT) && this.rank < this.right.rank) {
			container.rotationCount = container.rotationCount + 2;
			doubleRightRotate(this);
		}
	}

}