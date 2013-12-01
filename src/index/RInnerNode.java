/**
 * 
 */
package index;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.math.BigInteger;

import IO.DataIO;
import IO.RW;

/**
 * @author chenqian
 *
 */
public class RInnerNode extends RW {

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
			prex = DataIO.readString(ds);
			g_pi_su = DataIO.readBigInteger(ds);
			sigma = DataIO.readBigInteger(ds);
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
			DataIO.writeString(ds, prex);
			DataIO.writeBigInteger(ds, g_pi_su);
			DataIO.writeBigInteger(ds, sigma);
			ds.writeInt(lb);
			ds.writeInt(rb);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/* (non-Javadoc)
	 * @see IO.RW#loadBytes(byte[])
	 */
	@Override
	public void loadBytes(byte[] data) {
		// TODO Auto-generated method stub
		DataInputStream ds = new DataInputStream(new ByteArrayInputStream(data));
		read(ds);
	}

	/* (non-Javadoc)
	 * @see IO.RW#toBytes()
	 */
	@Override
	public byte[] toBytes() {
		// TODO Auto-generated method stub
		ByteArrayOutputStream bs = new ByteArrayOutputStream();
		DataOutputStream ds = new DataOutputStream(bs);
		write(ds);
		return bs.toByteArray();
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
