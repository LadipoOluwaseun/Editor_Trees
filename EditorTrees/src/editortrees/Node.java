package editortrees;

import java.util.ArrayList;

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
	
	// Node parent;  // You may want this field.
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

	public Node addHelper(char ch, int pos) throws IndexOutOfBoundsException {
		if(this == NULL_NODE) {
			Node newNode = new Node(ch);
			return newNode;
		}
		if(pos < 0) {
			throw new IndexOutOfBoundsException();
		}
//		Case of pos > root node
//		Subtract pos - (root node pos + 1) add to this pos to the right
		else if (pos > this.rank) {
			this.right = this.right.addHelper(ch, (pos - rank - 1));
		}
		else if (pos <= this.rank ) {
//			System.out.println(rank);
			rank ++;
			this.left = this.left.addHelper(ch, pos);
		}
		
		
		return this;
		
//		Case of pos < root node
		
//		Comparing rank instead of value in node
	}

	public void addHelper(char ch) {
		if (this.right == NULL_NODE) {
			this.right = new Node(ch);
		}
		this.right.addHelper(ch);
		// TODO Auto-generated method stub.
		
	}
}