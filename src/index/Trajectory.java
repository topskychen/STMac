/**
 * 
 */
package index;

import io.IO;
import io.RW;

import java.awt.Container;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.util.Scanner;

import crypto.Constants;
import crypto.PMAC;

/**
 * @author chenqian
 *
 */
public class Trajectory implements RW{

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
	
	/**
	 * Given lBound and rBound, return the range in time stamps.
	 * @param lBound
	 * @param rBound
	 * @return
	 */
	public int[] getTimeRange(int lBound, int rBound) {
		int l = -1, r = -1;
		for (int i = 1; i <= length(); i ++) {
			if (getTimeStamp(i - 1) < lBound && getTimeStamp(i) >= lBound) l = i;
			if (getTimeStamp(i + 1) > rBound && getTimeStamp(i) <= rBound) {
				r = i;
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
	
	public boolean checkTrajectory() {
		for (int i = 1; i <= length(); i ++ ) {
			if (getSigma(i) == null) return false;
		}
		return true;
	}
	
	public BData getLeafData(int p, PMAC pmac) {
		return new BData(locations[p], timeStamps[p - 1], timeStamps[p], timeStamps[p + 1], sigmas[p], rs[p], pmac, false);
	}

	@Override
	public void read(DataInputStream ds) {
		// TODO Auto-generated method stub
		try {
			int len = ds.readInt();
			locations = new String[len];
			for (int i = 1; i <= length(); i ++) {
				locations[i] = IO.readString(ds);
			}
			timeStamps = new int[len + 1];
			for (int i = 1; i <= length(); i ++) {
				timeStamps[i] = ds.readInt();
			}
			timeStamps[0] = Integer.MIN_VALUE;
			timeStamps[len] = Integer.MAX_VALUE;
			sigmas = new BigInteger[len];
			for (int i = 1; i <= length(); i ++) {
				sigmas[i] = IO.readBigInteger(ds);
			} 
			rs = new BigInteger[len];
			for (int i = 1; i <= length(); i ++) {
				rs[i] = IO.readBigInteger(ds);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	@Override
	public void write(DataOutputStream ds) {
		// TODO Auto-generated method stub
		try {
			ds.writeInt(locations.length);
			for (int i = 1; i <= length(); i ++) {
				IO.writeString(ds, locations[i]);
			}
			for (int i = 1; i <= length(); i ++) {
				ds.writeInt(timeStamps[i]);
			}
			for (int i = 1; i <= length(); i ++) {
				IO.writeBigInteger(ds, sigmas[i]);
			}
			for (int i = 1; i <= length(); i ++) {
				IO.writeBigInteger(ds, rs[i]);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	
	/**
	 * Prepare location and timeStamps from file.
	 * @param fileName
	 */
	public void prepare(String fileName) {
		try {			
			Scanner in = new Scanner(new File(fileName));
			int len = Integer.parseInt(in.nextLine());
			locations = new String[len + 1];
			timeStamps = new int[len + 2];
			sigmas = new BigInteger[len + 1];
			rs = new BigInteger[len + 1];
			for (int i = 1; i <= length(); i ++) {
				String[] tks = in.nextLine().split("\t");
				locations[i] = tks[0];
				timeStamps[i] = Integer.parseInt(tks[1]);
			}
			timeStamps[0] = Integer.MIN_VALUE;
			timeStamps[len + 1] = Integer.MAX_VALUE;
			in.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
