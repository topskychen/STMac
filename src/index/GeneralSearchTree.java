/**
 * 
 */
package index;

import index.BinarySearchTree.RangeQueryStrategy;

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

import utility.Constants;
import crypto.Gfunction;
import crypto.PMAC;
import memoryindex.BinaryTree;
import memoryindex.IQueryStrategy;
import multithread.MultiThread;
import multithread.Task;

/**
 * @author chenqian
 *
 */
public class GeneralSearchTree extends BinaryTree implements SearchIndex {
	
	public DataOutputStream ds = null;

	public GeneralSearchTree(Class classValue) {
		super(classValue);
		// TODO Auto-generated constructor stub
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public ArrayList<Data> rangeQuery(Query query) {
		// TODO Auto-generated method stub
		RangeQueryStrategy rangeQueryStrategy = 
				new RangeQueryStrategy(query.getlBound(), 
						query.getrBound(), 
						query.getRange());
		queryStrategy(rangeQueryStrategy);
		return rangeQueryStrategy.getResults();
	}

	@Override
	public void buildIndex(Trajectory tra, PMAC pmac) {
		// TODO Auto-generated method stub
		if (tra.checkTrajectory() == false) 
			throw new IllegalStateException("The pmac for trajectory is not complete.");
		BinaryTree[] nodes = new BinaryTree[tra.length()]; 
		int size = buildLeafNodes(tra, nodes, pmac);
		buildTree(nodes, size, pmac);
		flush();
	}
	
	public void buildTree(BinaryTree[] nodes, int size, PMAC pmac) {
		OptimizedTree structure = new OptimizedTree(); 
		OptimizedNode[] onodes = new OptimizedNode[size];
		for (int i = 0; i < size; i ++) {
			onodes[i] = new OptimizedNode(((GData)nodes[i].getValue()).getPrex(), nodes[i]);
		}
		structure.buildTree(onodes, size);
//		System.out.println(structure.toString());
		if (structure.root.isLeaf()) {
			BinaryTree root = (BinaryTree) structure.root.getValue();
			this.setValue(root.getValue());
		} else {
			buildTree(structure.root, this, pmac);
		}
	}
	
	public void buildTree(OptimizedNode onode, BinaryTree node, PMAC pmac) {
		if (onode.getLeftChild().isLeaf()) {
			node.setLeftChild((BinaryTree)onode.getLeftChild().getValue());
		} else {
			node.setLeftChild(new BinaryTree(GData.class));
			buildTree(onode.getLeftChild(), node.getLeftChild(), pmac);
		}
		if (onode.getRightChild().isLeaf()) {
			node.setRightChild((BinaryTree)onode.getRightChild().getValue());
		} else {
			node.setRightChild(new BinaryTree(GData.class));
			buildTree(onode.getRightChild(), node.getRightChild(), pmac);
		}
		node.setValue(new GData(new Data[]{(Data) node.getLeftChild().getValue(), (Data) node.getRightChild().getValue()}, 2, pmac));
		node.setClassValue(GData.class);
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
	
	public int buildLeafNodes(Trajectory tra, BinaryTree[] nodes, PMAC pmac) {
		int size = 0;
		Gfunction[] gfs = new Gfunction[tra.length() + 2];
		gfs[0] = new Gfunction(-1, 2);
		gfs[tra.length() + 1] = new Gfunction(1, 2);
		MyTask[] tasks = new MyTask[tra.length()];
		for (int i = 1; i <= tra.length(); i ++) {
			tasks[i - 1] = new MyTask(tra.getTimeStamp(i), 2);
//			gfs[i] = new Gfunction(tra.getTimeStamp(i), 2);
		}
		MultiThread multiThread = new MultiThread(tasks, Constants.ThreadNum); multiThread.run();
		for (int i = 1; i <= tra.length(); i ++) {
//			tasks[i - 1] = new MyTask(tra.getTimeStamp(i), 2);
			gfs[i] = tasks[i - 1].getGF();
		}
		MyTask2[] tasks2 = new MyTask2[tra.length()];
		for (int i = 1; i <= tra.length(); i ++) {
			GData gdata = new GData(tra.getLocation(i), 
					tra.getTimeStamp(i - 1), 
					tra.getTimeStamp(i), 
					tra.getTimeStamp(i + 1), 
					gfs[i - 1].getDigest(),
					gfs[i],
					gfs[i + 1].getDigest(),
					pmac,
					true
					);
			tasks2[i - 1] = new MyTask2(gdata, pmac);
		}
		MultiThread multiThread2 = new MultiThread(tasks2, Constants.ThreadNum); multiThread2.run();
		for (int i = 1; i <= tra.length(); i ++) {			
			BinaryTree<Integer, GData> node =
					new BinaryTree(tra.getTimeStamp(i), tasks2[i - 1].getData(), GData.class);
			nodes[size ++] = node;
		}
		return size;
	}

	private void loadGfunctions(BinaryTree node) {
		if (node.isLeaf()) return;
		loadGfunctions(node.getLeftChild());
		loadGfunctions(node.getRightChild());
		((GData)node.getValue()).gf2 = ((GData)node.getLeftChild().getValue()).gf2;
		((GData)node.getValue()).gf3 = ((GData)node.getRightChild().getValue()).gf3;
	}
	
	@Override
	public void leadTree(Object[] args) {
		// TODO Auto-generated method stub
		DataInputStream ds;
		try {
			ds = new DataInputStream(new BufferedInputStream(new FileInputStream(new File((String)args[0] + ".gen.dat"))));
			this.classValue = (Class) args[1];
			this.read(ds);
			loadGfunctions(this);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void createTree(Object[] args) {
		// TODO Auto-generated method stub
		try {
			this.ds = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(new File((String)args[0] + ".gen.dat"))));
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
				GData data = (GData) n.getLeftChild().getValue();
				if (visitData(data)) {
					toVisit.add(n.getLeftChild());
				}
			}
			if (!n.isRightChildEmpty()) {
				GData data = (GData) n.getRightChild().getValue();
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
		
		boolean visitData(GData data) {
			if (data.t2 >= lBound && data.t3 <= rBound) {
				if (!data.prex.startsWith(prex)) System.out.println("wanring: the data prefix: " + data.prex);
			}
			if (data.getT2() > rBound && data.getT3() < lBound) {
				return false;
			} else {
				if (data.getPrex().startsWith(prex)) {
					results.add(data);
					return false;
				} else return true;
			} 
		}
	}
	
	class MyTask2 extends Task {
		GData data = null;
		PMAC pmac = null;
		
		public MyTask2 (GData data, PMAC pmac) {
			this.data = data;
			this.pmac = pmac;
		}
		
		public GData getData() {
			return data;
		}

		@Override
		public void run() {
			// TODO Auto-generated method stub
			this.data.buildData(pmac);
		}
	}
	
	class MyTask extends Task {
		
		Gfunction gf = null;
		int t, base;
		
		public MyTask (int t, int base) {
			this.t = t;
			this.base = base;
		}
		
		public Gfunction getGF() {
			return gf;
		}
		
		@Override
		public void run() {
			// TODO Auto-generated method stub
			gf = new Gfunction(t, base);
		}
		
	}

}
