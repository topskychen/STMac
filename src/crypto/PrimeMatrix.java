package crypto;

import java.math.BigInteger;
import java.util.Collections;
import java.util.ArrayList;
import java.util.Random;

/**
 * generates a m*c prime matrix
 * 
 */
public class PrimeMatrix {

	private final static int PRIME_NUM = 168; // generate PRIME_NUM prime
												// numbers

	private final static int MVALUE = 32;
	private final static int CVALUE = 2;

	public PrimeMatrix() {

	}

	/**
	 * @param m
	 *            the row of mapping table
	 * @param c
	 *            the column of mapping table
	 * @return a prime matrix with m*c numbers
	 */
	BigInteger[][] genMatrix(int m, int c) {
		
		BigInteger[][] primeMatrix = new BigInteger[MVALUE][CVALUE];
		for (int i = 0; i < MVALUE; i++) {
			for (int j = 0; j < CVALUE; j++) {
//				primeMatrix[i][j] = BigInteger.probablePrime(32, new Random());
				primeMatrix[i][j] = Constants.PRIME_Q.multiply(Constants.PRIME_Q).multiply(Constants.PRIME_Q).multiply(Constants.PRIME_Q).multiply(Constants.PRIME_Q);
//				System.out.print(primeMatrix[i][j] + "\t");
			}
//			System.out.println();
		}
		return primeMatrix;
	}
	

	public static void main(String[] args) {
		PrimeMatrix p = new PrimeMatrix();
	}
}
