/**
 * 
 */
package party;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import index.BData;
import index.BinarySearchTree;
import index.GData;
import index.GeneralSearchTree;
import index.Query;
import index.SearchIndex;
import index.ThreadSearchTree;
import index.Trajectory;
import crypto.PMAC;
import crypto.VO;

/**
 * @author chenqian
 *
 */
public class Prover extends PMAC{

	Trajectory 		trajectory	= null;
	SearchIndex 	index		= null;
	
	public Prover() {
		super();
	}
	
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}
	
	public String toString() {
		StringBuffer sb = new StringBuffer("prover: \n");
		sb.append("trajectory " + trajectory.toString() + "\n");
		sb.append(super.toString() + "\n");
		return sb.toString();
	}
	
	/**
	 * @param x
	 * @param authenticator
	 */
	public void requestPMAC(Trajectory trajectory, Generator generator, int start, int end) {
		// TODO Auto-generated method stub
		this.trajectory = trajectory;
//		generator.generatePMAC(this.trajectory.getLocation(), -1, 0, 1);
		generator.generatePMAC(trajectory, start, end);
	}
	
	public void prepareTraPMAC(String traFileName, String traPMACFileName, Generator generator) throws IOException {
		this.trajectory = new Trajectory();
		File traFile = new File(traPMACFileName);
		if (traFile.exists()) {
			DataInputStream dis = new DataInputStream(new BufferedInputStream(new FileInputStream(traFile)));
			trajectory.read(dis);
			System.out.println("Trajectory data is loaded.");
		} else {
			trajectory.prepare(traFileName);
			this.requestPMAC(trajectory, generator, 1, trajectory.length());
			DataOutputStream dos = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(traFile)));
			trajectory.write(dos);
			dos.flush(); dos.close();
			System.out.println("Trajectory data is computed and stored.");
		}
//		System.out.println(this.toString());
//		System.out.println("sigma @2: " + trajectory.getSigma(2));
	}
	
	/**
	 * indexType = 0, means the Btree like
	 * indexType = 1, means the Rtree like
	 * @param fileName
	 * @param generator
	 * @param indexType
	 */
	public void prepareIndex(String fileName, Generator generator, int indexType) {
		File file = null;
		PMAC pmac = null;
		Class classValue = null;
		if (indexType == SearchIndex.ThreadSearchTree) {
			classValue = BData.class;
			file = new File(fileName + ".idx");
			index = new ThreadSearchTree();
			pmac = this;
//			System.out.println(index.toString());
		} else if (indexType == SearchIndex.GeneralSearchTree) {
			classValue = GData.class;
			file = new File(fileName + ".gen.dat");
			index = new GeneralSearchTree(classValue);
			pmac = generator;
		} else if (indexType == SearchIndex.BinarySearchTree) {
			classValue = BData.class;
			file = new File(fileName + ".bin.dat");
			index = new BinarySearchTree(classValue);
			pmac = this;
		} else {
			throw new IllegalStateException("No such index type.");
		}
		if (file.exists()) {
			index.leadTree(new Object[]{fileName, classValue, classValue});
			System.out.println("Index is loaded.");
		} else {
			index.createTree(new Object[]{fileName, "5", "6", classValue, classValue});
			index.buildIndex(trajectory, pmac);
			System.out.println("Index is built.");
		}
//		System.out.println(index.toString());
	}
	
	/**
	 * init the keys at the client
	 * @param pmac2
	 */
	public void initKey(PMAC pmac) {
		// TODO Auto-generated method stub
		this.g = pmac.g;
		this.n = pmac.n;
		this.e = pmac.e;
//		this.sk = pmac.sk;// this is only for testing
//		this.phi_n = pmac.phi_n;// this is only for testing
		this.mappingTable = pmac.mappingTable;
	}

	/**
	 * Prepare VO with trajectory
	 * @param q
	 * @param client
	 * @return
	 */
	public VO prepareVOwithTrajectory(Query query) {
		// TODO Auto-generated method stub
		VO vo = new VO();
		vo.prepare(this, trajectory, query);
		return vo;
	}
	
	public VO prepareVOwithIndex(Query query) {
		VO vo = new VO();
		vo.prepare(this, index, query);
		return vo;
	}
}
