/**
 * 
 */
package index;

import java.awt.Container;
import java.math.BigInteger;

import crypto.Constants;
import crypto.PMAC;

/**
 * @author chenqian
 *
 */
public class Trajectory{

	String[] locations 	= null;
	int[]	timeStamps 	= null; 
	BigInteger[] sigmas = null;
	BigInteger[] rs 	= null;
	
	public String getLocation() {
		return locations[0];
	}
	
	public Trajectory(String[] locations, int[] timeStamps) {
		super();
		this.locations = locations;
		this.timeStamps = timeStamps;
		if (timeStamps.length != locations.length + 1) {
			throw new IllegalStateException("The length of timeStamps should be 1 + length of locations.");
		}
		sigmas 	= new BigInteger[locations.length];
		rs 		= new BigInteger[locations.length]; 
	}

	public void setLocation(String x) {
		locations 	= new String[2];
		sigmas 		= new BigInteger[2];
		rs 			= new BigInteger[2];
		timeStamps 	= new int[3];
		locations[1] = x;
		timeStamps[0] = 0;
		timeStamps[1] = 1;
		timeStamps[2] = 2;
	}
	
	public String[] getLocations() {
		return locations;
	}

	public String getLocation(int p) {
		return locations[p];
	}
	
	public int getTimeStamp(int p) {
		return timeStamps[p]; 
	}
	
	public void setLocations(String[] locations) {
		this.locations = locations;
	}

	public int[] getTimeStamps() {
		return timeStamps;
	}

	public void setTimeStamps(int[] timeStamps) {
		this.timeStamps = timeStamps;
	}

	/**
	 * 
	 */
	public Trajectory() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * 
	 */
	public Trajectory(String x) {
		// TODO Auto-generated constructor stub
		setLocation(x);
		setTimeStamps(timeStamps);
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Trajectory trajectory = new Trajectory(new String[]{"", "0000", "0001", "0010", "0011"}, new int[] {0, 1, 2, 3, 4, 5});
		System.out.println(trajectory.toString());
	}

	public BigInteger getSigma(int p) {
		return sigmas[p];
	}

	public void setSigma(int p, BigInteger sigma) {
		sigmas[p] = sigma;
	}

	public BigInteger getR(int p) {
		return this.rs[p];
	}
	
	public BigInteger[] getRs() {
		return this.rs;
	}

	public void setR(int p, BigInteger r) {
		this.rs[p] = r;
	}

	public int length() {
		if (locations == null) return 0;
		return locations.length - 1;
	}
	
	public int[] getTimeRange(int lBound, int rBound) {
		int l = -1, r = -1;
		for (int i = 1; i <= length(); i ++) {
			if (l == -1 && getTimeStamp(i) > lBound) l = i;
			if (r == -1 && getTimeStamp(i) > rBound) {
				r = i - 1;
				break;
			}
		}
		if (l == -1) l = 1;
		if (r == -1) r = length();
		return new int[]{l, r};
	}
	
	public String toString() {
		StringBuffer sb = new StringBuffer("");
		sb.append("length = " + length() + " [");
		for (int i = 0; i < length(); i ++) {
			if (i != 0) {
				sb.append(",");
			}
			sb.append("(" + "\"" + locations[i + 1] + "\"");
			if (timeStamps != null) {
				sb.append(", " + timeStamps[i + 1]);
			}
			sb.append(")");
		}
		sb.append("]");
		return sb.toString();
	}

}
