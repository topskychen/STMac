/**
 * 
 */
package utility;

import java.io.File;
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
		prover.prepareTraPMAC(fileNames[0] + "_x", fileNames[1] + "_x", generator);
		prover.prepareIndex(fileNames[2] + "_x", generator, SearchIndex.GeneralSearchTree);
		prover.prepareTraPMAC(fileNames[0] + "_y", fileNames[1] + "_y", generator);
		prover.prepareIndex(fileNames[2] + "_y", generator, SearchIndex.GeneralSearchTree);
	
//		System.out.println(generator.toString());
//		System.out.println(prover.toString());
//		System.out.println(verifier.toString());
		VO.precise = false;
		
		Scanner in = new Scanner(System.in);
		while (in.hasNext()) {			
			
			String[] pre = in.nextLine().split(" ");
			String[] tks = in.nextLine().split(" ");
			/**
			 * Request VO from the client
			 */
			int lBound = Integer.parseInt(tks[0]), rBound = Integer.parseInt(tks[1]);
			Query query_x = new Query(pre[0], lBound, rBound);
			VO vo_x = prover.prepareVOwithIndex(query_x);
			Query query_y = new Query(pre[1], lBound, rBound);
			VO vo_y = prover.prepareVOwithIndex(query_y);
			
			/**
			 * Verify the VO.
			 */
			if (!verifier.verifyVO(vo_x, vo_y, query_x, query_y)) {
				System.err.println("It does not pass the verification");
			} else {
				System.out.println("Pass!");
			}
			System.out.println(vo_x.toString());
			System.out.println(vo_y.toString());
			
			preparationTime 	= vo_x.getPrepareTime() + vo_y.getPrepareTime();
			verificationTime 	= vo_x.getVerifyTime() + vo_y.getVerifyTime();
			voSize 				= vo_x.getVOSize() + vo_y.getVOSize();
			
			System.out.println(toString());
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
