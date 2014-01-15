/**
 * 
 */
package index;

import io.IO;
import io.RW;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.math.BigInteger;

/**
 * @author chenqian
 *
 */
public class RInnerNode implements RW {

	String prex 		= null;
	BigInteger g_pi_su 	= null;
	BigInteger sigma 	= null;
	int lb, rb;
	
	/**
	 * 
	 */
	public RInnerNode() {
		// TODO Auto-generated constructor stub
	}

	/* (non-Javadoc)
	 * @see IO.RW#read(java.io.DataInputStream)
	 */
	@Override
	public void read(DataInputStream ds) {
		// TODO Auto-generated method stub
		try {
			prex = IO.readString(ds);
			g_pi_su = IO.readBigInteger(ds);
			sigma = IO.readBigInteger(ds);
			lb = ds.readInt();
			rb = ds.readInt();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/* (non-Javadoc)
	 * @see IO.RW#write(java.io.DataOutputStream)
	 */
	@Override
	public void write(DataOutputStream ds) {
		// TODO Auto-generated method stub
		try {
			IO.writeString(ds, prex);
			IO.writeBigInteger(ds, g_pi_su);
			IO.writeBigInteger(ds, sigma);
			ds.writeInt(lb);
			ds.writeInt(rb);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
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
