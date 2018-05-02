package editortrees;

import java.util.ArrayList;

import editortrees.EditTree.Container;

// A node in a height-balanced binary tree with rank.
// Except for the NULL_NODE (if you choose to use one), one node cannot
// belong to two different trees.

public class Node {
	public int height = 0;

	// Enumeration for the balance codes.
	enum Code {
		SAME, LEFT, RIGHT;

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

	// Enumeration signaling when to do rotations.
	enum RotationCode {

		LEFT, RIGHT, DOUBLE_RIGHT, DOUBLE_LEFT;
	}

	// Fields used in the Node Class.
	char element;
	Node left, right, parent;
	int rank, size;
	Code balance;

	public static final Node NULL_NODE = new Node();

	/**
	 * Creates a new node with the given character passed as a parameter.
	 * 
	 * @param data
	 * 
	 * @return none
	 */
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

	/**
	 * Initialization of the NULL_NODE a node that is used to take the place of
	 * any Node with null parameters.
	 *
	 * @param
	 * 
	 * @return
	 */
	public Node() {

		this.left = null;
		this.right = null;
		this.height = -1;
		this.size = 0;

	}

	/**
	 * Returns the height of a Node held in a field.
	 * 
	 * @param
	 * 
	 * @return int
	 */
	public int height() {

		return this.height;
	}

	/**
	 * Returns the size of a node that is calculated in this method.
	 * 
	 * @param
	 * 
	 * @return int
	 */
	public int size() {

		if (this == NULL_NODE) {
			return 0;
		}
		return this.right.size() + this.rank + 1;
	}

	/**
	 * Returns an ArrayList of Characters that is an in order traversal of a
	 * Binary Tree.
	 * 
	 * @param list
	 * 
	 * @return ArrayList<Character>
	 */
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

	/**
	 * Returns an ArrayList of Characters that is an in order traversal of a
	 * Binary Tree. Also returns the element rank and balance code of that node.
	 * 
	 * @param list
	 * 
	 * @return ArrayList<String>
	 */
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

	/**
	 * Sets the balance code fields of a node when called.
	 * 
	 * @param
	 * 
	 * @return
	 */
	public void setNodeBalance() {

		if (right.height - left.height == -1) {
			this.balance = Code.LEFT;
		} else if (left.height - right.height == 0) {
			this.balance = Code.SAME;
		} else if (right.height - left.height == 1) {
			this.balance = Code.RIGHT;
		}
	}

	/**
	 * Adds a node to the end of Binary Tree. Ex. ABCDEFG[H] (the position with
	 * Brackets).
	 * 
	 * @param ch,
	 *            container
	 * 
	 * @return Node
	 */
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
	 * Adds a node character node at a certain specified position in the tree.
	 * 
	 * @param ch,
	 *            position, container
	 * 
	 * @return Node
	 */
	public Node add(char ch, int position, Container container) {

		if (position == this.rank) {
			this.rank++;
			if (this.left == NULL_NODE) {
				this.left = new Node(ch);
			} else {
				this.left = this.left.add(ch, position, container);
			}
		} else if (position < this.rank) {
			if (this.left == NULL_NODE) {
				this.left = new Node(ch);
			}
			this.left = this.left.add(ch, position, container);
			this.rank++;
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

	/**
	 * Gets the character of a node in a specified position.
	 * 
	 * @param position
	 * 
	 * @return char
	 */
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

	/**
	 * Performs a single left rotation on a set of nodes.
	 * 
	 * @param node,
	 *            container
	 * 
	 * @return
	 */
	public Node rotateSingleLeft(Node node, Container container) {

		node = this.right;
		this.right = this.right.left;
		node.left = this;

		node.updateNodeFields();
		container.rotationCount++;
		return node;
	}

	/**
	 * Performs a single right rotation on a set of nodes.
	 * 
	 * @param node,
	 *            container
	 * 
	 * @return
	 */
	public Node rotateSingleRight(Node node, Container container) {

		node = this.left;
		this.left = this.left.right;
		node.right = this;

		node.updateNodeFields();
		container.rotationCount++;
		return node;
	}

	/**
	 * Performs a double right rotation on a set of nodes.
	 * 
	 * @param node,
	 *            container
	 * 
	 * @return Node
	 */
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

	/**
	 * Performs a double left rotation on a set of nodes.
	 * 
	 * @param node,
	 *            container
	 * 
	 * @return Node
	 */
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

	/**
	 * Calls the relevant rotation based upon what Enumeration is specified in
	 * this method.
	 * 
	 * @param rotateEnum,
	 *            container
	 * 
	 * @return Node
	 */
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

	/**
	 * Updates the height of a node.
	 * 
	 * @param
	 * 
	 * @return int
	 */
	public int updateHeight() {

		this.right.height = 1 + Math.max(this.right.right.height, this.right.left.height);
		this.left.height = 1 + Math.max(this.left.right.height, this.left.left.height);
		return 1 + Math.max(this.left.height, this.right.height);

	}

	/**
	 * Updated all the fields in the Node Class. (height, size, rank, and
	 * balance)
	 * 
	 * @param
	 * 
	 * @return
	 */
	public void updateNode() {

		this.height = Math.max(this.left.height, this.right.height) + 1;
		this.size = this.left.size + this.right.size + 1;
		this.rank = this.left.size;
		this.setNodeBalance();
	}

	/**
	 * Updates the size of a node.
	 * 
	 * @param
	 * 
	 * @return int
	 */
	public int updateSize() {

		this.left.size = this.left.left.size + this.left.right.size + 1;
		this.right.size = this.right.right.size + this.right.left.size + 1;
		return this.right.size + this.left.size + 1;
	}

	/**
	 * Updates the rank of a node.
	 * 
	 * @param
	 * 
	 * @return int
	 */
	public int updateRank() {

		this.left.rank = this.left.left.size;
		this.right.rank = this.right.left.size;
		return this.left.size;
	}

	/**
	 * Updated all the fields in the Node class.
	 * 
	 * @param
	 * 
	 * @return
	 */
	public void updateNodeFields() {

		this.height = this.updateHeight();
		this.size = this.updateSize();
		this.rank = this.updateRank();

		this.left.setNodeBalance();
		this.right.setNodeBalance();
		this.setNodeBalance();
	}

	/**
	 * Checks if the specified node needs a rotation performed upon it.
	 * 
	 * @param container
	 * 
	 * @return Node
	 */
	public Node checkForRotation(Container container) {

		// Single Right
		if ((right.height - left.height < -1)
				&& (this.left.balance.equals(Code.LEFT) || this.left.balance.equals(Code.SAME))) {
			return this.rotate(RotationCode.RIGHT, container);
		}
		// Single Left
		else if ((right.height - left.height > 1)
				&& (this.right.balance.equals(Code.RIGHT) || this.right.balance.equals(Code.SAME))) {
			return this.rotate(RotationCode.LEFT, container);
		}
		// Double Right
		else if ((right.height - left.height < -1) && this.left.balance.equals(Code.RIGHT)) {
			return this.rotate(RotationCode.DOUBLE_RIGHT, container);
		}
		// Double Left
		else if ((right.height - left.height > 1) && this.right.balance.equals(Code.LEFT)) {
			return this.rotate(RotationCode.DOUBLE_LEFT, container);
		}
		return this;
	}

	/**
	 * Returns the left most child of a Node.
	 * 
	 * @param
	 * 
	 * @return Node
	 */
	public Node nodeSucessor() {

		if (this.left == NULL_NODE) {
			return this;
		}
		return this.left.nodeSucessor();
	}

	/**
	 * Deletes the node at the specified position.
	 * 
	 * @param position,
	 *            container
	 * 
	 * @return Node
	 */
	public Node delete(int position, Container container) {

		if (position < 0 || position >= this.size) {
			throw new IndexOutOfBoundsException("Out of bounds");
		}
		// When we find the actual Position
		if (position == this.rank) {
			container.charRemoved = this.element;
			// 4 cases
			if (this.left != NULL_NODE && this.right != NULL_NODE) {
				Node toReturn = this.right.nodeSucessor();
				container.charRemoved = this.element;
				this.right = this.right.delete(0, container);
				this.element = toReturn.element;

			} else if (this.left == NULL_NODE && this.right == NULL_NODE) {
				return NULL_NODE;
			} else if (this.left != NULL_NODE) {
				return this.left;
			} else if (this.right != NULL_NODE) {
				return this.right;
			}
		}
		// Searching left
		else if (position < this.rank) {
			this.left = this.left.delete(position, container);
			this.rank--;
		}
		// Searching right
		else if (position > this.rank) {
			this.right = this.right.delete(position - (this.rank + 1), container);
		}
		this.updateNode();
		this.setNodeBalance();
		return this.checkForRotation(container);
	}

	/**
	 * Takes a tree and copies the entire tree.
	 * 
	 * @param oldTreeNode,
	 *            e, container
	 * 
	 * @return Node
	 */
	public Node createTreeCopy(Node oldTreeNode, EditTree e, Container container) {

		if (oldTreeNode == NULL_NODE) {
			return NULL_NODE;
		}
		container.size = e.container.size;
		this.balance = oldTreeNode.balance;
		this.element = oldTreeNode.element;
		this.height = oldTreeNode.height;
		this.rank = oldTreeNode.rank;
		this.size = oldTreeNode.size;
		// Creates news nodes with the information copied over
		this.left = new Node(oldTreeNode.left.element);
		this.right = new Node(oldTreeNode.right.element);
		// Recursive calls
		this.left = this.left.createTreeCopy(oldTreeNode.left, e, container);
		this.right = this.right.createTreeCopy(oldTreeNode.right, e, container);
		return this;
	}

	/**
	 * Takes two trees and merges them together making one version larger
	 * version of the two.
	 * 
	 * @param leftTree,
	 *            rightTree, connectingNode, checkParent, checkNode, pHeight
	 *            container
	 * 
	 * @return Node
	 */
	public Node concatenate(EditTree leftTree, EditTree rightTree, Node connectingNode, Node checkParent,
			Node checkNode, int checkNodeHeight, Container container) {
		// Determining the heights of both left and right trees
		int heightOfLeftTree = leftTree.height();
		int heightOfRightTree = rightTree.height();

		// Height of leftTree is greater than or equal height of rightTree
		if (heightOfLeftTree >= heightOfRightTree) {
			if ((checkNodeHeight - heightOfRightTree) > 1) {
				if (checkNode.balance.equals(Code.LEFT)) {
					checkNodeHeight = checkNodeHeight - 2;
				} else {
					checkNodeHeight = checkNodeHeight - 1;
				}
				checkParent = checkNode;
				checkNode.right = concatenate(leftTree, rightTree, connectingNode, checkParent, checkNode.right,
						checkNodeHeight, container);
				checkNode.updateNode();
				checkNode.setNodeBalance();
				return checkNode.checkForRotation(container);
			}
			connectingNode.left = checkNode;
			connectingNode.right = rightTree.getRoot();
			connectingNode.updateNode();
			connectingNode.setNodeBalance();
			if (checkParent != null) {
				checkParent.right = connectingNode;
			}
			return connectingNode.checkForRotation(container);
		}
		// Height of leftTree is less than height of rightTree
		else if (heightOfLeftTree < heightOfRightTree) {
			if (checkNodeHeight - heightOfRightTree > 1) {
				if (checkNode.balance.equals(Code.RIGHT)) {
					checkNodeHeight = checkNodeHeight - 2;
				} else {
					checkNodeHeight = checkNodeHeight - 1;
				}
				checkParent = checkNode;
				checkNode.left = concatenate(leftTree, rightTree, connectingNode, checkParent, checkNode.left,
						checkNodeHeight, container);
				checkNode.updateNode();
				checkNode.setNodeBalance();
				return checkNode.checkForRotation(container);
			}
			connectingNode.right = checkNode;
			connectingNode.left = leftTree.getRoot();
			connectingNode.updateNode();
			connectingNode.setNodeBalance();
			if (parent != null) {
				parent.left = connectingNode;
			}
			return connectingNode.checkForRotation(container);
		}
		return checkNode;
	}

	/**
	 * Constructs a new tree based upon a string given method.
	 * 
	 * @param string
	 * 
	 * @return Node
	 */
	public Node EditTree(String string) {
		if (string.length() == 0) {
			return NULL_NODE;
		}
		int rootCharIndex = (int) Math.floor((double) (string.length() / 2));
		this.element = string.charAt(rootCharIndex);
		this.left = new Node();
		this.left = this.left.EditTree(string.substring(0, rootCharIndex));
		this.right = new Node();
		this.right = this.right.EditTree(string.substring(rootCharIndex + 1, string.length()));
		this.updateNode();

		return this;
	}
}
