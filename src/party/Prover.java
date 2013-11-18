/**
 * 
 */
package party;

import index.Query;
import index.Trajectory;

import java.math.BigInteger;

import crypto.PMAC;
import crypto.VO;

/**
 * @author chenqian
 *
 */
public class Prover extends PMAC{

	BigInteger 		singlePMAC 	= null;
	Trajectory 		trajectory	= null;
	public Prover() {
		super();
	}
	
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}
	
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("trajectory " + trajectory.toString());
		return sb.toString();
	}
	
	/**
	 * @param x
	 * @param authenticator
	 */
	public void requestPMAC(Trajectory trajectory, Authenticator authenticator) {
		// TODO Auto-generated method stub
		this.trajectory = trajectory;
		singlePMAC = authenticator.generatePMAC(this.trajectory.getLocation(), -1, 0, 1);
	}

	/**
	 * init the keys at the client
	 * @param pmac2
	 */
	public void initKey(PMAC pmac) {
		// TODO Auto-generated method stub
		this.n = pmac.n;
		this.e = pmac.e;
		this.g = pmac.g;
		this.phi_n = pmac.phi_n;
		this.mappingTable = pmac.mappingTable;
	}


	/**
	 * Prepare VO
	 * @param q
	 * @param client
	 * @return
	 */
	public VO prepareVO(Query query) {
		// TODO Auto-generated method stub
		VO vo = new VO(singlePMAC, trajectory);
		vo.prepare(this, query);
		return vo;
	}
}
