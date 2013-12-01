/**
 * 
 */
package utility;

import java.io.IOException;
import java.util.Scanner;

import crypto.VO;
import index.Query;
import index.SearchIndex;
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
	public TrajectorySimulator(Generator generator, Prover client,
			Verifier verifier) {
		super(generator, client, verifier);
		// TODO Auto-generated constructor stub
	}

	
	
	/* (non-Javadoc)
	 * @see utility.Simulator#run()
	 */
	public void run(String[] fileNames) throws IOException{
		// TODO Auto-generated method stub
		
//		Trajectory trajectory = new Trajectory(); trajectory.prepare(fileNames[0]);
		prover.prepareTraPMAC(fileNames[0], fileNames[1], generator);
		prover.prepareIndex(fileNames[2], generator, SearchIndex.BinarySearchTree);
	
//		System.out.println(generator.toString());
//		System.out.println(prover.toString());
//		System.out.println(verifier.toString());
		VO.precise = false;
		
		Scanner in = new Scanner(System.in);
		while (in.hasNext()) {			
			
			String[] tks = in.nextLine().split(" ");
			/**
			 * Request VO from the client
			 */
			int lBound = Integer.parseInt(tks[0]), rBound = Integer.parseInt(tks[1]);
			Query query = new Query("01", lBound, rBound);
			VO vo = prover.prepareVOwithIndex(query);
			
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
			voSize 				= vo.getVOSize();
		}
		in.close();
	}

	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Generator generator = new Generator("./database/tra_test.keys");
		Prover prover = new Prover();
		Verifier verifier = new Verifier();
		TrajectorySimulator trajectorySimulator = new TrajectorySimulator(generator, prover, verifier);
		trajectorySimulator.init();
		try {
			trajectorySimulator.run(new String[] {
					"./dataset/tra_test.txt", 
					"./database/tra_test.pmacs",
					"./database/tra_test.tra"});
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println(trajectorySimulator.toString());
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

	@Override
	public void run(Trajectory trajectory, Query query) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		
	}
	
	

}
