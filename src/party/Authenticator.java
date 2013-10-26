/**
 * 
 */
package party;

import crypto.PMAC;

/**
 * @author chenqian
 *
 */
public class Authenticator extends PMAC{

	
	public Authenticator() {
		super();
		this.initKey();
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
	public void initClientKey(Client client) {
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


}
