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

import crypto.PMAC;
import IO.DataIO;

/**
 * @author chenqian
 *
 */
public class BData extends Data {

	

	String prex 		= null;
	BigInteger g_pi_su 	= null;
	BigInteger sigma	= null;
	int t1, t2, t3, t4;
	
	/**
	 * 
	 */
	public BData() {
		// TODO Auto-generated constructor stub
	}
	
	public BData(String location, int t1, int t2, int t3, BigInteger sigma,
			BigInteger r, PMAC pmac, boolean buildLater) {
		super();
		this.prex = location;
		this.t1 = t1;
		this.t2 = t2;
		this.t3 = t2;
		this.t4 = t3;
		this.sigma = sigma;
		if (!buildLater) {
			this.g_pi_su = pmac.generateGPiSu(this.prex, r, this.prex.length());
		}
//		System.out.println(timeStampsToString() + testData(pmac, prex));
	}
	
	public void buildData(PMAC pmac, BigInteger r) {
		this.g_pi_su = pmac.generateGPiSu(this.prex, r, this.prex.length());
	}

	public BData(Data[] data, int slots, PMAC pmac) {
		String[] tra = new String[slots];
		BigInteger[] g_pi_sus = new BigInteger[slots];
		sigma = BigInteger.ONE;
		for (int i = 0; i < slots; i ++) {
			tra[i] = ((BData) data[i]).prex;
			g_pi_sus[i] = ((BData) data[i]).g_pi_su;
			sigma = sigma.multiply(((BData) data[i]).sigma).mod(pmac.n);
		}
		prex = DataIO.commonPrefix(tra, 0, slots - 1);
		g_pi_su = pmac.increTraGPiSu(tra, g_pi_sus, 0, slots - 1, prex.length());
		t1 = ((BData) data[0]).getT1();
		t2 = ((BData) data[0]).getT2();
		t3 = ((BData) data[slots - 1]).getT3();
		t4 = ((BData) data[slots - 1]).getT4();
//		System.out.println(timeStampsToString() + testData(pmac, prex));
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
			t1 = ds.readInt();
			t2 = ds.readInt();
			t3 = ds.readInt();
			t4 = ds.readInt();
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
			ds.writeInt(t1);
			ds.writeInt(t2);
			ds.writeInt(t3);
			ds.writeInt(t4);
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
		StringBuffer sb = new StringBuffer();
		sb.append(prex + "(" + t1 + ", " + t2 + "), (" + t3 + "," + t4 + ")");
		return sb.toString();
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}
	

	public boolean testData(PMAC pmac, String prex) {
		BigInteger inc_g_pi_su = pmac.increGPiSu(this.prex, g_pi_su, prex.length());
		return pmac.verify(sigma, pmac.generatePMACbyPrex(inc_g_pi_su, pmac.generatePix(prex), t1, t2, t3, t4));
	}
	
	public String getPrex() {
		return prex;
	}

	public void setPrex(String prex) {
		this.prex = prex;
	}

	public BigInteger getG_pi_su() {
		return g_pi_su;
	}

	public void setG_pi_su(BigInteger g_pi_su) {
		this.g_pi_su = g_pi_su;
	}

	public BigInteger getSigma() {
		return sigma;
	}

	public void setSigma(BigInteger sigma) {
		this.sigma = sigma;
	}

	public int getT1() {
		return t1;
	}

	public void setT1(int t1) {
		this.t1 = t1;
	}

	public int getT2() {
		return t2;
	}

	public void setT2(int t2) {
		this.t2 = t2;
	}

	public int getT3() {
		return t3;
	}

	public void setT3(int t3) {
		this.t3 = t3;
	}

	public int getT4() {
		return t4;
	}

	public void setT4(int t4) {
		this.t4 = t4;
	}
	
	public String timeStampsToString() {
		return new String("[" + t1 + "|" + t2 + ", " + t3 + "|" + t4 + "]");
	}
}
