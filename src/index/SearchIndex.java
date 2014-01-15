/**
 * 
 */
package index;

import io.RW;

import java.util.ArrayList;

import crypto.PMAC;

/**
 * @author chenqian
 *
 */
public abstract interface SearchIndex {

	public static int ThreadSearchTree 		= 0;
	public static int GeneralSearchTree 	= 1;
	public static int BinarySearchTree 		= 3;

	public abstract String toString();
//	
	public abstract ArrayList<Data> rangeQuery(Query query);
	public abstract void buildIndex(Trajectory tra, PMAC pmac);
	public abstract void leadTree(Object[] args);
	public abstract void createTree(Object[] args);
//	/**
//	 * 
//	 */
//	public SearchIndex() {
//		// TODO Auto-generated constructor stub
//	}
//
//	/**
//	 * @param args
//	 */
//	public static void main(String[] args) {
//		// TODO Auto-generated method stub
//
//	}

}
