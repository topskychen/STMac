package crypto;

import java.math.BigInteger;
import java.util.Collections;
import java.util.ArrayList;

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
	int[][] genMatrix(int m, int c) {

		BigInteger p0 = new BigInteger("1");

		// define ArrayList to hold Integer objects
		ArrayList<Integer> numbers = new ArrayList<Integer>();

		for (int i = 0; i < PRIME_NUM; i++) {
			numbers.add(p0.nextProbablePrime().intValue());
			p0 = BigInteger.valueOf(numbers.get(i));
		}

		Collections.shuffle(numbers);

		Integer[] p = new Integer[PRIME_NUM];
		p = numbers.toArray(p);

		for (Integer number : p) {
			System.out.print(number + " ");
		}

		System.out.println();

		System.out.println("********print the mapping table now:********");

		int[][] primeMatrix = new int[MVALUE][CVALUE];

		for (int i = 0; i < MVALUE; i++) {

			for (int j = 0; j < CVALUE; j++) {
				primeMatrix[i][j] = p[CVALUE * i + j];
				System.out.print(primeMatrix[i][j] + "\t");
			}

			System.out.println();
		}
		return primeMatrix;

	}

	public static void main(String[] args) {

		PrimeMatrix p = new PrimeMatrix();
		p.genMatrix(MVALUE, CVALUE);

	}
}
