/**
 * 
 */
package index;
import io.IO;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.PriorityQueue;

/**
 * @author chenqian
 *
 */
public class OptimizedTree {

	PriorityQueue<OptimizedNode> heap = new PriorityQueue<OptimizedNode>(10, new NodeComparator());
	OptimizedNode root;
	
	private void initHeap(OptimizedNode[] leafOptimizedNodes, int size) {
		if (size <= 1) return;
		for (int i = 1; i < size; i ++) {
			OptimizedNode left = leafOptimizedNodes[i - 1], cur = leafOptimizedNodes[i];
			heap.add(new OptimizedNode(left, cur));
		}
	}
	
	public void buildTree(OptimizedNode[] leafOptimizedNodes, int size) {
		initHeap(leafOptimizedNodes, size);
		while(!heap.isEmpty()) {
			OptimizedNode top = heap.remove();
			if (top.isValid()) {
				root = top;
				top.merge(heap);
			}
		}
	}
	
	public String toString() {
		return root.toString(0);
	}
	
	public void getEncodings(ArrayList<String> encodings) {
		StringBuffer sb = new StringBuffer("0");
		root.getEncoding(encodings, sb);
	}
	
	/**
	 * 
	 */
	public OptimizedTree() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		OptimizedTree optimizedTree = new OptimizedTree();
		OptimizedNode[] OptimizedNodes = new OptimizedNode[]{
				optimizedTree.newOptimizedNode("01001011100011100110001000010110", "1"),
				optimizedTree.newOptimizedNode("01001011100011100110001000010110", "2"),
				optimizedTree.newOptimizedNode("01001011100011100110001000010101", "3"),
				optimizedTree.newOptimizedNode("01001011100011100110001000011110", "4"),
				optimizedTree.newOptimizedNode("01001011100011100110001000010001", "5"),
				optimizedTree.newOptimizedNode("01001011100011100110001000010110", "6"),
				optimizedTree.newOptimizedNode("01001011100011100110001000010101", "7"),
				optimizedTree.newOptimizedNode("01001011100011100110001000011110", "8"),
				optimizedTree.newOptimizedNode("01001011100011100110001000010001", "9"),
				optimizedTree.newOptimizedNode("01001011100011100110001000010110", "10"),
				optimizedTree.newOptimizedNode("01001011100011100110001000010110", "11"),
				optimizedTree.newOptimizedNode("01001011100011100110001000010110", "12"),
				optimizedTree.newOptimizedNode("01001011100011100110001000010101", "13"),
				optimizedTree.newOptimizedNode("01001011100011100110001000011110", "14"),
				optimizedTree.newOptimizedNode("01001011100011100110001000010001", "15"),
				optimizedTree.newOptimizedNode("01001011100011100110001000010110", "16"),
				optimizedTree.newOptimizedNode("01001011100011100110001000010101", "17"),
				optimizedTree.newOptimizedNode("01001011100011100110001000011110", "18"),
				optimizedTree.newOptimizedNode("01001011100011100110001000010001", "19"),
				optimizedTree.newOptimizedNode("01001011100011100110001000010110", "20"),
				optimizedTree.newOptimizedNode("01001011100011100110001000010110", "21"),
				optimizedTree.newOptimizedNode("01001011100011100110001000010110", "22"),
				optimizedTree.newOptimizedNode("01001011100011100110001000010101", "23"),
				optimizedTree.newOptimizedNode("01001011100011100110001000011110", "24"),
				optimizedTree.newOptimizedNode("01001011100011100110001000010001", "25"),
				optimizedTree.newOptimizedNode("01001011100011100110001000010110", "26"),
				optimizedTree.newOptimizedNode("01001011100011100110001000010101", "27"),
				optimizedTree.newOptimizedNode("01001011100011100110001000011110", "28"),
				optimizedTree.newOptimizedNode("01001011100011100110001000010001", "29"),
				optimizedTree.newOptimizedNode("01001011100011100110001000010110", "30"),
				optimizedTree.newOptimizedNode("01001011100011100110001000010110", "31"),
				optimizedTree.newOptimizedNode("01001011100011100110001000010110", "32"),
				optimizedTree.newOptimizedNode("01001011100011100110001000010101", "33"),
				optimizedTree.newOptimizedNode("01001011100011100110001000011110", "34"),
				optimizedTree.newOptimizedNode("01001011100011100110001000010001", "35"),
				optimizedTree.newOptimizedNode("01001011100011100110001000010110", "36"),
				optimizedTree.newOptimizedNode("01001011100011100110001000010101", "37"),
				optimizedTree.newOptimizedNode("01001011100011100110001000011110", "38"),
				optimizedTree.newOptimizedNode("01001011100011100110001000010001", "39"),
				optimizedTree.newOptimizedNode("01001011100011100110001000010110", "40"),
				optimizedTree.newOptimizedNode("01001011100011100110001000010110", "41"),
				optimizedTree.newOptimizedNode("01001011100011100110001000010110", "42"),
				optimizedTree.newOptimizedNode("01001011100011100110001000010101", "43"),
				optimizedTree.newOptimizedNode("01001011100011100110001000011110", "44"),
				optimizedTree.newOptimizedNode("01001011100011100110001000010001", "45"),
				optimizedTree.newOptimizedNode("01001011100011100110001000010110", "46"),
				optimizedTree.newOptimizedNode("01001011100011100110001000010101", "47"),
				optimizedTree.newOptimizedNode("01001011100011100110001000011110", "48"),
				optimizedTree.newOptimizedNode("01001011100011100110001000010001", "49"),
				optimizedTree.newOptimizedNode("01001011100011100110001000010110", "50")
		}; 
		optimizedTree.buildTree(OptimizedNodes, OptimizedNodes.length);
		System.out.println(optimizedTree.toString());
	}
	
	public OptimizedNode newOptimizedNode(String prex, Object o) {
		return new OptimizedNode(prex,o);
	}
	
	class NodeComparator implements Comparator<OptimizedNode> {

		@Override
		public int compare(OptimizedNode o1, OptimizedNode o2) {
			// TODO Auto-generated method stub
			if (o1.getPrex().length() > o2.getPrex().length()) return -1;
			else if (o1.getPrex().length() < o2.getPrex().length()) return 1;
			else {
				if (o1.getSize() < o2.getSize()) return -1;
				else if (o1.getSize() > o2.getSize()) return 1;
				else return 0;
			}
		}
		
	}

}
