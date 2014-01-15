/**
 * 
 */
package index;

import io.RW;

import java.io.DataInputStream;
import java.io.DataOutputStream;

/**
 * @author chenqian
 *
 */
public class Data implements RW {

	public static int B_TYPE 	= 0;
	public static int G_TYPE 	= 1;
	
	public static String[] TYPE_NAMES = {"BData", "GData"};
	
	/**
	 * 
	 */
	public Data() {
		// TODO Auto-generated constructor stub
	}

	/* (non-Javadoc)
	 * @see IO.RW#read(java.io.DataInputStream)
	 */
	@Override
	public void read(DataInputStream ds) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see IO.RW#write(java.io.DataOutputStream)
	 */
	@Override
	public void write(DataOutputStream ds) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see IO.RW#toString()
	 */
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
