package editortrees;

import java.util.ArrayList;

import editortrees.EditTree.Container;

// A node in a height-balanced binary tree with rank.
// Except for the NULL_NODE (if you choose to use one), one node cannot
// belong to two different trees.

public class Node {
	public int height = 0;// Field to track the height in the tree

	// Enumeration for the balance codes of each node
	enum Code {
		SAME, LEFT, RIGHT;
		// Used in the displayer and debug string
		@Override
		public String toString() {
			switch (this) {
			case LEFT:
				return "/";
			case SAME:
				return "=";
			case RIGHT:
				return "\\";
			default:
				throw new IllegalStateException("Not a valid balance code");
			}
		}
	}

	enum RotationCode {
		LEFT, RIGHT, DOUBLE_RIGHT, DOUBLE_LEFT;
	}

	// Fields
	char element;
	Node left, right, parent;
	int rank, size;
	Code balance;
	public static final Node NULL_NODE = new Node();

	public Node(Character data) {
		// Basic Node data
		this.element = data;
		this.left = NULL_NODE;
		this.right = NULL_NODE;

		// Advanced Node Data
		this.rank = 0;
		this.balance = Code.SAME;
		this.size = 1;
		this.height = 0;
	}

	public Node() {
		
		this.left = null;
		this.right = null;
		this.height = -1;
		this.size = 0;

	}

	public int height() {
		
		return this.height;
	}

	public int size() {
		
		if (this == NULL_NODE) {
			return 0;
		}
		return this.right.size() + this.rank + 1;
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

	public void setNodeBalance() {
		
		if (right.height - left.height == -1) {
			this.balance = Code.LEFT;
		} else if (left.height - right.height == 0) {
			this.balance = Code.SAME;
		} else if (right.height - left.height == 1) {
			this.balance = Code.RIGHT;
		}
	}
	
	public Node add(char ch, Container container) {
		
		if (this == NULL_NODE) {
			return new Node(ch);
		}
		this.right = this.right.add(ch, container);
		this.height = Math.max(this.left.height, this.right.height) + 1;
		this.size++;
		
		this.setNodeBalance();
		return this.checkForRotation(container);
	}

	/**
	 * Adds a node character node at a certain position in the tree
	 * 
	 *
	 * @return Node
	 */
	public Node add(char ch, int position, Container container) {
		
		if (position < this.rank) {
			if (this.left == NULL_NODE) {
				this.left = new Node(ch);
			}
			this.left = this.left.add(ch, position, container);
			this.rank++;
		} else if (position == this.rank) {
			this.rank++;
			if (this.left == NULL_NODE) {
				this.left = new Node(ch);
			} else {
				this.left = this.left.add(ch, position, container);
			}
		} else {
			if (this.right == NULL_NODE) {
				if (position - this.rank > 1) {
					throw new IndexOutOfBoundsException("Out of bounds");
				}
				this.right = new Node(ch);
			} else {
				this.right = this.right.add(ch, position - (this.rank + 1), container);
			}
		}
		
		this.updateNode();
		this.setNodeBalance();
		return this.checkForRotation(container);
	}

	public char get(int position) throws IndexOutOfBoundsException {

		if (position < this.rank) {
			if (this.left == NULL_NODE) {
				throw new IndexOutOfBoundsException();
			}
			return this.left.get(position);
		} else if (position > this.rank) {
			if (this.right == NULL_NODE) {
				throw new IndexOutOfBoundsException();
			}
			return this.right.get(position - (this.rank + 1));
		} else if (this.left == NULL_NODE && this.right == NULL_NODE && position != this.rank) {
			throw new IndexOutOfBoundsException("Not in tree");
		}
		return this.element;
	}

	public Node rotateSingleLeft(Node node, Container container) {

		node = this.right;
		this.right = this.right.left;
		node.left = this;
		node.updateNodeFields();
		container.rotationCount++;
		return node;
	}

	public Node rotateSingleRight(Node node, Container container) {

		node = this.left;
		this.left = this.left.right;
		node.right = this;
		node.updateNodeFields();
		container.rotationCount++;
		return node;
	}

	public Node rotateDoubleRight(Node node, Container container) {

		node = this.left.right;
		Node temp1 = this.left.right.left;
		node.left = this.left;
		Node temp2 = this.left.right.right;
		this.left.right = temp1;
		node.left = this.left;
		this.left = temp2;
		node.right = this;
		node.updateNodeFields();
		container.rotationCount += 2;
		return node;
	}

	public Node rotateDoubleLeft(Node node, Container container) {

		node = this.right.left;
		Node temp1 = this.right.left.right;
		node.right = this.right;
		Node temp2 = this.right.left.left;
		this.right.left = temp1;
		node.right = this.right;
		this.right = temp2;
		node.left = this;
		node.updateNodeFields();
		container.rotationCount += 2;
		return node;
	}

	public Node rotate(RotationCode rotateEnum, Container container) {

		Node tempNode = this;

		if (rotateEnum.equals(RotationCode.DOUBLE_RIGHT)) {
			return this.rotateDoubleRight(tempNode, container);
		} else if (rotateEnum.equals(RotationCode.DOUBLE_LEFT)) {
			return this.rotateDoubleLeft(tempNode, container);
		} else if (rotateEnum.equals(RotationCode.LEFT)) {
			return this.rotateSingleLeft(tempNode, container);
		} else if (rotateEnum.equals(RotationCode.RIGHT)) {
			return this.rotateSingleRight(tempNode, container);
		}
		return tempNode;
	}

	public void updateNode() {

		this.height = Math.max(this.left.height, this.right.height) + 1;
		this.size = this.left.size + this.right.size + 1;
		this.rank = this.left.size;
		this.setNodeBalance();
	}

	public int updateHeight() {

		this.right.height = 1 + Math.max(this.right.right.height, this.right.left.height);
		this.left.height = 1 + Math.max(this.left.right.height, this.left.left.height);
		return 1 + Math.max(this.left.height, this.right.height);

	}

	public int updateSize() {

		this.left.size = this.left.left.size + this.left.right.size + 1;
		this.right.size = this.right.right.size + this.right.left.size + 1;
		return this.right.size + this.left.size + 1;
	}

	public int updateRank() {

		this.left.rank = this.left.left.size;
		this.right.rank = this.right.left.size;
		return this.left.size;
	}

	public void updateNodeFields() {

		this.height = this.updateHeight();
		this.size = this.updateSize();
		this.rank = this.updateRank();

		this.left.setNodeBalance();
		this.right.setNodeBalance();
		this.setNodeBalance();
	}

	public Node checkForRotation(Container c) {

		// Single Right
		if ((right.height - left.height < -1)
				&& (this.left.balance.equals(Code.LEFT) || this.left.balance.equals(Code.SAME))) {
			return this.rotate(RotationCode.RIGHT, c);
		}
		// Single Left
		else if ((right.height - left.height > 1)
				&& (this.right.balance.equals(Code.RIGHT) || this.right.balance.equals(Code.SAME))) {
			return this.rotate(RotationCode.LEFT, c);
		}
		// Double Right
		else if ((right.height - left.height < -1) && this.left.balance.equals(Code.RIGHT)) {
			return this.rotate(RotationCode.DOUBLE_RIGHT, c);
		}
		// Double Left
		else if ((right.height - left.height > 1) && this.right.balance.equals(Code.LEFT)) {
			return this.rotate(RotationCode.DOUBLE_LEFT, c);
		}
		return this;
	}

	public Node delete(int position, Container container) {
//		When we find the actual Position
		if (position == this.rank) {
			container.charRemoved = this.element;
//		4 cases 
			if (this.left != NULL_NODE && this.right != NULL_NODE) {
				return NULL_NODE;
			}
			else if (this.left == NULL_NODE && this.right == NULL_NODE) {
				
			}
			else if (this.left != NULL_NODE) {
				return this.left;
			}
			else if (this.right != NULL_NODE) {
				return this.right;
			}
		}
//		Searching left
		else if (position < this.rank) {
			
			this.left = this.left.delete(position, container);
			this.rank--;
		} 
//		Searching right
		else {
			
			this.right = this.right.delete(position - (this.rank + 1), container);
		}
		
		this.updateNode();
		this.setNodeBalance();
		return this.checkForRotation(container);
	}

}
