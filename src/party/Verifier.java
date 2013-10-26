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

	Query query = null;
	
	public Verifier() {
		super();
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

	/**
	 * Verify the VO
	 * @param vo
	 * @return
	 */
	public boolean verifyVO(VO vo, Query query) {
		// TODO Auto-generated method stub
		this.query = query;
		return vo.verify(this, query);
	}

	/**
	 * Init key at verifier
	 * @param pmac
	 */
	public void initKey(PMAC pmac) {
		// TODO Auto-generated method stub
		this.p = pmac.p;
		this.g = pmac.g;
		this.r = pmac.r;
		this.phi_p = pmac.phi_p;
		this.mappingTable = pmac.mappingTable;
	}
	
	public String toString() {
		StringBuffer sb = new StringBuffer("");
		sb.append("q=" + query.toString());
		return sb.toString();
	}
}
