/**
 * 
 */
package utility;

import index.Query;
import index.Trajectory;
import party.Authenticator;
import party.Client;
import party.Verifier;
import crypto.VO;

/**
 * 
 * This is for testing the single MAC
 * @author chenqian
 *
 */
public class SinglePointSimulator extends Simulator {

	/**
	 * Constructor
	 * @param authenticator
	 * @param client
	 * @param verifier
	 */
	public SinglePointSimulator(Authenticator authenticator, Client client, Verifier verifier) {
		// TODO Auto-generated constructor stub
		super(authenticator, client, verifier);
	}
	
	
	/**
	 * Run the simulation
	 * Run random test case
	 */
	public void run() {
//		Authenticator authenticator = new Authenticator();
//		Client client = new Client();
//		Verifier verifier = new Verifier();
		Trajectory trajectory = new Trajectory("01001011100011100110001000010110");
		Query query = new Query("0");
		run(trajectory, query);
	}

	
	@Override
	public void run(Trajectory trajectory, Query query) {
		// TODO Auto-generated method stub
		/**
		 * Client requests the PMAC for a point.
		 */
//		String x = "0011";
		client.requestPMAC(trajectory, authenticator);
		
		/**
		 * Request VO from the client
		 */
//		Query query = new Query("00");
		VO vo = client.prepareVO(query);
		
		/**
		 * Verify the VO.
		 */
		if (!verifier.verifyVO(vo, query)) {
			System.err.println("It does not pass the verification");
		} else {
			System.out.println("Pass!");
		}
		
		preparationTime 	= vo.getPrepareTime();
		verificationTime 	= vo.getVerifyTime();
		voSize 				= vo.getVOinBytes().length;
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Authenticator authenticator = new Authenticator();
		Client client = new Client();
		Verifier verifier = new Verifier();
		SinglePointSimulator singlePointSimulator = new SinglePointSimulator(authenticator, client, verifier);
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
		authenticator.initClientKey(client);
		authenticator.initVerifierKey(verifier);
	}
}
