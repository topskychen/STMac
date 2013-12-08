/**
 * 
 */
package utility;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Scanner;

import crypto.Hasher;
import crypto.PMAC;
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

	public void runCase (String line, int queryType) {
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
			if (Constants.InputQuery == queryType) { 
				System.out.println("Pass!");
			}
		}
		if (Constants.InputQuery == queryType) {
			System.out.println(vo_x.toString());
			System.out.println(vo_y.toString());
		}
		
//		searchTime 			= vo_x.getSearchTime() + vo_y.getSearchTime();
		preparationTime 	= vo_x.getPrepareTime() + vo_y.getPrepareTime();
		verificationTime 	= vo_x.getVerifyTime() + vo_y.getVerifyTime();
		voSize 				= vo_x.getVOSize() + vo_y.getVOSize();
//		System.out.println(toString());
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
				runCase(in.nextLine(), Constants.InputQuery);
				System.out.println(toString());
			} else if (line.equalsIgnoreCase("b")){
				System.out.println("Input file to query:");
				String fileName = in.nextLine();
				Scanner fin = new Scanner(new File(fileName)); int num = Integer.parseInt(fin.nextLine());
				Statistics stat = new Statistics();
				for (int i = 0; i < num; i ++) {
					runCase(fin.nextLine(), Constants.FileQuery);
					stat.append(preparationTime, verificationTime, voSize);
				}
				fin.close();
				System.out.println(stat.toString());
			} else if (line.equalsIgnoreCase("c")){
				System.out.println("Input folder to query:");
				String dir = in.nextLine();
				if (!dir.endsWith("/")) dir = dir + "/";
				System.out.println("Input file prefix to query:");
				String prefix = in.nextLine();
				System.out.println("Input query type (t/p):");
				String type = in.nextLine();
				Statistics[] stats = new Statistics[QueryGenerator.ratios.length];
				PrintWriter pw = new PrintWriter(new File(dir + prefix + ".ans_" + index + "_" + PMAC.noPhi + "_" + type));
				for (int i = QueryGenerator.ratios.length - 1; i >= 0; i --) {
					String fileName = dir + prefix + "." + type + "_" + Math.sqrt(QueryGenerator.ratios[i]);
					Scanner fin = new Scanner(new File(fileName)); int num = Integer.parseInt(fin.nextLine());
					stats[i] = new Statistics();
					for (int j = 0; j < num; j ++) {
						runCase(fin.nextLine(), Constants.FileQuery);
						stats[i].append(preparationTime, verificationTime, voSize);
					}
					fin.close();
					System.out.println(stats[i]);
					pw.println(stats[i].getAvePrepareTime() + "\t" + stats[i].getAveVerifyTime() + "\t" + stats[i].getAveVOSize() / 1024.0);
				}
				pw.close();
			} else {
				System.out.println("Input folder to query:");
				String dir = in.nextLine();
				if (!dir.endsWith("/")) dir = dir + "/";
				System.out.println("Input file prefix to query:");
				String prefix = in.nextLine();
				System.out.println("Input query type (t/p):");
				String type = in.nextLine();
				Statistics[] stats = new Statistics[7];
				PrintWriter pw = new PrintWriter(new File(dir + prefix + ".ans_" + index + "_ratio_" + type));
				for (int i = 0; i < 7; i ++) {
					String fileName = dir + prefix + "." + type + "_" + i;
					Scanner fin = new Scanner(new File(fileName)); int num = Integer.parseInt(fin.nextLine());
					stats[i] = new Statistics();
					for (int j = 0; j < num; j ++) {
						runCase(fin.nextLine(), Constants.FileQuery);
						stats[i].append(preparationTime, verificationTime, voSize);
					}
					fin.close();
					System.out.println(stats[i]);
					pw.println(stats[i].getAvePrepareTime() + "\t" + stats[i].getAveVerifyTime() + "\t" + stats[i].getAveVOSize() / 1024.0);
				}
				pw.close();
			}
		}
	}

	public static void printInfo(int o) {
		if (o == 0) {
			System.out.println("(a) user input");
			System.out.println("(b) file input");
			System.out.println("(c) folder input");
			System.out.println("(d) ratio test");
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
		Generator generator = new Generator("./database/keys");
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
			System.out.println("use phi or not (n/y):");
			String noPhi = in.nextLine();
			if (noPhi.equalsIgnoreCase("n")) {
				PMAC.noPhi = true;
			}
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
