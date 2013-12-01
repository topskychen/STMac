/**
 * 
 */
package party;

import crypto.PMAC;

/**
 * @author chenqian
 *
 */
public class Generator extends PMAC{

	
	public Generator() {
		super();
		this.initKey();
	}
	
	public Generator(String fileName) {
		super(fileName);
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

	/**
	 * Initialize the keys at client
	 * @param client
	 */
	public void initClientKey(Prover client) {
		// TODO Auto-generated method stub
		client.initKey(this);
	}

	/**
	 * Initialize the keys at verifier
	 * @param verifier
	 */
	public void initVerifierKey(Verifier verifier) {
		// TODO Auto-generated method stub
		verifier.initKey(this);
	}

	
	public String toString() {
		StringBuffer sb = new StringBuffer("Generator: \n");
		sb.append(super.toString());
		return sb.toString();
	}

}
