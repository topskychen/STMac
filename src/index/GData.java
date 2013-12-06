/**
 * 
 */
package index;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream.GetField;
import java.math.BigInteger;
import java.nio.ByteBuffer;

import IO.DataIO;
import crypto.Gfunction;
import crypto.Hasher;
import crypto.PMAC;

/**
 * @author chenqian
 *
 */
public class GData extends BData{

	Gfunction gf2 = null, gf3 = null;
	byte[] gf1 = null, gf4 = null;
			
	/**
	 * 
	 */
	public GData() {
		// TODO Auto-generated constructor stub
	}
	
	public static byte[] getHash(int t) {
		return ByteBuffer.allocate(4).putInt(t).array();
	}
	
	public static byte[] getHash(byte[] a, int t) {
		return getHash(a, getHash(t));
	}
	
	public static byte[] getHash(byte[] a, byte[] b) {
		return Hasher.computeGeneralHashValue(new byte[][]{a, b});
	}
	
	public GData(String location, int t1, int t2, int t4, byte[] gf1, Gfunction gf2, byte[] gf4, PMAC pmac, boolean buildLater) {
		this.prex = location;
		this.t1 = t1;
		this.t2 = t2;
		this.t3 = t2;
		this.t4 = t4;
		if (!buildLater){
			BigInteger[] sigma_r = pmac.generatePMAC(getPrex(), getHash(gf1, getT1()), getHash(gf2.getDigest(), getT2()), getHash(gf4, getT4()));
			this.sigma = sigma_r[0];
			this.g_pi_su = pmac.generateGPiSu(getPrex(), sigma_r[1], getPrex().length());
		}
		this.gf1 = gf1;
		this.gf2 = gf2;
		this.gf3 = gf2;
		this.gf4 = gf4;
//		System.out.println(timeStampsToString() + testData(pmac, getPrex()));
	}
	
	public void buildData(PMAC pmac) {
		BigInteger[] sigma_r = pmac.generatePMAC(getPrex(), getHash(gf1, getT1()), getHash(gf2.getDigest(), getT2()), getHash(gf4, getT4()));
		this.sigma = sigma_r[0];
		this.g_pi_su = pmac.generateGPiSu(getPrex(), sigma_r[1], getPrex().length());
	}

	public GData(Data[] data, int slots, PMAC pmac) {
		String[] tra = new String[slots];
		BigInteger[] g_pi_sus = new BigInteger[slots];
		this.sigma = BigInteger.ONE;
		for (int i = 0; i < slots; i ++) {
			tra[i] = ((GData) data[i]).getPrex();
			g_pi_sus[i] = ((GData) data[i]).getG_pi_su();
			this.sigma = this.sigma.multiply(((GData) data[i]).getSigma()).mod(pmac.n);
		}
		this.prex = DataIO.commonPrefix(tra, 0, slots - 1);
		this.g_pi_su = pmac.increTraGPiSu(tra, g_pi_sus, 0, slots - 1, getPrex().length());
		this.t1 = ((GData) data[0]).getT1();
		this.t2 = ((GData) data[0]).getT2();
		this.t3 = ((GData) data[slots - 1]).getT3();
		this.t4 = ((GData) data[slots - 1]).getT4();
		this.gf1 = ((GData) data[0]).getGf1();
		this.gf2 = ((GData) data[0]).getGf2();
		this.gf3 = ((GData) data[slots - 1]).getGf3();
		this.gf4 = ((GData) data[slots - 1]).getGf4();
//		System.out.println(timeStampsToString() + testData(pmac, getPrex()));
	}
	
	public boolean testData(PMAC pmac, String prex) {
		BigInteger inc_g_pi_su = pmac.increGPiSu(getPrex(), g_pi_su, prex.length());
		return pmac.verify(sigma, pmac.generatePMACbyPrex(inc_g_pi_su, pmac.generatePix(prex), getHash(gf1, t1), getHash(gf2.getDigest(), t2), getHash(gf3.getDigest(), t3), getHash(gf4, t4)));
	}
	
	/* (non-Javadoc)
	 * @see IO.RW#read(java.io.DataInputStream)
	 */
	@Override
	public void read(DataInputStream ds) {
		// TODO Auto-generated method stub
		super.read(ds);
		if (getT2() == getT3()) {
			gf2 = new Gfunction(); gf2.read(ds);
			gf3 = gf2;
		}
		gf1 = DataIO.readBytes(ds);
		gf4 = DataIO.readBytes(ds);
	}

	/* (non-Javadoc)
	 * @see IO.RW#write(java.io.DataOutputStream)
	 */
	@Override
	public void write(DataOutputStream ds) {
		// TODO Auto-generated method stub
		super.write(ds);
		if (getT2() == getT3()) {
			gf2.write(ds);
		}
//		System.out.println(gf1.length);
		DataIO.writeBytes(ds, gf1);
		DataIO.writeBytes(ds, gf4);
	}
	
	public Gfunction getGf2() {
		return gf2;
	}

	public void setGf2(Gfunction gf2) {
		this.gf2 = gf2;
	}

	public Gfunction getGf3() {
		return gf3;
	}

	public void setGf3(Gfunction gf3) {
		this.gf3 = gf3;
	}

	public byte[] getGf1() {
		return gf1;
	}

	public void setGf1(byte[] gf1) {
		this.gf1 = gf1;
	}

	public byte[] getGf4() {
		return gf4;
	}

	public void setGf4(byte[] gf4) {
		this.gf4 = gf4;
	}
	
	public String timeStampsToString() {
		return new String("[" + getT1() + "|" + getT2() + ", " + getT3() + "|" + getT4() + "]");
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		GData data = new GData();
		if (data instanceof GData) System.out.println(true);
	}

}
