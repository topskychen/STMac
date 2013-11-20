/**
 * 
 */
package crypto;

import index.Query;
import index.Trajectory;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.math.BigInteger;

import timer.Timer;

/**
 * @author chenqian
 *
 */
public class VO {

	Timer timer 				= null;
	private double prepareTime	= -1; 
	private double verifyTime 	= -1;
	
	BigInteger sigma			= null;
	BigInteger 	g_pi_su			= null; 
	int t1, t2, t3, t4;
//	Trajectory 	trajectory		= null;
	
	public VO() {
		timer = new Timer();
	}
	
	/**
	 * Prepare the VO
	 */
	public void prepare(PMAC pmac, Trajectory tra, Query query, int lBound, int rBound) {
		timer.reset();
		int d = query.getRange().length();
		int[] bounds = tra.getTimeRange(lBound, rBound);
		g_pi_su = pmac.generateTraGPiSu(tra.getLocations(), tra.getRs(), bounds[0], bounds[1], d);
		sigma	= pmac.aggregatePMACs(tra, bounds[0], bounds[1]);
		t1 = tra.getTimeStamp(bounds[0] - 1);
		t2 = tra.getTimeStamp(bounds[0]);
		t3 = tra.getTimeStamp(bounds[1]);
		t4 = tra.getTimeStamp(bounds[1] + 1);
		timer.stop();
		prepareTime = timer.timeElapseinMs();
	}
	
	/**
	 * Verify the VO.
	 * @return
	 */
	public boolean verify(PMAC pmac, Query query) {
		timer.reset();
		boolean isVerify = false;
		int d = query.getRange().length();
		BigInteger pi_prex = pmac.generatePix(query.getRange());
		BigInteger verifierComponent = pmac.generateTraPMACbyPrex(g_pi_su, pi_prex, t1, t2, t3, t4);
		isVerify = pmac.verify(sigma, verifierComponent);
		timer.stop();
		verifyTime = timer.timeElapseinMs();
		return isVerify;
	}
	
	
	
	/**
	 * get VO in bytes
	 * @return
	 * @throws IOException 
	 */
	public byte[] getVOinBytes() {
		ByteArrayOutputStream bs = new ByteArrayOutputStream();
		DataOutputStream ds = new DataOutputStream(bs);
		try {
			ds.write(sigma.toByteArray());
			ds.write(g_pi_su.toByteArray());
			ds.writeInt(t1);
			ds.writeInt(t2);
			ds.writeInt(t3);
			ds.writeInt(t4);
			ds.flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return bs.toByteArray();
	}
	
	public String toString() {
		StringBuffer sb = new StringBuffer("");
		sb.append("PrepareTime: " + prepareTime + "ms\n");
		sb.append("VerifyTime: " + verifyTime + "ms\n");
		sb.append("Time Range: [" + t2 + ", " + t3 +"]");
		return sb.toString();
	}
	
	public double getVerifyTime() {
		if (verifyTime == -1) {
			throw new IllegalStateException("No verify function is called.");
		}
		return verifyTime;
	}
	
	/**
	 * Get preparation time.
	 * @return
	 */
	public double getPrepareTime() {
		if (prepareTime == -1) {
			throw new IllegalStateException("No prepare function is called.");
		}
		return prepareTime;
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
	}

}
