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
 * 
 * This is for testing the single MAC
 * @author chenqian
 *
 */
public class SimplePointSimulator extends Simulator {

	/**
	 * Constructor
	 * @param authenticator
	 * @param prover
	 * @param verifier
	 */
	public SimplePointSimulator(Generator authenticator, Prover prover, Verifier verifier) {
		// TODO Auto-generated constructor stub
		super(authenticator, prover, verifier);
	}
	
	
	/**
	 * Run the simulation
	 * Run random test case
	 */
	public void run() {
//		Authenticator authenticator = new Authenticator();
//		Client client = new Client();
//		Verifier verifier = new Verifier();
		Trajectory trajectory = new Trajectory(new String[]{"", "0000", "0001", "0010", "0011"}, new int[] {0, 1, 5, 10, 14, 50});
//		System.out.println(trajectory.toString());
		Query query = new Query("0", 1, 15);
		run(trajectory, query);
	}

	
	@Override
	public void run(Trajectory trajectory, Query query) {
		// TODO Auto-generated method stub
		/**
		 * Client requests the PMAC for a point.
		 */
//		String x = "0011";
		prover.requestPMAC(trajectory, generator, 1, trajectory.length());
		
		/**
		 * Request VO from the client
		 */
//		Query query = new Query("00");
		VO vo = prover.prepareVOwithTrajectory(query);
		
		/**
		 * Verify the VO.
		 */
		if (!verifier.verifyVO(vo, query)) {
			System.err.println("It does not pass the verification");
		} else {
			System.out.println("Pass!");
		}
		System.out.println(vo.toString());
		
		preparationTime 	= vo.getPrepareTime();
		verificationTime 	= vo.getVerifyTime();
		voSize 				= vo.toBytes().length;
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Generator authenticator = new Generator();
		Prover prover = new Prover();
		Verifier verifier = new Verifier();
		SimplePointSimulator singlePointSimulator = new SimplePointSimulator(authenticator, prover, verifier);
		singlePointSimulator.init();
		singlePointSimulator.run();
		System.out.println(singlePointSimulator.toString());
	}


	@Override
	public void init() {
		// TODO Auto-generated method stub
		/**
		 * Initialize the keys
		 */
		generator.initClientKey(prover);
		generator.initVerifierKey(verifier);
	}
}
