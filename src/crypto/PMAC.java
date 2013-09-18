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

	public BigInteger p, g, r;

	private final static int BITLENGTH = 32;
	private final static int CERTAINTY = 100;
	private final static int MVALUE = 32;
	private final static int CVALUE = 2;

	private final static int T_START = 1; // time stamp begins from 1, not 0
	private final static int T_END = 8;

	int[] t = new int[100]; // timestamp array

	/**
	 * Constructs an instance of the PMAC with BITLENGTH of modulus and at least
	 * 1-2^(-CERTAINTY) certainty of primes generation.
	 */

	public PMAC() {
		keyGeneration(BITLENGTH, CERTAINTY);
	}

	PrimeMatrix papp = new PrimeMatrix();
	// fix the mapping table on given MVALUE and CVALUE
	public final int[][] mappingTable = papp.genMatrix(MVALUE, CVALUE);

	/**
	 * @param x
	 *            string x which denotes the locaton info
	 * @return the TT(x) value of string x
	 */
	public BigInteger generatePix(String x) {

		BigInteger Pix = BigInteger.valueOf(1);

		// System.out.println("The Initial ¦°(x) value is " + Pix);

		for (int i = 0; i < x.length(); i++) {

			// the "decimal" ASCII code for char '0' is 48, '1' is 49
			int j = x.charAt(i) - 48;

			BigInteger pi = BigInteger.valueOf(mappingTable[i][j]);
			// System.out.print("pi is "+pi+"\t");

			// Calculate ¦°(x)=£k(x1)£k(x2)£k(x3)...£k(xm)
			Pix = Pix.multiply(pi);
			// System.out.println("pix is " + Pix);
		}

		// System.out.println("The Final ¦°(x) value is " + Pix);
		return Pix;
	}

	public BigInteger generateAggrePix(String[] x) {

		x = new String[T_END - T_START];
		BigInteger aggrePix = BigInteger.valueOf(1);

		for (int i = 0; i < T_END - T_START; i++) {

			aggrePix = aggrePix.multiply(generatePix(x[i]));
		}

		return aggrePix;
	}

	/**
	 * compute ¦°(su)
	 * 
	 * @param su
	 *            string which denotes the suffix
	 * @param d
	 *            length of predicate, the index where suffix begins
	 * @return the ¦°(su) value
	 */
	public BigInteger generatePiSu(String su, int d) {
		BigInteger PiSux = BigInteger.valueOf(1);
		// System.out.println("The Initial ¦°(x) value is " + PiSux);
		for (int i = 0; i < su.length(); i++) {
			// the "decimal" ASCII code for char '0' is 48, '1' is 49
			int j = su.charAt(i) - 48;
			BigInteger pi = BigInteger.valueOf(mappingTable[i + d][j]);
			// System.out.print("pi is "+pi+"\t");
			// Calculate ¦°(x)=£k(x1)£k(x2)£k(x3)...£k(xm)
			PiSux = PiSux.multiply(pi);
			// System.out.println("pix is " + Pix);
		}
		// System.out.println("The Final ¦°(su) value is " + PiSux);
		return PiSux;
	}


	public BigInteger aggreSecPiSux(String[] su, int start, int end, int d) {
		su = new String[end - start];
		BigInteger result = BigInteger.valueOf(1);
		for (int i = start; i < end - start; i++) {
			result = result.multiply(generatePiSu(su[i], d));
		}		
		result = g.modPow(result, p);
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

		/**
		 * store g, r, p in the array key for future use, any better assignment
		 * form?
		 */
		key[0] = g;
		key[1] = p;
		key[2] = r;

		System.out.println("p = " + p);
		System.out.println("r = " + r);

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
			return BigInteger.valueOf(1);
		} else {
			String ts = "" + t[i] + t[i - 1] + t[i + 1];
			MessageDigest md = null;
			try {
				md = MessageDigest.getInstance("MD5");
			} catch (NoSuchAlgorithmException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			try {
				md.update(ts.getBytes("UTF-8"));
			} catch (UnsupportedEncodingException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			BigInteger result = new BigInteger(1, md.digest());
			System.out.println("digest of concatenated timestamps are: "
					+ result);
			return result;
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
	public BigInteger singlepointPMAC(String x, int i) {
		return g.modPow((generatePix(x)).multiply(timeDegist(i)).multiply(r), p);
	}

	public BigInteger trajectoryPMAC(String x[], int start, int end) {
		BigInteger pmac[] = new BigInteger[end - start];
		BigInteger result;
		BigInteger tmp = BigInteger.valueOf(1);
		for (int i = start; i < end; i++) {
			pmac[i] = singlepointPMAC(x[i], i); // generate PMAC for every point
												// separately
			tmp = tmp.multiply(generatePix(x[i])).multiply(timeDegist(i));
		}
		result = g.modPow(tmp.multiply(r), p); // generated by authenticator
		return result;
	}

	/**
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
