/**
 * 
 */
package index;

import java.util.ArrayList;
import java.io.*;
import java.util.*;

import crypto.PMAC;
import bptree.BPlusTree;
import bptree.IQueryStrategy;
import bptree.InnerNode;
import bptree.LeafNode;
import bptree.Node;

/**
 * @author chenqian
 * @param <K>
 *
 */
public class ThreadSearchTree extends BPlusTree implements SearchIndex{

//	BPlusTree<Long, Data> bptree = null;
	
	public ThreadSearchTree(){}
	
	public ThreadSearchTree(BPlusTree<Long, Data> bptree) {
		super(bptree);
	}

	public void leadTree(Object[] args) {
		copy(BPlusTree.loadBPTree(new Object[]{args[0], args[1], args[2]}));
	}
	
	public void createTree(Object[] args) {
		try {
			copy(BPlusTree.createBPTree(new Object[] {args[0] , args[1], args[2], args[3], args[4]}));
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NullPointerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	
	/**
	 * Build the data for btree.
	 * @param tra
	 * @param pmac
	 */
	public void buildIndex(Trajectory tra, PMAC pmac) {
		if (tra.checkTrajectory() == false) 
			throw new IllegalStateException("The pmac for trajectory is not complete.");
		for (int i = 1; i <= tra.length(); i ++) {
			put(new Long(tra.getTimeStamp(i)), tra.getLeafData(i, pmac));
		}
		buildIndexData(getRoot(), pmac);
		flush();
	}
	
	/**
	 * Build the data for inner node via dfs. 
	 * @param n
	 * @param pmac
	 */
	public void buildIndexData(Node<Long, Data> n, PMAC pmac) {
		
		if (n.isInnerNode()) {
			for (int i = 0; i < n.getSlots() + 1; i ++) {
				Node<Long, Data> cur = readNode(((InnerNode<Long, Data>)n).getChildId(i));
				if (!cur.isLeafNode()) {
					buildIndexData(cur, pmac);
					n.setValue(new BData(cur.getValues(), cur.getSlots() + 1, pmac), i);
				} else 
					n.setValue(new BData(cur.getValues(), cur.getSlots(), pmac), i);
			}
			writeNode(n);
		} else {
			throw new IllegalStateException("The leaf node is not possilble to be visited.");
		}
	}
	
	public ArrayList<Data> rangeQuery(Query query) {
		RangeQueryStrategy rangeQueryStrategy = new RangeQueryStrategy(query.getlBound(), query.getrBound());
		queryStrategy(rangeQueryStrategy);
		return rangeQueryStrategy.getResults();
	} 
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return toString();
	}
	
	class RangeQueryStrategy implements IQueryStrategy {

		private ArrayList<Integer> toVisit = new ArrayList<Integer>();
		private ArrayList<Data> results = new ArrayList<Data>();
		private int lBound, rBound;

		
		
		public RangeQueryStrategy(int lBound, int rBound) {
			super();
			this.lBound = lBound;
			this.rBound = rBound;
		}

		public ArrayList<Data> getResults() {
			return results;
		}
		
		@Override
		public void getNextEntry(Node n, int[] next, boolean[] hasNext) {
			// TODO Auto-generated method stub
			if (n instanceof LeafNode){				
				for (int i = 0; i < n.getSlots(); i ++) {
					if (visitInnerData((BData) n.getValue(i))) {
						// do nothing
					}
				}
			} else {
				for (int i = 0; i < n.getSlots() + 1; i ++) {
					if (visitInnerData((BData) n.getValue(i))) {
						toVisit.add(((InnerNode) n).getChildId(i));
					}
				}
			}
			if (!toVisit.isEmpty()) {
				next[0] = toVisit.remove(0);
				hasNext[0] = true;
			} else {
				hasNext[0] = false;
			}
		}
		
		
		/**
		 * 
		 * @param data
		 * @return
		 */
		public boolean visitInnerData(BData data) {
			if (data.t2 >= lBound && data.t3 <= rBound) {
				results.add(data);
				return false;
			} else if (data.t2 > rBound || data.t3 < lBound) {
				return false;
			} 
			return true;
		}
		
	}

}
