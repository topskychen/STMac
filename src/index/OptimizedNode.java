package index;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.PriorityQueue;

import IO.DataIO;

public class OptimizedNode {
	String prex			= null;
	Object value		= null;
	OptimizedNode leftChild		= null;
	OptimizedNode rightChild		= null;
	OptimizedNode leftParent		= null;
	OptimizedNode rightParent	= null;
	boolean valid		= true;
	int size = 1;

	public boolean isValid() {
		return valid;
	}

	public void setValid(boolean valid) {
		this.valid = valid;
	}

	public OptimizedNode(String prex, Object value) {
		this.prex = prex;
		this.value = value;
	}
	
	public OptimizedNode(OptimizedNode left, OptimizedNode right) {
		leftChild = left;
		rightChild = right;
		leftChild.setRightParent(this);
		rightChild.setLeftParent(this);
		prex = DataIO.commonPrefix(leftChild.getPrex(), rightChild.getPrex());
	}
	
	public void updateSize() {
		size = leftChild.getSize() + rightChild.getSize();
	}
	
	public int getSize() {
		return size;
	}

	public void setSize(int size) {
		this.size = size;
	}

	public void merge(PriorityQueue<OptimizedNode> heap) {
		OptimizedNode n1 = leftChild.getLeftParent();
		if (n1 != null) {
			n1.setValid(false);
			OptimizedNode left = n1.getLeftChild();
			if (left != null) {
				OptimizedNode parent = new OptimizedNode(left, this);
				parent.updateSize();
				left.setRightParent(parent);
				heap.add(parent);
			}
		}
		OptimizedNode n2 = rightChild.getRightParent();
		if (n2 != null) {
			n2.setValid(false);
			OptimizedNode right = n2.getRightChild();
			if (right != null) {
				OptimizedNode parent = new OptimizedNode(this, right);
				parent.updateSize();
				right.setLeftParent(parent);
				heap.add(parent);
			}
		}
	}
	
	public OptimizedNode getLeftParent() {
		return leftParent;
	}

	public void setLeftParent(OptimizedNode leftParent) {
		this.leftParent = leftParent;
	}

	public OptimizedNode getRightParent() {
		return rightParent;
	}

	public void setRightParent(OptimizedNode rightParent) {
		this.rightParent = rightParent;
	}

	public OptimizedNode getLeftChild() {
		return leftChild;
	}

	public void setLeftChild(OptimizedNode leftChild) {
		this.leftChild = leftChild;
	}

	public OptimizedNode getRightChild() {
		return rightChild;
	}

	public void setRightChild(OptimizedNode rightChild) {
		this.rightChild = rightChild;
	}

	
	public String getPrex() {
		return prex;
	}
	
	public void setPrex(String prex) {
		this.prex = prex;
	}
	
	public Object getValue() {
		return value;
	}
	
	public void setValue(Object value) {
		this.value = value;
	}
	
	public String toString(int level) {
		StringBuffer sb  = new StringBuffer();
		sb.append(DataIO.getIndent(level));
		sb.append(prex + ", " + value + "\n");
		if (leftChild != null) sb.append(leftChild.toString(level + 1));
		else {
			sb.append(DataIO.getIndent(level + 1));
			sb.append("null\n");
		}
		if (rightChild != null)sb.append(rightChild.toString(level + 1));
		else {
			sb.append(DataIO.getIndent(level + 1));
			sb.append("null\n");
		}
		return sb.toString();
	}
	
	public boolean isLeaf() {
		return getLeftChild() == null && getRightChild() == null;
	}

	public void getEncoding(ArrayList<String> encodings, StringBuffer sb) {
		// TODO Auto-generated method stub
		if (isLeaf()) {
			encodings.add(sb.toString());
			return;
		}
		leftChild.getEncoding(encodings, sb.append("0")); sb.deleteCharAt(sb.length() - 1);
		rightChild.getEncoding(encodings, sb.append("1")); sb.deleteCharAt(sb.length() - 1);
	}

}

