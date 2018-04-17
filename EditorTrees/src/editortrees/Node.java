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
	// we want to be able to test the results of the algorithms in addition to the
	// "publicly visible" effects
	
	char element;            
	Node left, right; // subtrees
	int rank;         // inorder position of this node within its own subtree.
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

	// For the following methods, you should fill in the details so that they work correctly
	public int heightHelper() {
		if (this.height == -1) {
			return -1;
		}
		
	    int leftHeight = this.left.heightHelper();
	    int rightHeight = this.right.heightHelper();

	    if (leftHeight > rightHeight) {
	        return leftHeight + 1;
	    } 
	    else {
	        return rightHeight + 1;
	    }	
	}

	public int sizeHelper() {
		if (this == NULL_NODE) {
			return 0;
		}
		return this.rank + this.right.sizeHelper();
	}

	public ArrayList<Character> inOrder(ArrayList<Character> list) {
		if(left != NULL_NODE) {
			left.inOrder(list);
		}
		list.add(this.element);
		if(right != NULL_NODE) {
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
		if(left != NULL_NODE) {
			left.inOrderDebug(list);
		}
		if(right != NULL_NODE) {
			right.inOrderDebug(list);
		}
		return list;
	}

	public Node addHelper(char ch, int pos, Container container) throws IndexOutOfBoundsException {
		if(this == NULL_NODE) {
			Node newNode = new Node(ch);
			return newNode;
		}
		//TODO: add second case for position 
		if(pos < 0) {
			throw new IndexOutOfBoundsException();
		}

		//add to right	
		else if (pos > this.rank) {
			this.right = this.right.addHelper(ch, (pos - rank - 1), container);
		}
		//add to left
		else if (pos <= this.rank ) {
			rank ++;
			this.left = this.left.addHelper(ch, pos, container);
		}
		//call balance method 
		//three cases left, right, and equal
//		this.height = 1 + Math.max(this.left.heightHelper(),
//                this.right.heightHelper());
//		
//		int balance = getBalance(this);
//		this.checkBalance(balance,container);
		return this;
	}

	public void addHelper(char ch, Container container) {
		if (this.right == NULL_NODE) {
			this.right = new Node(ch);
//			if (this.left == NULL_NODE) {
//				this.balance = Code.RIGHT;
//				//rotate
//			}
//			else {
//				this.balance = Code.SAME;
//			}	
		}
		else {
			this.right.addHelper(ch, container);
		}	
//		this.height = 1 + Math.max(this.left.heightHelper(),
//                this.right.heightHelper());
//		
//		int balance = getBalance(this);
//		this.checkBalance(balance, container);
		
	}
	
	public Node singleRightRotate(Node node) {
		Node newRootNode = node.left;
		Node rightChildOfLeft = newRootNode.right;
		newRootNode.right = node;
		node.left = rightChildOfLeft;
		newRootNode.rank = 1;
		newRootNode.heightHelper();
		node.heightHelper();
		return newRootNode;
		
	}
	
	public Node singleLeftRotate(Node node) {
		Node newRootNode = node.right;
		Node leftChildOfRight = newRootNode.left;
		newRootNode.left = node;
		node.right = leftChildOfRight;
		newRootNode.rank = 1;
		newRootNode.heightHelper();
		node.heightHelper();
		return newRootNode;
		//		Node child = parent.right;
//		parent.right = child.left;
//		child.left = parent;
//		parent.balance = Code.SAME;
//		child.balance = Code.SAME;
//		parent.rank = parent.left.size;
//		child.rank = child.left.size;
//		return child;
	}
//	right-left rotation
	public Node doubleRightRotate(Node node) {
		node.right = singleRightRotate(node.right);
		return singleLeftRotate(node);
	}
//	left-right rotation
	public Node doubleLeftRotate(Node node) {
		node.left = singleRightRotate(node.left);
		return singleRightRotate(node);
	}
	public int getBalance(Node node) {
		if (node == NULL_NODE) {
			return 0;
		}
		return node.left.height- node.right.height;
	}
	public void checkBalance(int balance, Container container) {
		if (balance > 1 && this.rank < this.left.rank)
			container.rotationCount ++;
            singleRightRotate(this);
 
        // Right Right Case
        if (balance < -1 && this.rank > this.right.rank)
        	container.rotationCount ++;
            singleLeftRotate(this);
 
        // Left Right Case
        if (balance > 1 && this.rank > this.left.rank) {
        	container.rotationCount = container.rotationCount +2;
            doubleLeftRotate(this);
        }
 
        // Right Left Case
        if (balance < -1 && this.rank < this.right.rank) {
        	container.rotationCount = container.rotationCount +2;
            doubleRightRotate(this);
        }
	}
	
	
}