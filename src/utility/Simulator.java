/**
 * 
 */
package utility;

import index.Query;
import index.Trajectory;
import party.Generator;
import party.Prover;
import party.Verifier;
import crypto.VO;

/**
 * @author chenqian
 *
 */
public abstract class Simulator {

	Generator 	authenticator			= null;
	Prover 			client					= null;
	Verifier 		verifier				= null;
	double 			preparationTime 		= -1;
	double 			verificationTime 		= -1;
	long 			voSize					= -1;
	
	public Simulator(Generator authenticator, Prover client, Verifier verifier) {
		this.authenticator = authenticator;
		this.client = client;
		this.verifier = verifier;
	}
	
	
	/**
	 * For initializing the keys and indexes.
	 */
	public abstract void init();
	
	/**
	 * Run multi times
	 */
	public abstract void run();
	
	public abstract void run(Trajectory trajectory, Query query);
	
	/**
	 * Get the total time of simulation
	 * @return
	 */
	public double getTotalSimulationTime() {
		return getPreparationTime() + getVerificationTime();
	}
	
	/**
	 * Get time of preparation
	 * @return
	 */
	public double getPreparationTime() {
		if (preparationTime < 0) {
			throw new IllegalStateException("The preparationTime is not set, maybe u need to call run function first");
		}
		return preparationTime;
	}
	
	/**
	 * Get time of verification
	 * @return
	 */
	public double getVerificationTime() {
		if (verificationTime < 0) {
			throw new IllegalStateException("The verificationTime time is not set, maybe u need to call run function first");
		}
		return verificationTime;
	}
	
	/**
	 * Get size of VO
	 * @return
	 */
	public long getVOsize() {
		if (voSize < 0) {
			throw new IllegalStateException("The voSize is not set, maybe u need to call run function first");
		}
		return voSize;
	}
	
	/**
	 * Get the infomation.
	 */
	public String toString() {
		StringBuffer sb = new StringBuffer("");
		sb.append("Client\t\t\t\t: \t" + client.toString() + "\n");
		sb.append("Verifier\t\t\t: \t" + verifier.toString() + "\n");
		sb.append("VO preparation time is \t\t: \t" + getPreparationTime() + " ms\n");
		sb.append("VO verification time is \t: \t" + getVerificationTime() + " ms\n");
		sb.append("VO size is \t\t\t: \t" + getVOsize() + " B\n");
		return sb.toString();
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
