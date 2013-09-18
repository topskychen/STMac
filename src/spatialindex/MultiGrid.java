package spatialindex;

import java.lang.Math;

public class MultiGrid {

	int q_begin; // digits in range [0, 2^m], inclusive.
					// for example: when m =3, in range [0,8] 
	int q_end; // like the above

	int leftShift;
	int rightShift;

	private final int C = 2; // only considers binary
	int m;

	private Boolean is2Power(int x) {
		return (((x - 1) & x) == 0) && (x != 0);
	}

	private double binaryLog(int x) {
		if (x < 0)
			throw new IllegalArgumentException();
		return (Math.log(x) / Math.log(C));
	}

	public void verify(int m, int q_begin, int q_end) {

		if (q_end > (int) Math.pow(2, m) || (q_begin < 0)) { // when q exceeds the range [0, 2^m]
			System.out.println("Predicate returns FALSE...");
		} else if (is2Power(q_end - q_begin)
				&& (q_begin % (q_end - q_begin) == 0)
				&& (q_end % (q_end - q_begin) == 0)) {
			leftShift = 0;
			rightShift = 0;
			System.out
					.println("Predicate returns TRUE, q can be verified in current grid!");
		} else {
			
			// TODO: wrong with the following method calculating rightShift value
			
			rightShift = (int) (q_begin - (int) Math.pow(2,
					Math.floor(binaryLog(q_begin))));
			leftShift = (int) ((int) Math.pow(2, Math.ceil(binaryLog(q_end))) - q_end);
			System.out
					.println("Predicate returns TRUE under multigrid system, with conditions: left shift by "
							+ leftShift + " and right shift by " + rightShift);
		}

	}

	public static void main(String[] args) {
		MultiGrid mgapp = new MultiGrid();

		// test case 0:
		int m0 = 2;
		int q_begin0 = 0;
		int q_end0 = 5;
		System.out.print("Case 0: ");
		mgapp.verify(m0, q_begin0, q_end0);

		// test case 1:
		int m1 = 3;
		int q_begin1 = 0;
		int q_end1 = 4;
		System.out.print("Case 1: ");
		mgapp.verify(m1, q_begin1, q_end1);

		// test case 2:
		int m2 = 3;
		int q_begin2 = 0;
		int q_end2 = 3;
		System.out.print("Case 2: ");
		mgapp.verify(m2, q_begin2, q_end2);

		// test case 3:
		int m3 = 3;
		int q_begin3 = 2;
		int q_end3 = 6;
		System.out.print("Case 3: ");
		mgapp.verify(m3, q_begin3, q_end3);

		// test case 4:
		int m4 = 4;
		int q_begin4 = 5;
		int q_end4 = 11;
		System.out.print("Case 4: ");
		mgapp.verify(m4, q_begin4, q_end4);

	}

}
