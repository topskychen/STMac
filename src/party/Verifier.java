/**
 * 
 */
package party;

import index.Query;
import crypto.PMAC;
import crypto.VO;

/**
 * @author chenqian
 *
 */
public class Verifier extends PMAC{

	Query query_x = null, query_y = null;
	
	public Verifier() {
		super();
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

	public boolean verifyVO(VO vo_x, VO vo_y, Query query_x, Query query_y) {
		this.query_x = query_x;
		this.query_y = query_y;
		return vo_x.verify(this, query_x) && vo_y.verify(this, query_y);
	}
	
	/**
	 * Verify the VO
	 * @param vo
	 * @return
	 */
	public boolean verifyVO(VO vo, Query query) {
		// TODO Auto-generated method stub
		this.query_x = query;
		return vo.verify(this, query_x);
	}

	/**
	 * Init key at verifier
	 * @param pmac
	 */
	public void initKey(PMAC pmac) {
		// TODO Auto-generated method stub
		this.g = pmac.g;
		this.n = pmac.n;
		this.e = pmac.e;
//		this.d = pmac.d;
		this.sk = pmac.sk;
		this.phi_n = pmac.phi_n;
		this.mappingTable = pmac.mappingTable;
	}
	
	public String toString() {
		StringBuffer sb = new StringBuffer("verifier: \n");
		sb.append("q=" + query_x + ", " + query_y);
		sb.append(super.toString());
		return sb.toString();
	}
}
