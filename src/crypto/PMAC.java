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

	public BigInteger p, g, r, phi_p;

	private final static int BITLENGTH = 1024;
	private final static int CERTAINTY = 100;
	private final static int MVALUE = 32;
	private final static int CVALUE = 2;

	private final static int T_START = 1; // time stamp begins from 1, not 0
	private final static int T_END = 8;

	int[] t = new int[100]; // timestamp array

	public BigInteger[][] mappingTable = null;
	
	/**
	 * Constructs an instance of the PMAC with BITLENGTH of modulus and at least
	 * 1-2^(-CERTAINTY) certainty of primes generation.
	 */

	public PMAC() {
		keyGeneration(BITLENGTH, CERTAINTY);
		PrimeMatrix papp = new PrimeMatrix();
		mappingTable = papp.genMatrix(MVALUE, CVALUE);
	}

	/**
	 * @param x
	 *            string x which denotes the locaton info
	 * @return the TT(x) value of string x
	 */
	public BigInteger generatePix(String x) {
		BigInteger Pix = BigInteger.ONE;
		for (int i = 0; i < x.length(); i++) {
			Pix = Pix.multiply(mappingTable[i][x.charAt(i) - '0']).mod(phi_p);
		}
		return Pix;
	}
	
	/**
	 * @param x
	 *            string x which denotes the locaton info
	 * @return the TT(x) value of string x
	 */
	public BigInteger generatePixPhit(String x, int i_t) {
		BigInteger Pix = BigInteger.ONE;
		for (int i = 0; i < x.length(); i++) {
			Pix = Pix.multiply(mappingTable[i][x.charAt(i) - '0']).mod(phi_p);
		}
		Pix = Pix.multiply(timeDegist(i_t)).mod(phi_p);
		return Pix;
	}
	

	public BigInteger generateAggrePix(String[] x) {
		x = new String[T_END - T_START];
		BigInteger aggrePix = BigInteger.valueOf(1);
		for (int i = 0; i < T_END - T_START; i++) {
			aggrePix = aggrePix.multiply(generatePix(x[i])).mod(phi_p);
		}
		return aggrePix;
	}

	/**
	 * compute pi(su) 
	 * 
	 * @param su
	 *            string which denotes the suffix
	 * @param d
	 *            length of predicate, the index where suffix begins
	 * @return the pi(su)
	 */
	public BigInteger generatePiSu(String su, int d) {
		BigInteger PiSux = BigInteger.ONE;
		for (int i = 0; i < su.length(); i++) {
			PiSux = PiSux.multiply(mappingTable[i + d][su.charAt(i) - '0']).mod(phi_p);
		}
		return PiSux;
	}

	/**
	 * compute g^pi(su) 
	 * 
	 * @param su
	 *            string which denotes the suffix
	 * @param d
	 *            length of predicate, the index where suffix begins
	 * @return the g^pi(su)
	 */
	public BigInteger generateGPiSu(String su, int d){
		return g.modPow(generatePiSu(su, d).multiply(r).mod(phi_p), p);
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
	public BigInteger generatePMACPrex(BigInteger g_pi_su, BigInteger pi_prex){
		return g_pi_su.modPow(pi_prex, p);
	}
	
	public BigInteger aggreSecPiSux(String[] su, int start, int end, int d) {
		su = new String[end - start];
		BigInteger result = BigInteger.valueOf(1);
		for (int i = start; i < end - start; i++) {
			result = result.multiply(generatePiSu(su[i], d));
		}		
		result = g.modPow(result.multiply(r).mod(phi_p), p);
		return result;		
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

//		System.out.println("p = " + p);
//		System.out.println("r = " + r);

		return key;

	}

	public void printKey() {

		System.out.println("g is " + g);
		System.out.println("p is " + p);
		System.out.println("r is " + r);
	}

	

	/**
	 * compute hash of time stamps t[i], based on t[i] + t[i-1] + t[i+1]
	 * 
	 * @param i
	 *            index
	 */
	public BigInteger timeDegist(int i) {
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
	 * generate PMAC for single point (x[i],t[i]) in trajectory TX
	 * 
	 * @param x
	 *            client's location plain text
	 * @param i
	 *            timestamp index for point
	 * @return PMAC value of input point
	 */
	public BigInteger singlepointPMAC(String x, int i_t) {
		return g.modPow(generatePixPhit(x, i_t).multiply(r).mod(phi_p), p);
	}
	
	
	public BigInteger trajectoryPMAC(String x[], int start, int end) {
//		BigInteger pmac[] = new BigInteger[end - start];
		BigInteger result;
		BigInteger tmp = BigInteger.valueOf(1);
		for (int i = start; i < end; i++) {
//			pmac[i] = singlepointPMAC(x[i], i); // generate PMAC for every point
			tmp = tmp.multiply(generatePixPhit(x[i], i));
		}
		result = g.modPow(tmp, p); // generated by authenticator
		return result;
	}

	/**
	 * 
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		PMAC pmac = new PMAC();
		String x = "01110011100011100110001000010110";
		System.out.println("Encrypted message is: " + x);
		BigInteger pmacval = pmac.singlepointPMAC(x, 0);
		System.out.println("The PMAC value is " + pmacval);	

	}

}
