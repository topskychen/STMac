package crypto;

import index.Trajectory;

import java.math.*;
import java.util.*;
import java.io.*;

import IO.DataIO;
import IO.RW;

/**
 * This program calculates the PMAC value of given string x.
 * 
  */
public class PMAC implements RW{

	/**
	 * Here note n is composed of two large primes.
	 * */
	public BigInteger n = null, g = null, e = null;
	
	public BigInteger phi_n = null, d = null;
//	public BigInteger r = null, g_r = null; // here r is for randomness.
	public byte[] sk = null;
	
	private final static int BITLENGTH = 1024;
	private final static int CERTAINTY = 100;
	private final static int MVALUE = 32;
	private final static int CVALUE = 2;

	private int bitLength;
//	
//	private final static int T_START = 1; // time stamp begins from 1, not 0
//	private final static int T_END = 8;
	
	int[] t; // timestamp array

	public BigInteger[][] mappingTable = null;
	
	static {
		Hasher.setInstance("MD5");
	}
	
	/**
	 * Constructs an instance of the PMAC with BITLENGTH of modulus and at least
	 * 1-2^(-CERTAINTY) certainty of primes generation.
	 */
	public PMAC() {
	}
	
	public PMAC(String fileName) {
		File file = new File(fileName);
		if (file.exists()) {
			try {
				read(new DataInputStream(new FileInputStream(file)));
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}else {
			initKey();
			try {
				write(new DataOutputStream(new FileOutputStream(file)));
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	public PMAC(int[] _t) {
		keyGeneration(BITLENGTH, CERTAINTY);
		PrimeMatrix papp = new PrimeMatrix();
		mappingTable = papp.genMatrix(MVALUE, CVALUE);
		t = new int[_t.length];
		for (int i = 0; i < _t.length; i++) {
			t[i] = _t[i];
		}
	}
		
	public void initKey() {
		keyGeneration(BITLENGTH, CERTAINTY);
		PrimeMatrix papp = new PrimeMatrix();
		mappingTable = papp.genMatrix(MVALUE, CVALUE);
	}

	
	public void checkInitKeys() {
		if (g == null) {
			throw new IllegalStateException("Keys are not initialized, maybe intikey function is required to be called.");
		}
	}

//	/**
//	 * @param x
//	 *            string x which denotes the locaton info
//	 * @return the TT(x) value of string x
//	 */
//	public BigInteger generatePix(String x) {
//		BigInteger Pix = BigInteger.ONE;
//		for (int i = 0; i < x.length(); i++) {
//			Pix = Pix.multiply(mappingTable[i][x.charAt(i) - '0']).mod(phi_p);
//		}
//		return Pix;
//	}
//	
	/**
	 * @param x
	 *            string x which denotes the locaton info
	 * @return the TT(x) value of string x
	 */
	public BigInteger generatePix(String x) {
		checkInitKeys();
		BigInteger Pix = BigInteger.ONE;
		for (int i = 0; i < x.length(); i++) {
			Pix = Pix.multiply(mappingTable[i][x.charAt(i) - '0']).mod(phi_n);
		}
//		Pix = Pix.multiply(r).mod(phi_p);
		return Pix;
	}
	

//	public BigInteger generateAggrePix(String[] x) {
//		x = new String[T_END - T_START];
//		BigInteger aggrePix = BigInteger.valueOf(1);
//		for (int i = 0; i < T_END - T_START; i++) {
//			aggrePix = aggrePix.multiply(generatePix(x[i])).mod(phi_p);
//		}
//		return aggrePix;
//	}

	/**
	 * compute pi(su) 
	 * NOTE: this function is called by the prover, thus phi_n cannot be used.
	 * @param su
	 *            string which denotes the suffix
	 * @param d
	 *            length of predicate, the index where suffix begins
	 * @return the pi(su)
	 */
	public BigInteger generatePiSu(String su, int d) {
		checkInitKeys();
		BigInteger PiSux = BigInteger.ONE;
		for (int i = d; i < su.length(); i++) {
			PiSux = PiSux.multiply(mappingTable[i][su.charAt(i) - '0']);
		}
//		PiSux = PiSux.multiply(r);
//		PiSux = PiSux.multiply(timeDigest(i_t)).mod(phi_n);
		return PiSux;
	}

	/**
	 * compute g^pi(su, t) 
	 * 
	 * @param su
	 *            string which denotes the suffix
	 * @param d
	 *            length of predicate, the index where suffix begins
	 * @return the g^pi(su, t)
	 */
	public BigInteger generateGPiSu(String su, BigInteger r, int d){
		return g.modPow(generatePiSu(su, d).multiply(r), n);
	}

	/**
	 * Increment g_pi_su
	 * 
	 * @param su
	 * 				old prex
	 * @param g_pi_su
	 * 				
	 * @param d
	 * 				length of new prex
	 * @return
	 */
	public BigInteger increGPiSu(String su, BigInteger g_pi_su, int d) {
		return g_pi_su.modPow(generatePiSu(su, d), n);
	}
	
	/**
	 * @param su[]
	 * 			strings denote suffixes
	 * @param start
	 * 			time start
	 * @param end
	 * 			time end
	 * @param d
	 * 			length of predicate, the index begins
	 *  @return g^pi(x[i])
	 */
	public BigInteger generateTraPiSu(String[] su, BigInteger[] rs, int start, int end, int d) {
		checkInitKeys();
		BigInteger result = BigInteger.ZERO;
		for (int i = start; i <= end; i++) {
			result = result.add(generatePiSu(su[i], d).multiply(rs[i]));
		}		
		return result;
	}
	
	/**
	 * 
	 * @param su
	 * @param rs
	 * @param start
	 * @param end
	 * @param d
	 * @return
	 */
	public BigInteger generateTraGPiSu(String[] su, BigInteger[] rs, int start, int end, int d) {
		checkInitKeys();
		return g.modPow(generateTraPiSu(su, rs, start, end, d), n);
	}

	public BigInteger increTraGPiSu(String[] su, BigInteger[] g_pi_sus, int start, int end, int d) {
		BigInteger ans = BigInteger.ONE;
		for (int i = start; i <= end; i ++) {
			ans = ans.multiply(increGPiSu(su[i], g_pi_sus[i], d)).mod(n);
		}
		return ans;
	} 

	/**
	 * 
	 * @param t
	 * @return
	 */
	public BigInteger getPsiTime(int t) {
		String ts = "" + t;
		return AES.encryptBI(sk, Hasher.hashBytes(ts.getBytes()));
	}
	
	/**
	 * 
	 * @param o
	 * @return
	 */
	public BigInteger getPsiObject(Object o) {
		if (o instanceof byte[]) {
			return AES.encryptBI(sk, Hasher.hashBytes((byte[])o));
		} else {
			return AES.encryptBI(sk, Hasher.hashBytes(o.toString().getBytes()));
		}
	}
	
	/**
	 * 
	 * @param t1
	 * @param t2
	 * @param t3
	 * @param t4
	 * @return
	 */
	public BigInteger getPsiTime(int t1, int t2, int t3, int t4) {
//		return BigInteger.ONE;
		return getPsiTime(t2).subtract(getPsiTime(t1)).add(getPsiTime(t3)).subtract(getPsiTime(t4)).mod(phi_n);
	}

	/**
	 * 
	 * @param o1
	 * @param o2
	 * @param o3
	 * @param o4
	 * @return
	 */
	public BigInteger getPsiObject(Object o1, Object o2, Object o3, Object o4) {
//		return BigInteger.ONE;
		return getPsiObject(o2).subtract(getPsiObject(o1)).
				add(getPsiObject(o3)).subtract(getPsiObject(o4)).mod(phi_n);
	}
	
	/**
	 * compute hash of time stamps t[i], based on t[i] + t[i-1] + t[i+1]
	 * 
	 * @param i
	 *            index
	 */
	public BigInteger timeDigest(int i) {
		if (i < 0) {
			throw new IllegalArgumentException();
		} else if (i == 0) { // time domain not included
			return BigInteger.ONE;
		} else {
			String ts = "" + t[i] + "|" + t[i - 1] + "|" + t[i + 1];
//			Hasher.hashString(ts);
			return new BigInteger(Hasher.hashString(ts), 16);
		}
	}

	/**
	 * compute PMAC by pre
	 * @param g_pi_su
	 * 					g^pi(su)
	 * @param pi_prex 
	 * 					pi(pre(x))
	 * @return PMAC
	 * 
	 * */
	public BigInteger generatePMACbyPrex(BigInteger g_pi_su, BigInteger pi_prex, int t1, int t2, int t3){
		return generatePMACbyPrex(g_pi_su, pi_prex, t1, t2, t2, t3);
	}
	
	/**
	 * 
	 * @param g_pi_su
	 * @param pi_prex
	 * @param o1
	 * @param o2
	 * @param o3
	 * @return
	 */
	public BigInteger generatePMACbyPrex(BigInteger g_pi_su, BigInteger pi_prex, 
			Object o1, Object o2, Object o3){
		return generatePMACbyPrex(g_pi_su, pi_prex, o1, o2, o2, o3);
	}
	
	/**
	 * compute PMAC by pre
	 * @param g_pi_su
	 * 					g^pi(su)
	 * @param pi_prex 
	 * 					pi(pre(x))
	 * @return PMAC
	 * 
	 * */
	public BigInteger generatePMACbyPrex(BigInteger g_pi_su, BigInteger pi_prex, 
			int t1, int t2, int t3, int t4){
		return g_pi_su.modPow(pi_prex, n).multiply(g.modPow(getPsiTime(t1, t2, t3, t4), n)).mod(n);
	}
	
	/**
	 * 
	 * @param g_pi_su
	 * @param pi_prex
	 * @param o1
	 * @param o2
	 * @param o3
	 * @param o4
	 * @return
	 */
	public BigInteger generatePMACbyPrex(BigInteger g_pi_su, BigInteger pi_prex, 
			Object o1, Object o2, Object o3, Object o4){
		return g_pi_su.modPow(pi_prex, n).multiply(g.modPow(getPsiObject(o1, o2, o3, o4), n)).mod(n);
	}

	
	
	/**
	 * generate PMAC for single point (x[i],t[i]) in trajectory TX
	 * 
	 * @param x
	 *            client's location plain text
	 * @param i
	 *            timestamp index for point
	 * @return PMAC value of input point
	 */
	public BigInteger[] generatePMAC(String x, int t1, int t2, int t3) {
//		BigInteger r = BigInteger.probablePrime(bitLength, new Random());
		BigInteger r = Constants.PRIME_P;
		BigInteger exp = generatePix(x).multiply(r).mod(phi_n);
		exp = exp.add(getPsiTime(t1, t2, t2, t3)).mod(phi_n);
		exp = exp.multiply(d).mod(phi_n);
		return new BigInteger[]{g.modPow(exp, n), r};
	}
	
	/**
	 * 
	 * @param x
	 * @param o1
	 * @param o2
	 * @param o3
	 * @return
	 */
	public BigInteger[] generatePMAC(String x, Object o1, Object o2, Object o3) {
//		BigInteger r = BigInteger.probablePrime(bitLength, new Random());
		BigInteger r = Constants.PRIME_P;
		BigInteger exp = generatePix(x).multiply(r).mod(phi_n);
		exp = exp.add(getPsiObject(o1, o2, o2, o3)).mod(phi_n);
		exp = exp.multiply(d).mod(phi_n);
		return new BigInteger[]{g.modPow(exp, n), r};
	}
	
	public void generatePMAC(Trajectory tra, int start, int end) {
		BigInteger[] sigma_r = new BigInteger[2];
		for (int i = start; i <= end; i ++ ) {
			sigma_r = generatePMAC(tra.getLocation(i), 
					tra.getTimeStamp(i - 1), tra.getTimeStamp(i), tra.getTimeStamp(i + 1));
			tra.setSigma(i, sigma_r[0]);
			tra.setR(i, sigma_r[1]);
		}
	}
	
	public BigInteger aggregatePMACs(Trajectory tra, int start, int end) {
		BigInteger aggPMAC = BigInteger.ONE;
		for (int i = start; i <= end; i ++) {
			if (tra.getSigma(i) == null) {
				throw new NullPointerException("Sigma @" + i + " is empty");
			}
			aggPMAC = aggPMAC.multiply(tra.getSigma(i)).mod(n);
		}
		return aggPMAC;
	}
	
//	/**
//	 * compute PMAC by pre
//	 * @param g_pi_su
//	 * 					g^pi(su)
//	 * @param pi_prex 
//	 * 					pi(pre(x))
//	 * @return PMAC
//	 * 
//	 * */
//	public BigInteger generateTraPMACbyPrex(BigInteger g_pi_su, BigInteger pi_prex, int t1, int t2, int t3, int t4){
//		BigInteger res = g_pi_su.modPow(pi_prex, n);
//		BigInteger res2 = g.modPow(getPsiTime(t1, t2, t3, t4), n);
//		return res.multiply(res2).mod(n);
//	}
	
//	public BigInteger generateTraPMAC(String x[], int start, int end) {
////		BigInteger pmac[] = new BigInteger[end - start];
//		BigInteger res = BigInteger.ONE;
//		for (int i = start; i <= end; i++) {
//			res = res.multiply(generatePMAC(x[i], t[i - 1], t[i], t[i + 1]));
//		}
//		return res;
//	}
	
	/**
	 * generate keys.
	 * 
	 * @param bitLength
	 *            number of bits of modulus.
	 * @param certainty
	 *            The probability that the new BigInteger represents a prime
	 *            number will exceed (1 - 2^(-certainty)). The execution time of
	 *            this constructor is proportional to the value of this
	 *            parameter.
	 * @return a BigInteger array key keeping g, p, r
	 */
	public BigInteger[] keyGeneration(int bitLength, int certainty) {

		this.bitLength = bitLength; 
		BigInteger key[] = new BigInteger[7];

		// ToDo: how to test g
		g = new BigInteger("2");

		/**
		 * Constructs two randomly generated positive BigIntegers that are
		 * probably prime, with the specified bitLength and certainty.
		 */
		BigInteger p = new BigInteger(bitLength / 2, certainty, new Random());
		BigInteger q = new BigInteger(bitLength / 2, certainty, new Random());
		n = p.multiply(q);
		phi_n = p.subtract(BigInteger.ONE).multiply(q.subtract(BigInteger.ONE));
		e =	RSA.PRIME_P;
		while(phi_n.gcd(e).compareTo(BigInteger.ONE) > 0) e = e.add(new BigInteger("2"));
		d = e.modInverse(phi_n);
//		r = new BigInteger(bitLength, certainty, new Random());
//		g_r = g.modPow(r, n);
		sk = AES.getSampleKey();
		
		/**
		 * Set the instance of Hasher to MD5, since it is 16 bytes, we use the digest as the input of AES.
		 */
//		Hasher.setInstance("MD5");
		/**
		 * store g, r, p in the array key for future use, any better assignment
		 * form?
		 */
		key[0] = g;
		key[1] = p;
		key[2] = q;
//		key[3] = r; 
		key[4] = new BigInteger(DataIO.toHexFromBytes(sk), 16); 
		key[5] = e;
		key[6] = d;

		return key;

	}

	public String toString() {
		StringBuffer sb = new StringBuffer("");
		sb.append("n: " + n + "\n");
		sb.append("g: " + g + "\n");
		sb.append("e: " + e + "\n");
		sb.append("phi_n: " + phi_n + "\n");
		sb.append("d: " + d + "\n");
		sb.append("sk: " + DataIO.toHexFromBytes(sk) + "\n");
//		for (int i = 0; i < MVALUE; i ++) {
//			for (int j = 0; j < CVALUE; j ++) {
//				sb.append(mappingTable[i][j] + ", ");
//			}
//			sb.append("\n");
//		}
		return sb.toString();
	}
	
	public boolean verify(BigInteger pmac, BigInteger component) {
		return pmac.modPow(e, n).equals(component);
	}
	
	/**
	 * 
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		PMAC pmac = new PMAC("./database/pmac.keys"); //pmac.initKey();
		String x = "01110011100011100110001000010110";
		String prex = "0111";
//		System.out.println("Encrypted message is: " + x);
		BigInteger[] sigma_r = pmac.generatePMAC(x, "0".getBytes(), "1".getBytes(), "2".getBytes());
		BigInteger sigma = sigma_r[0];
		BigInteger r = sigma_r[1];
		BigInteger g_pi_su = pmac.generateGPiSu(x, r, prex.length());
		BigInteger pi_prex = pmac.generatePix(prex);
		System.out.println("The PMAC value is " + sigma);
		System.out.println(pmac.verify(sigma, pmac.generatePMACbyPrex(g_pi_su, pi_prex, "0".getBytes(), "1".getBytes(), "2".getBytes())));
		System.out.println(DataIO.toHexFromBytes("1".getBytes()));
		System.out.println(pmac.hashCode());
		pmac = new PMAC("./database/pmac.keys");
		System.out.println(pmac.hashCode());
	}

	@Override
	public void read(DataInputStream ds){
		// TODO Auto-generated method stub
		n = DataIO.readBigInteger(ds);
		g = DataIO.readBigInteger(ds);
		e = DataIO.readBigInteger(ds);
		phi_n = DataIO.readBigInteger(ds);
		d = DataIO.readBigInteger(ds);
		sk = DataIO.readBytes(ds);
		mappingTable = new BigInteger[MVALUE][CVALUE];
		for (int i = 0; i < MVALUE; i ++) {
			for (int j = 0; j < CVALUE; j ++) {
				mappingTable[i][j] = DataIO.readBigInteger(ds);
			}
		}
		try {
			bitLength = ds.readInt();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}

	@Override
	public void write(DataOutputStream ds) {
		// TODO Auto-generated method stub
		DataIO.writeBigInteger(ds, n);
		DataIO.writeBigInteger(ds, g);
		DataIO.writeBigInteger(ds, e);
		DataIO.writeBigInteger(ds, phi_n);
		DataIO.writeBigInteger(ds, d);
		DataIO.writeBytes(ds, sk);
		for (int i = 0; i < MVALUE; i ++) {
			for (int j = 0; j < CVALUE; j ++) {
				DataIO.writeBigInteger(ds, mappingTable[i][j]);
			}
		}
		try {
			ds.writeInt(bitLength);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}

	@Override
	public void loadBytes(byte[] data) {
		// TODO Auto-generated method stub
		DataInputStream ds = new DataInputStream(new ByteArrayInputStream(data));
		read(ds);
	}

	@Override
	public byte[] toBytes() {
		// TODO Auto-generated method stub
		ByteArrayOutputStream bs = new ByteArrayOutputStream();
		DataOutputStream ds = new DataOutputStream(bs);
		write(ds);
		return bs.toByteArray();
	}

	public int hashCode() {
		int hash = 1;
		hash = hash * 31 + n.hashCode();
		hash = hash * 31 + g.hashCode();
		hash = hash * 31 + e.hashCode();
		hash = hash * 31 + phi_n.hashCode();
		hash = hash * 31 + d.hashCode();
		hash = hash * 31 + new String(sk).hashCode();
		for (int i = 0; i < MVALUE; i ++) {
			for (int j = 0; j < CVALUE; j++) {
				hash = hash * 31 + mappingTable[i][j].hashCode();
			}
		}
		return hash;
	}
}
