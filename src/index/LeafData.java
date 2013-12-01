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
public class LeafData extends Data{

	String 		location 	= null;
	int 		t1, t2, t3;
	BigInteger 	sigma 		= null;
	BigInteger 	r			= null;
	
	/**
	 * 
	 */
	public LeafData() {
		// TODO Auto-generated constructor stub
	}
	

	public LeafData(String location, int t1, int t2, int t3, BigInteger sigma,
			BigInteger r) {
		super();
		this.location = location;
		this.t1 = t1;
		this.t2 = t2;
		this.t3 = t3;
		this.sigma = sigma;
		this.r = r;
	}

	public boolean testData(PMAC pmac, String prex) {
		BigInteger g_pi_su = pmac.generateGPiSu(location, r, prex.length());
		return pmac.verify(sigma, pmac.generatePMACbyPrex(g_pi_su, pmac.generatePix(prex), t1, t2, t3));
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

	@Override
	public void write(DataOutputStream ds) {
		// TODO Auto-generated method stub
		try {
			DataIO.writeString(ds, location);
			ds.writeInt(t1);
			ds.writeInt(t2);
			ds.writeInt(t3);
			DataIO.writeBigInteger(ds, sigma);
			DataIO.writeBigInteger(ds, r);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public byte[] toBytes() {
		// TODO Auto-generated method stub
		ByteArrayOutputStream bs = new ByteArrayOutputStream();
		DataOutputStream ds = new DataOutputStream(bs);
		write(ds);
		return bs.toByteArray();
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		StringBuffer sb = new StringBuffer();
		sb.append(location + " @ " + t2);
		return sb.toString();
	}

	@Override
	public void read(DataInputStream ds) {
		// TODO Auto-generated method stub
		try {
			location = DataIO.readString(ds);
			t1 = ds.readInt();
			t2 = ds.readInt();
			t3 = ds.readInt();
			sigma = DataIO.readBigInteger(ds);
			r = DataIO.readBigInteger(ds);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	@Override
	public void loadBytes(byte[] data) {
		// TODO Auto-generated method stub
		DataInputStream ds = new DataInputStream(new ByteArrayInputStream(data));
		read(ds);
	}


	public String getLocation() {
		return location;
	}


	public void setLocation(String location) {
		this.location = location;
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


	public BigInteger getSigma() {
		return sigma;
	}


	public void setSigma(BigInteger sigma) {
		this.sigma = sigma;
	}


	public BigInteger getR() {
		return r;
	}


	public void setR(BigInteger r) {
		this.r = r;
	}


}
