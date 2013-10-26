package crypto;

import java.math.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.io.*;

/**
 * This program calculates the PMAC value of given string x.
 * 
  */
public class PMAC {

	public BigInteger p = null, g = null, r = null, phi_p = null;

	private final static int BITLENGTH = 1024;
	private final static int CERTAINTY = 100;
	private final static int MVALUE = 32;
	private final static int CVALUE = 2;

	private final static int T_START = 1; // time stamp begins from 1, not 0
	private final static int T_END = 8;

	int[] t; // timestamp array

	public BigInteger[][] mappingTable = null;
	
	/**
	 * Constructs an instance of the PMAC with BITLENGTH of modulus and at least
	 * 1-2^(-CERTAINTY) certainty of primes generation.
	 */

	public PMAC() {
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
	public BigInteger generatePix(String x, int i_t) {
		checkInitKeys();
		BigInteger Pix = BigInteger.ONE;
		for (int i = 0; i < x.length(); i++) {
			Pix = Pix.multiply(mappingTable[i][x.charAt(i) - '0']).mod(phi_p);
		}
		Pix = Pix.multiply(timeDigest(i_t)).mod(phi_p);
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
	 * 
	 * @param su
	 *            string which denotes the suffix
	 * @param d
	 *            length of predicate, the index where suffix begins
	 * @return the pi(su)
	 */
	public BigInteger generatePiSu(String su, int d, int i_t) {
		checkInitKeys();
		BigInteger PiSux = BigInteger.ONE;
		for (int i = d; i < su.length(); i++) {
			PiSux = PiSux.multiply(mappingTable[i][su.charAt(i) - '0']).mod(phi_p);
		}
		PiSux = PiSux.multiply(timeDigest(i_t)).mod(phi_p);
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
	public BigInteger generateGPiSu(String su, int d, int i_t){
		return g.modPow(generatePiSu(su, d, i_t), p);
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
	public BigInteger generateTraPiSu(String[] su, int start, int end, int d) {
		checkInitKeys();
		BigInteger result = BigInteger.ONE;
		for (int i = start; i <= end; i++) {
			result = result.multiply(generatePiSu(su[i], d, t[i])).mod(phi_p);
		}		
		return result;		
	}
	
	public BigInteger generateTraGPiSu(String[] su, int start, int end, int d) {
		checkInitKeys();
		return g.modPow(generateTraPiSu(su, start, end, d), p);
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
			Hasher.hashString(ts);
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
	public BigInteger generatePMACbyPrex(BigInteger g_pi_su, BigInteger pi_prex){
		return g_pi_su.modPow(pi_prex.multiply(r).mod(phi_p), p);
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
	
	public BigInteger generatePMAC(String x, int i_t) {
		return g.modPow(generatePix(x, i_t).multiply(r).mod(phi_p), p);
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
	public BigInteger generateTraPMACbyPrex(BigInteger g_pi_su, BigInteger pi_prex, int start, int end){
		BigInteger pow = new BigInteger(new Integer(end - start + 1).toString());
		pow = pi_prex.modPow(pow, phi_p);
		return g_pi_su.modPow(pow.multiply(r).mod(phi_p), p);
	}
	
	public BigInteger generateTraPMAC(String x[], int start, int end) {
//		BigInteger pmac[] = new BigInteger[end - start];
		BigInteger tmp = BigInteger.ONE;
		for (int i = start; i <= end; i++) {
			tmp = tmp.multiply(generatePix(x[i], i)).mod(phi_p);
		}
		return g.modPow(tmp.multiply(r).mod(phi_p), p); // generated by authenticator
	}
	
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

		BigInteger key[] = new BigInteger[3];

		// ToDo: how to test g
		g = new BigInteger("2");

		/**
		 * Constructs two randomly generated positive BigIntegers that are
		 * probably prime, with the specified bitLength and certainty.
		 */
		p = new BigInteger(bitLength, certainty, new Random());
		r = new BigInteger(bitLength, certainty, new Random());
		phi_p = p.subtract(BigInteger.ONE);
		
		/**
		 * store g, r, p in the array key for future use, any better assignment
		 * form?
		 */
		key[0] = g;
		key[1] = p;
		key[2] = r;


		return key;

	}

	public String toString() {
		StringBuffer sb = new StringBuffer("");
		sb.append("public g is " + g + "\n");
		sb.append("public p is " + p + "\n");
		sb.append("private r is " + r + "\n");
		return sb.toString();
	}
	
	/**
	 * 
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		PMAC pmac = new PMAC(); pmac.initKey();
		String x = "01110011100011100110001000010110";
		System.out.println("Encrypted message is: " + x);
		BigInteger pmacval = pmac.generatePMAC(x, 0);
		System.out.println("The PMAC value is " + pmacval);	

	}

}
