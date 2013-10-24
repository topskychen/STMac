package index;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;
import java.math.BigInteger;

import timer.Timer;
import crypto.PMAC;


public class VO implements Serializable {

	private static final long serialVersionUID = 1L;

	private final static int T_START = 1; // time stamp begins from 1, not 0
	private final static int T_END = 8;
	int[] t; // timestamp array
	String[] x = null;
	
	
		
	/**
	 * The verify
	 * 
	 * @return
	 * @throws IOException
	 */
	public void singlepointVerify(String x, String q) throws IOException {
		System.out.println("================ Single PMAC ==============");
		
		int d = q.length();
		// prefix of x
		String pre = x.substring(0, d);
		System.out.println("prefix of x is: " + pre);
		
		if (!pre.equals(q)) {
			System.out.println("user location x not in predicate q");
			return;
		}
		// suffix of x
//			String su = x.substring(d);
//			System.out.println("suffix of x is: " + su);
		Timer timer = new Timer();
		// Get CPU time in nanoseconds (one billionth of a second)
		PMAC pmacapp = new PMAC();
		
		/**
		 * authenticator preparation
		 * */
		timer.reset();
		BigInteger singlepmacValue = pmacapp.generatePMAC(x, 0);
		timer.stop();
		System.out.println("Authenticator time:\t" + timer.timeElapseinMs() + "ms");
		
		/**
		 * client preparation
		 * */
		timer.reset();
		BigInteger g_pi_su = pmacapp.generateGPiSu(x, d, 0);
		timer.stop();
		System.out.println("Client time: \t\t" + timer.timeElapseinMs() + "ms");
		
		/**
		 * verifier verification
		 * */
		timer.reset();
		BigInteger pi_prex = pmacapp.generatePix(pre, 0);
		BigInteger verifierComponent = pmacapp.generatePMACbyPrex(g_pi_su, pi_prex);
		Boolean isVerify = singlepmacValue.equals(verifierComponent);
		timer.stop();
		System.out.println("Verifier time: \t\t" + timer.timeElapseinMs() + "ms");
//			System.out.println("verifierComponent is \t\t\t" + verifierComponent);
		
		// compare singlepmacValue and verifierComponent
		System.out.println("Comparation result is " + isVerify + "");
		System.out.println("==========================================");
	}

	public void trajectoryVerify(String q, int start, int end, String[] _x, int[] _t)
			throws IOException {
		System.out.println("================ Trajectory PMAC ==============");
		
		int d = q.length();
		if (start <= 0 || end >= _x.length || end >= _t.length - 1) {
			System.out.println("The start/end can not fit the array s or t");
			return;
		}
		x = new String[_x.length];
		for (int i = 0; i < _x.length; i ++){
			x[i] = _x[i];
		}
		
		for (int i = start; i <= end ; i++) {
			if (!x[i].substring(0, d).equals(q)) {
				System.out.println("user location " + x[i]
						+ " not in predicate q, verification failed");
				break;
			}
		}
		t = new int[_t.length];
		for(int i = 0; i < _t.length; i ++){
			t[i] = _t[i];
		}
		
		Timer timer = new Timer();
		// assume all points in string x locate in window q, then they share
		// common prefix
		String pre = x[start].substring(0, d);
		System.out.println("prefix of x is: " + pre);
		PMAC pmacapp = new PMAC(t);

		timer.reset();
		// authenticator: generate aggregated PMAC
		BigInteger aggregatedPMAC = pmacapp.generateTraPMAC(x, start, end);
		timer.stop();
		System.out.println("Authenticator time:\t" + timer.timeElapseinMs() + "ms");
		
		// client: building VO= pre(x) + ??????t[i] + g^??????(su)mod p
		timer.reset();
		BigInteger tra_suffixEncry = pmacapp.generateTraGPiSu(x, start, end, d);
		timer.stop();
		System.out.println("Client time: \t\t" + timer.timeElapseinMs() + "ms");
		
		// verifier: compute PMAC out of VO, and compare
		timer.reset();
		BigInteger pi_prex = pmacapp.generatePix(pre, 0);
		BigInteger verifierComponent = pmacapp.generateTraPMACbyPrex(tra_suffixEncry, pi_prex, start, end);
		Boolean isVerify = aggregatedPMAC.equals(verifierComponent);
		timer.stop();
		System.out.println("Verifier time: \t\t" + timer.timeElapseinMs() + "ms");
		
		// compare singlepmacValue and verifierComponent
		System.out.println("Comparation result is " + isVerify);
		System.out.println("==========================================");
	}

	/**
	 * The main function
	 * 
	 * @param args
	 * @throws IOException
	 */

	public static void main(String[] args) throws IOException {

		String x = "01001011100011100110001000010110";
		String[] xs0 = { 
//				"",
				"01001011100011100110001000010110",
				"01001011100011100110001000010110",
				"01001011100011100110001000010101",
				"01001011100011100110001000011110",
				"01001011100011100110001000010001",
				"01001011100011100110001000010110",
				"01001011100011100110001000010101",
				"01001011100011100110001000011110",
				"01001011100011100110001000010001",
				"01001011100011100110001000010110",
		};
		System.out.print("the predicate q is: ");
		String q = new BufferedReader(new InputStreamReader(System.in))
				.readLine();
		//010010111000111001100010000
		
		VO vo = new VO();
		vo.singlepointVerify(x, q);
		
		int times = 1000;
		int[] t = new int[times];
		String[] xs = new String[times];
		for (int i = 0; i < times; i ++) {
			t[i] = i;
			xs[i] = xs0[i % 10]; 
		}
		xs[0] = "";
		vo.trajectoryVerify(q, 1, 998, xs, t);
	}

}
