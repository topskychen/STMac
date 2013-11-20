/**
 * 
 */
package utility;

import index.Query;
import index.Trajectory;
import party.Generator;
import party.Prover;
import party.Verifier;

/**
 * @author chenqian
 *
 */
public class TrajectorySimulator extends Simulator {

	/**
	 * @param authenticator
	 * @param client
	 * @param verifier
	 */
	public TrajectorySimulator(Generator authenticator, Prover client,
			Verifier verifier) {
		super(authenticator, client, verifier);
		// TODO Auto-generated constructor stub
	}

	/* (non-Javadoc)
	 * @see utility.Simulator#run()
	 */
	@Override
	public void run() {
		// TODO Auto-generated method stub
		/**
		 * Initialize the keys
		 */
		authenticator.initClientKey(client);
		authenticator.initVerifierKey(verifier);
		
//		client
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

	@Override
	public void init() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void run(Trajectory trajectory, Query query) {
		// TODO Auto-generated method stub
		
	}
	
	

}
