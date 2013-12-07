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

	Generator 		generator				= null;
	Prover[] 			prover					= null;
	Verifier[] 		verifier				= null;
	double 			preparationTime 		= -1;
	double 			verificationTime 		= -1;
	double			searchTime				= -1;
	long 			voSize					= -1;
	
	public Simulator(Generator authenticator, Prover[] prover, Verifier[] verifier) {
		this.generator = authenticator;
		this.prover = prover;
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
	 * Get time of verification
	 * @return
	 */
	public double getSearchTime() {
		if (searchTime < 0) {
			throw new IllegalStateException("The verificationTime time is not set, maybe u need to call run function first");
		}
		return searchTime;
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
//		sb.append("Prover\t\t\t\t: \t" + prover.toString() + "\n");
//		sb.append("Verifier\t\t\t: \t" + verifier.toString() + "\n");
		sb.append("Search time : " + getSearchTime() + " ms\n");
		sb.append("Prepare time : " + getPreparationTime() + " ms\n");
		sb.append("Verify time : " + getVerificationTime() + " ms\n");
		sb.append("VO size : " + getVOsize() + " B, " + getVOsize() / 1000.0 + " KB\n");
		return sb.toString();
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
