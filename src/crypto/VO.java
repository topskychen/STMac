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
	
	BigInteger 	g_pi_su			= null; 
	BigInteger 	singlePMAC 		= null;
	Trajectory 	trajectory		= null;
	
	public VO(BigInteger singlePMAC, Trajectory trajectory) {
		timer = new Timer();
		this.singlePMAC = singlePMAC;
		this.trajectory = trajectory;
	}
	
	/**
	 * Prepare the VO
	 */
	public void prepare(PMAC pmac, Query query) {
		timer.reset();
		if (query.getQueryType() == Query.POINT_QUERY) {
			int d = query.getRange().length();
			g_pi_su = pmac.generateGPiSu(trajectory.getLocation(), d);
		} else {
			throw new IllegalStateException("The query type is unknown.");
		}
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
		if (query.getQueryType() == Query.POINT_QUERY) {
			int d = query.getRange().length();
			String pre = trajectory.getLocation().substring(0, d);
			if (!pre.equals(query.getRange())) 
				throw new IllegalStateException("The query is not the prefix of location, pre : " + pre + ", query : " + query.getRange());
			BigInteger pi_prex = pmac.generatePix(pre);
			BigInteger verifierComponent = pmac.generatePMACbyPrex(g_pi_su, pi_prex, -1, 0, 1);
			isVerify = singlePMAC.equals(verifierComponent);
			isVerify = pmac.verify(singlePMAC, verifierComponent);
		} else {
			throw new IllegalStateException("The query type is unknown.");
		}
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
			ds.write(singlePMAC.toByteArray());
			ds.write(g_pi_su.toByteArray());
			ds.flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return bs.toByteArray();
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
