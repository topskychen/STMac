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
	public void requestPMAC(Trajectory trajectory, Generator generator, int start, int end) {
		// TODO Auto-generated method stub
		this.trajectory = trajectory;
//		generator.generatePMAC(this.trajectory.getLocation(), -1, 0, 1);
		generator.generatePMAC(trajectory, start, end);
	}
	
	/**
	 * init the keys at the client
	 * @param pmac2
	 */
	public void initKey(PMAC pmac) {
		// TODO Auto-generated method stub
		this.g = pmac.g;
		this.n = pmac.n;
		this.e = pmac.e;
		this.mappingTable = pmac.mappingTable;
	}


	/**
	 * Prepare VO
	 * @param q
	 * @param client
	 * @return
	 */
	public VO prepareVO(Query query, int start, int end) {
		// TODO Auto-generated method stub
		VO vo = new VO();
		vo.prepare(this, trajectory, query, start, end);
		return vo;
	}
}
