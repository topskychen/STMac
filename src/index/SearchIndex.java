/**
 * 
 */
package index;

import java.util.ArrayList;

import crypto.PMAC;
import IO.RW;

/**
 * @author chenqian
 *
 */
public abstract class SearchIndex {

	public static int ThreadSearchTree 		= 0;
	public static int GeneralSearchTree 	= 1;

	public abstract String toString();
//	
	public abstract ArrayList<Data> rangeQuery(Query query);
	public abstract void buildIndex(Trajectory tra, PMAC pmac);
	
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
