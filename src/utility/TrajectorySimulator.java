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
	public TrajectorySimulator(Generator generator, Prover[] client,
			Verifier[] verifier) {
		super(generator, client, verifier);
		// TODO Auto-generated constructor stub
	}

	public void runCase (String line) {
		String[] tks = line.split(" ");
		/**
		 * Request VO from the client
		 */
		int lBound = Integer.parseInt(tks[2]), rBound = Integer.parseInt(tks[3]);
		Query query_x = new Query(tks[0], lBound, rBound);
		VO vo_x = prover[0].prepareVOwithIndex(query_x);
		Query query_y = new Query(tks[1], lBound, rBound);
		VO vo_y = prover[1].prepareVOwithIndex(query_y);
		
		/**
		 * Verify the VO.
		 */
		if (!verifier[0].verifyVO(vo_x, query_x) || !verifier[1].verifyVO(vo_y, query_y)) {
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
	
	/* (non-Javadoc)
	 * @see utility.Simulator#run()
	 */
	public void run(String[] fileNames, int index, Scanner in) throws IOException{
		// TODO Auto-generated method stub
		
		
//		Trajectory trajectory = new Trajectory(); trajectory.prepare(fileNames[0]);
		prover[0].prepareTraPMAC(fileNames[0] + "_x", fileNames[1] + "_x", generator);
		prover[0].prepareIndex(fileNames[2] + "_x", generator, index);
		prover[1].prepareTraPMAC(fileNames[0] + "_y", fileNames[1] + "_y", generator);
		prover[1].prepareIndex(fileNames[2] + "_y", generator, index);
	
//		System.out.println(generator.toString());
//		System.out.println(prover.toString());
//		System.out.println(verifier.toString());
		VO.precise = false;
		
		while(true) {
			printInfo(0); 
			String line = in.nextLine();
			if (line.equalsIgnoreCase("a")) {	
				System.out.println("Input prex prey l r:");
				runCase(in.nextLine());
			} else {
				System.out.println("Input file to query:");
				String fileName = in.nextLine();
				Scanner fin = new Scanner(new File(fileName)); int num = Integer.parseInt(fin.nextLine());
				for (int i = 0; i < num; i ++) {
					runCase(fin.nextLine());
				}
				fin.close();
			}
		}
	}

	public static void printInfo(int o) {
		if (o == 0) {
			System.out.println("(a) user input");
			System.out.println("(b) file input");
		} else if (o == 1){
			System.out.println("(a) build index\n(b) load index");
		}
	}
	
	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Generator generator = new Generator("./database/tra_test.keys");
		Prover proverX = new Prover();
		Prover proverY = new Prover();
		Verifier verifierX = new Verifier();
		Verifier verifierY = new Verifier();
		TrajectorySimulator trajectorySimulator = new TrajectorySimulator(generator, new Prover[]{proverX, proverY}, new Verifier[]{verifierX, verifierY});
		trajectorySimulator.init();
		Scanner in = new Scanner(System.in);
		while (true) {
//		String encoding = ".enc";
			System.out.println("input fileName:");
			String fileName = in.nextLine();
			System.out.println("input encoding (enc, or none):");
			String encoding = in.nextLine();
			System.out.println("input index (binary, or general):");
			String index = in.nextLine(); int treeIndex;
			if (encoding.equals("none")) encoding = "";
			if (encoding.endsWith("enc")) encoding = ".enc";
			if (index.equals("binary")) {
				treeIndex = SearchIndex.BinarySearchTree;
			} else {
				treeIndex = SearchIndex.GeneralSearchTree;
			}
			System.out.println("input threadNum:");
			Constants.ThreadNum = Integer.parseInt(in.nextLine());
			try {
				trajectorySimulator.run(new String[] {
						"./dataset/" + fileName + ".txt" + encoding, 
						"./database/" + fileName + ".pmacs" + encoding,
						"./database/" + fileName + ".tra" + encoding},
						treeIndex,
						in);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			System.out.println(trajectorySimulator.toString());
		}
	}

	@Override
	public void init() {
		// TODO Auto-generated method stub
		/**
		 * Initialize the keys
		 */
		for (int i = 0; i < prover.length; i ++) {
			generator.initClientKey(prover[i]);
			generator.initVerifierKey(verifier[i]);
		}
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
