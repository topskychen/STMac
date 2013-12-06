/**
 * 
 */
package index;

import index.ThreadSearchTree.RangeQueryStrategy;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import memoryindex.BinaryTree;
import memoryindex.IQueryStrategy;
import crypto.PMAC;

/**
 * @author chenqian
 *
 */
public class BinarySearchTree extends BinaryTree implements SearchIndex {

	public DataOutputStream ds = null;

	public BinarySearchTree(Class classValue) {
		super(classValue);
		// TODO Auto-generated constructor stub
	}

	/**
	 * 
	 */

	/* (non-Javadoc)
	 * @see index.SearchIndex#toString()
	 */
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see index.SearchIndex#rangeQuery(index.Query)
	 */
	@Override
	public ArrayList<Data> rangeQuery(Query query) {
		// TODO Auto-generated method stub
		RangeQueryStrategy rangeQueryStrategy = new RangeQueryStrategy(query.getlBound(), query.getrBound(), query.getRange());
		queryStrategy(rangeQueryStrategy);
		return rangeQueryStrategy.getResults();
	}

	/* (non-Javadoc)
	 * @see index.SearchIndex#buildIndex(index.Trajectory, crypto.PMAC)
	 */
	@Override
	public void buildIndex(Trajectory tra, PMAC pmac) {
		// TODO Auto-generated method stub
		if (tra.checkTrajectory() == false) 
			throw new IllegalStateException("The pmac for trajectory is not complete.");
		BinaryTree[] nodes = new BinaryTree[tra.length()]; int size = 0;
		for (int i = 1; i <= tra.length(); i ++) {
			BinaryTree<Integer, BData> node = 
					new BinaryTree(tra.getTimeStamp(i), new BData(tra.getLocation(i), 
									tra.getTimeStamp(i - 1), 
									tra.getTimeStamp(i), 
									tra.getTimeStamp(i + 1), 
									tra.getSigma(i), 
									tra.getR(i), pmac),
									getClassValue());
			nodes[size ++] = node;
//			System.out.println(node.getValue().timeStampsToString());
		}
		buildTree(nodes, size, pmac);
//		print();
//		checkTree(this, pmac);
		flush();
	}
	
	public void flush() {
		try {
			write(ds);
			ds.flush();
			ds.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
	}
	public void buildTree(BinaryTree[] nodes, int size, PMAC pmac) {
		while (size > 1) {
			int newSize = 0;
			for (int i = 0; i < size; i += 2) {
				if (i + 1 >= size) {
					nodes[newSize ++] = nodes[i];
				} else {
					BinaryTree leftNode = nodes[i];
					BinaryTree rightNode = nodes[i + 1];
					nodes[newSize ++] = new BinaryTree(null,
							new BData(new Data[]{(Data) leftNode.getValue(), (Data) rightNode.getValue()}, 2, pmac),
							leftNode,
							rightNode,
							getClassValue()
							);
				}
			}
			size = newSize;
		}
		this.value = nodes[0].getValue();
		this.setLeftChild(nodes[0].getLeftChild());
		this.setRightChild(nodes[0].getRightChild());
	}

	public void checkTree(BinaryTree tree, PMAC pmac) {
		if (((BData) tree.getValue()).testData(pmac, ((BData) tree.getValue()).getPrex())) System.out.println("pass.");
		else System.out.println("fail.");
		if (!tree.isLeftChildEmpty()) checkTree(tree.getLeftChild(), pmac);
		if (!tree.isRightChildEmpty()) checkTree(tree.getRightChild(), pmac);
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

	@Override
	public void leadTree(Object[] args) {
		// TODO Auto-generated method stub
		DataInputStream ds;
		try {
			ds = new DataInputStream(new BufferedInputStream(new FileInputStream(new File((String)args[0] + ".bin.dat"))));
			this.classValue = (Class) args[1];
			this.read(ds);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void createTree(Object[] args) {
		// TODO Auto-generated method stub
		try {
			this.ds = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(new File((String)args[0] + ".bin.dat"))));
			this.classValue = (Class) args[3];
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	class RangeQueryStrategy implements IQueryStrategy {

		private ArrayList<BinaryTree> toVisit = new ArrayList<BinaryTree>();
		private ArrayList<Data> results = new ArrayList<Data>();
		private int lBound, rBound;
		private String prex;
		
		
		public RangeQueryStrategy(int lBound, int rBound, String prex) {
			super();
			this.lBound = lBound;
			this.rBound = rBound;
			this.prex = prex;
		}

		public ArrayList<Data> getResults() {
			return results;
		}

		@Override
		public void getNextEntry(BinaryTree n, BinaryTree[] next,
				boolean[] hasNext) {
			// TODO Auto-generated method stub
			if (!n.isLeftChildEmpty()) {
				BData data = (BData) n.getLeftChild().getValue();
				if (visitData(data)) {
					toVisit.add(n.getLeftChild());
				}
			}
			if (!n.isRightChildEmpty()) {
				BData data = (BData) n.getRightChild().getValue();
				if (visitData(data)) {
					toVisit.add(n.getRightChild());
				}
			}
			if (!toVisit.isEmpty()) {
				next[0] = toVisit.remove(0);
				hasNext[0] = true;
			} else {
				hasNext[0] = false;
			}
		}
		
		boolean visitData(BData data) {
			if (data.t2 >= lBound && data.t3 <= rBound) {
				if (!data.prex.startsWith(prex)) System.out.println("wanring: the data prefix: " + data.prex);
				results.add(data);
				return false;
			} else if (data.t2 > rBound || data.t3 < lBound) {
				return false;
			} 
			return true;
		}
		
	}
}
