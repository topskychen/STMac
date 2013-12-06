/**
 * 
 */
package utility;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Scanner;

import index.OptimizedNode;
import index.OptimizedTree;

/**
 * @author chenqian
 *
 */
public class EncodingConverter {

	public void init() {
		
	}
	
	/**
	 * 
	 */
	public EncodingConverter(String fileName) {
		// TODO Auto-generated constructor stub
		File file = new File(fileName);
		File fileOut = new File(fileName + ".enc");
		Scanner in;
		try {
			in = new Scanner(file); int num = Integer.parseInt(in.nextLine());
			
			/**
			 * Prepare Nodes
			 */
			OptimizedTree optimizedTree = new OptimizedTree();
			OptimizedNode[] onodes = new OptimizedNode[num];
			Node[] nodes = new Node[num];
			for (int i = 0; i < num; i ++) {
				String[] tks = in.nextLine().split("\t");
				nodes[i] = new Node(tks[0], i, Integer.parseInt(tks[1]));
//				nodes.add(new OptimizedNode(prex, value))
			}
			Arrays.sort(nodes);
			
			
			/**
			 * Generate Encodings
			 */
			for (int i = 0; i < num; i ++) {
				onodes[i] = new OptimizedNode(nodes[i].pre, nodes[i].id);
			}
			optimizedTree.buildTree(onodes, onodes.length);
//			System.out.println(optimizedTree.toString());
			
			/**
			 * Prepare to print out
			 */
			ArrayList<String> encodings = new ArrayList<String>();
			EncNode[] nodesPrint = new EncNode[num];
			optimizedTree.getEncodings(encodings);
			PrintWriter pw = new PrintWriter(fileOut);
			for (int i = 0; i < encodings.size(); i ++) {
//				System.out.println(encodings.get(i).length() + ", " + nodes[i].id);
				nodesPrint[i] = new EncNode(encodings.get(i), nodes[i].id, nodes[i].time);
			}
			Arrays.sort(nodesPrint);
			pw.println(num);
			for (int i = 0; i < num; i ++) {
				pw.println(nodesPrint[i].pre + "\t" + nodesPrint[i].time);
			}
			pw.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	class EncNode implements Comparable<EncNode> {
		String pre;
		Integer id;
		int time;
		@Override
		
		public int compareTo(EncNode o) {
			// TODO Auto-generated method stub
			return id.compareTo(o.id);
		}
		public EncNode(String pre, Integer id, int time) {
			super();
			this.pre = pre;
			this.id = id;
			this.time = time;
		}
		
	} 
	
	class Node implements Comparable<Node>{
		String pre;
		Integer id;
		int time;
		
		Node (String pre, int id, int time) {
			this.pre = pre;
			this.id = id;
			this.time = time;
		}
		
		@Override
		public int compareTo(Node o) {
			// TODO Auto-generated method stub
			return pre.compareTo(o.pre);
		}
	}
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		EncodingConverter encodingConverter = new EncodingConverter("./dataset/1000.txt_x");
		encodingConverter = new EncodingConverter("./dataset/10000.txt_x");
		encodingConverter = new EncodingConverter("./dataset/100000.txt_x");
		encodingConverter = new EncodingConverter("./dataset/1000.txt_y");
		encodingConverter = new EncodingConverter("./dataset/10000.txt_y");
		encodingConverter = new EncodingConverter("./dataset/100000.txt_y");
	}

}
