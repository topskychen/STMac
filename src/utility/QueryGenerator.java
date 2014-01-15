/**
 * 
 */
package utility;

import io.IO;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

/**
 * @author chenqian
 *
 */
public class QueryGenerator {
	
//	public static double[] ratios = {0.000625, 0.00125, 0.0025, 0.005, 0.01, 0.02, 0.04};
	public static double[] ratios_t = {1, 0.25, 0.625, 0.015625, 0.00390625};
	public static int[] ratios_q = {0, 4, 8, 12, 16};
//	public static double[] ratios = {0.4, 0.2, 0.1, 0.05, 0.025, 0.0125, 0.00625};
	static Data[] dataxs = null;
	static Data[] datays = null;
	
//	public static void queryWithFixedTime(String fileName, double ratio) {
//		try {
//			Scanner in = new Scanner(new File("./dataset/" + fileName + ".txt_x")); int num = Integer.parseInt(in.nextLine());
//			dataxs = new Data[num];
//			for (int i = 0; i < num; i ++) {
//				String[] tks = in.nextLine().split("\t");
//				dataxs[i] = new Data(tks[0], null, Integer.parseInt(tks[1]));
//			}
//			in.close();
//			in = new Scanner(new File("./dataset/" + fileName + ".txt_y")); num = Integer.parseInt(in.nextLine());
//			datays = new Data[num];
//			for (int i = 0; i < num; i ++) {
//				String[] tks = in.nextLine().split("\t");
//				datays[i] = new Data(tks[0], null, Integer.parseInt(tks[1]));
//			} 
//			in.close();
//			
//			int range = (int) ((dataxs[num - 1].time - dataxs[0].time) * ratio);
//			System.out.println(range);
//			ArrayList<Query>[] datas = new ArrayList[8];
//			for (int i = 0; i < 8; i ++) {
//				datas[i] = new ArrayList<Query>();
//			}
//			for (int i = 0; i < num; i ++) {
//				int j = i;
//				while(j < num && dataxs[j].time - dataxs[i].time <= range) {
//					j ++;
//				}
//				if (j < num && dataxs[j].time - dataxs[i].time > range) {
//					datas[0].add(new Query("0", "0", "0", "0", i, j, -1, -1));
//				}
//			}
//			generateQuery(datas, 0);
//			for (int i = 0; i < 7; i ++) {
//				PrintWriter pw = new PrintWriter(new File("./query/" + fileName + ".t_" + ratio + ".qry_" + ratios[i]));
//				pw.println(datas[i + 1].size());
//				System.out.println(datas[i + 1].size());
//				for (int j = 0; j < datas[i + 1].size(); j ++) {
//					String prex = datas[i + 1].get(j).prex; String prey = datas[i + 1].get(j).prey;
//					int l = datas[i + 1].get(j).l, r = datas[i + 1].get(j).r;
//					pw.println(prex + " " + prey + " " + dataxs[l].time + " " + dataxs[r].time);
//					if (j < 100) System.out.print("(" + prex + ", " + prey + ", " + l + ", " + r + ")");
//				}System.out.println();
//				pw.close();
//			}
////			System.out.println(range);
//		} catch (FileNotFoundException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//	}
	
	public static void queryVariedTime(String fileName) {
		try {
			Scanner in = new Scanner(new File("./dataset/" + fileName + ".txt_x")); int num = Integer.parseInt(in.nextLine());
			dataxs = new Data[num];
			for (int i = 0; i < num; i ++) {
				String[] tks = in.nextLine().split("\t");
				dataxs[i] = new Data(tks[0], null, Integer.parseInt(tks[1]));
			}
			in.close();
			in = new Scanner(new File("./dataset/" + fileName + ".txt_y")); num = Integer.parseInt(in.nextLine());
			datays = new Data[num];
			for (int i = 0; i < num; i ++) {
				String[] tks = in.nextLine().split("\t");
				datays[i] = new Data(tks[0], null, Integer.parseInt(tks[1]));
			} 
			in.close();
			in = new Scanner(new File("./dataset/" + fileName + ".txt.enc_x")); num = Integer.parseInt(in.nextLine());
			for (int i = 0; i < num; i ++) {
				String[] tks = in.nextLine().split("\t");
				dataxs[i].prex_enc = tks[0];
			} 
			in.close();
			in = new Scanner(new File("./dataset/" + fileName + ".txt.enc_y")); num = Integer.parseInt(in.nextLine());
			for (int i = 0; i < num; i ++) {
				String[] tks = in.nextLine().split("\t");
				datays[i].prex_enc = tks[0];
			} 
			in.close();
			
			int times = 100, inc = 3;
			for (int i = 0; i < 5; i ++){
				Random random = new Random();
				PrintWriter pw = new PrintWriter(new File("./query/" + fileName + ".t_" + ratios_t[i]));
				PrintWriter pw2 = new PrintWriter(new File("./query/" + fileName + "_enc.t_" + ratios_t[i]));
				int range = (int) ((dataxs[num - 1].time - dataxs[0].time) * ratios_t[i]);
				System.out.println(range);
				pw.println(times * inc);
				pw2.println(times * inc);
				for (int j = 0; j < times; j ++) {
					Query query = getRandomTime(random, num, range);
					for (int k = 0; k < inc; k ++) {
						String prex = getPreString(query.prex, k), prey = getPreString(query.prey, k); 
						String prex_enc = getPreString(query.prex_enc, k), prey_enc = getPreString(query.prey_enc, k);
						System.out.println(prex + " " + prey + " " + query.l + " " + query.r);
						System.out.println(prex_enc + " " + prey_enc + " " + query.l + " " + query.r);
						pw.println(prex + " " + prey + " " + query.l + " " + query.r);
						pw2.println(prex_enc + " " + prey_enc + " " + query.l + " " + query.r);
					}
				}
				pw.close();
				pw2.close();
			}
			
//			System.out.println(range);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void queryVariedPre(String fileName) {
		try {
			Scanner in = new Scanner(new File("./dataset/" + fileName + ".txt_x")); int num = Integer.parseInt(in.nextLine());
			dataxs = new Data[num];
			for (int i = 0; i < num; i ++) {
				String[] tks = in.nextLine().split("\t");
				dataxs[i] = new Data(tks[0], null, Integer.parseInt(tks[1]));
			}
			in.close();
			in = new Scanner(new File("./dataset/" + fileName + ".txt_y")); num = Integer.parseInt(in.nextLine());
			datays = new Data[num];
			for (int i = 0; i < num; i ++) {
				String[] tks = in.nextLine().split("\t");
				datays[i] = new Data(tks[0], null, Integer.parseInt(tks[1]));
			} 
			in.close();
			in = new Scanner(new File("./dataset/" + fileName + ".txt.enc_x")); num = Integer.parseInt(in.nextLine());
			for (int i = 0; i < num; i ++) {
				String[] tks = in.nextLine().split("\t");
				dataxs[i].prex_enc = tks[0];
			} 
			in.close();
			in = new Scanner(new File("./dataset/" + fileName + ".txt.enc_y")); num = Integer.parseInt(in.nextLine());
			for (int i = 0; i < num; i ++) {
				String[] tks = in.nextLine().split("\t");
				datays[i].prex_enc = tks[0];
			} 
			in.close();
			
			int times = 100, inc = 3;
			for (int i = 0; i < 5; i ++){
				Random random = new Random();
				PrintWriter pw = new PrintWriter(new File("./query/" + fileName + ".p_" + ratios_q[i]));
				PrintWriter pw2 = new PrintWriter(new File("./query/" + fileName + "_enc.p_" + ratios_q[i]));
				int range = 32 - ratios_q[i];
				System.out.println(range);
				pw.println(times * inc);
				pw2.println(times * inc);
				for (int j = 0; j < times; j ++) {
					Query query = getRandomPre(random, num, range);
					for (int k = 0; k < inc; k ++) {
						String prex = query.prex, prey = query.prey; 
						String prex_enc = query.prex_enc, prey_enc = query.prey_enc;
						int [] bounds = getTimeRange(query.ol, query.or, query.l, query.r, k, random);
						System.out.println(prex + " " + prey + " " + bounds[0] + " " + bounds[1]);
						System.out.println(prex_enc + " " + prey_enc + " " + bounds[0] + " " + bounds[1]);
						pw.println(prex + " " + prey + " " + bounds[0] + " " + bounds[1]);
						pw2.println(prex_enc + " " + prey_enc + " " + bounds[0] + " " + bounds[1]);
					}
				}
				pw.close();
				pw2.close();
			}
			
//			System.out.println(range);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void getRatioVariedTime(String fileName) {
		try {
			Scanner in = new Scanner(new File("./dataset/" + fileName + ".txt_x")); int num = Integer.parseInt(in.nextLine());
			dataxs = new Data[num];
			for (int i = 0; i < num; i ++) {
				String[] tks = in.nextLine().split("\t");
				dataxs[i] = new Data(tks[0], null, Integer.parseInt(tks[1]));
			}
			in.close();
			in = new Scanner(new File("./dataset/" + fileName + ".txt_y")); num = Integer.parseInt(in.nextLine());
			datays = new Data[num];
			for (int i = 0; i < num; i ++) {
				String[] tks = in.nextLine().split("\t");
				datays[i] = new Data(tks[0], null, Integer.parseInt(tks[1]));
			} 
			in.close();
			in = new Scanner(new File("./dataset/" + fileName + ".txt.enc_x")); num = Integer.parseInt(in.nextLine());
			for (int i = 0; i < num; i ++) {
				String[] tks = in.nextLine().split("\t");
				dataxs[i].prex_enc = tks[0];
			} 
			in.close();
			in = new Scanner(new File("./dataset/" + fileName + ".txt.enc_y")); num = Integer.parseInt(in.nextLine());
			for (int i = 0; i < num; i ++) {
				String[] tks = in.nextLine().split("\t");
				datays[i].prex_enc = tks[0];
			} 
			in.close();
			
			int times = 100, inc = 5;
			for (int k = 0; k < inc; k ++) {
				Random random = new Random();
				PrintWriter pw = new PrintWriter(new File("./query/" + fileName + ".t_ratio_" + k));
				PrintWriter pw2 = new PrintWriter(new File("./query/" + fileName + "_enc.t_ratio_" + k));
				pw.println(times);
				pw2.println(times);
				for (int j = 0; j < times; j ++) {
					for (int i = 0; i < 1; i ++){
						int range = (int) ((dataxs[num - 1].time - dataxs[0].time) * ratios_t[4]);
						Query query = getRandomTime(random, num, range);
						System.out.println(range);
						String prex = getPreString(query.prex, k), prey = getPreString(query.prey, k); 
						String prex_enc = getPreString(query.prex_enc, k), prey_enc = getPreString(query.prey_enc, k);
						System.out.println(prex + " " + prey + " " + query.l + " " + query.r);
						System.out.println(prex_enc + " " + prey_enc + " " + query.l + " " + query.r);
						pw.println(prex + " " + prey + " " + query.l + " " + query.r);
						pw2.println(prex_enc + " " + prey_enc + " " + query.l + " " + query.r);
					}
				}
				pw.close();
				pw2.close();
			}
			
//			System.out.println(range);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public static void getRatioVariedPre(String fileName) {
		try {
			Scanner in = new Scanner(new File("./dataset/" + fileName + ".txt_x")); int num = Integer.parseInt(in.nextLine());
			dataxs = new Data[num];
			for (int i = 0; i < num; i ++) {
				String[] tks = in.nextLine().split("\t");
				dataxs[i] = new Data(tks[0], null, Integer.parseInt(tks[1]));
			}
			in.close();
			in = new Scanner(new File("./dataset/" + fileName + ".txt_y")); num = Integer.parseInt(in.nextLine());
			datays = new Data[num];
			for (int i = 0; i < num; i ++) {
				String[] tks = in.nextLine().split("\t");
				datays[i] = new Data(tks[0], null, Integer.parseInt(tks[1]));
			} 
			in.close();
			in = new Scanner(new File("./dataset/" + fileName + ".txt.enc_x")); num = Integer.parseInt(in.nextLine());
			for (int i = 0; i < num; i ++) {
				String[] tks = in.nextLine().split("\t");
				dataxs[i].prex_enc = tks[0];
			} 
			in.close();
			in = new Scanner(new File("./dataset/" + fileName + ".txt.enc_y")); num = Integer.parseInt(in.nextLine());
			for (int i = 0; i < num; i ++) {
				String[] tks = in.nextLine().split("\t");
				datays[i].prex_enc = tks[0];
			} 
			in.close();
			
			int times = 100, inc = 5;
			for (int k = 0; k < inc; k ++) {
				Random random = new Random();
				PrintWriter pw = new PrintWriter(new File("./query/" + fileName + ".p_ratio_" + k));
				PrintWriter pw2 = new PrintWriter(new File("./query/" + fileName + "_enc.p_ratio_" + k));
				pw.println(times);
				pw2.println(times);
				for (int j = 0; j < times; j ++) {
					for (int i = 0; i < 1; i ++){
						int range = 32 - ratios_q[4];
						Query query = getRandomPre(random, num, range);
						System.out.println(range);
						String prex = query.prex, prey = query.prey; 
						String prex_enc = query.prex_enc, prey_enc = query.prey_enc;
						int [] bounds = getTimeRange(query.ol, query.or, query.l, query.r, k, random);
						System.out.println(prex + " " + prey + " " + bounds[0] + " " + bounds[1]);
						System.out.println(prex_enc + " " + prey_enc + " " + bounds[0] + " " + bounds[1]);
						pw.println(prex + " " + prey + " " + bounds[0] + " " + bounds[1]);
						pw2.println(prex_enc + " " + prey_enc + " " + bounds[0] + " " + bounds[1]);
					}
				}
				pw.close();
				pw2.close();
			}
			
//			System.out.println(range);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static int[] getTimeRange (int ol, int or, int l, int r, int k, Random random) {
		int range = (r - l) >> (4*k), times = 100;
		while (times -- > 0) {
			int start = random.nextInt(or - ol + 1) + ol;
			int end = start;
			while( end <= or) {
				if ((dataxs[end].time - dataxs[start].time) >= range) {
					return new int[] {dataxs[start].time, dataxs[end].time};
				}
				end ++;
			}
		}
		if (k != 0) System.out.println("no found");
		return new int[]{l, r};
	}
	
	public static String getPreString(String pre, int k) {
		if (pre.length() > 4*k) {
			return pre.substring(0, pre.length() - 4*k); // -k
		} else {
			return pre.substring(0, 1);
		}
	} 
	
	public static Query getRandomPre(Random random, int num, int range) {
		int times = 100;
		while (true) {
			int start = random.nextInt(num);
			int end = start;
			String prex = dataxs[end].prex;
			String prey = datays[end].prex;
			while( end < num) {
				if (IO.commonPrefix(prex, dataxs[end].prex).length() < range &&
						IO.commonPrefix(prey, datays[end].prex).length() < range) {
					-- end;
					String[] tra = new String[end - start + 1];
					for (int i = start; i <= end; i ++) {
						tra[i - start] = dataxs[i].prex_enc; 
					}
					String prex_enc = IO.commonPrefix(tra, 0, end - start);
					tra = new String[end - start + 1];
					for (int i = start; i <= end; i ++) {
						tra[i - start] = datays[i].prex_enc; 
					}
					String prey_enc = IO.commonPrefix(tra, 0, end - start);
					return new Query (prex, prey, prex_enc, prey_enc, dataxs[start].time, dataxs[end].time, start, end);
				}
				prex = IO.commonPrefix(prex, dataxs[end].prex);
				prey = IO.commonPrefix(prey, datays[end].prex);
				end ++;
			}
			if (times -- == 0) {
				String[] tra = new String[num];
				for (int i = 0; i < num; i ++) {
					tra[i] = dataxs[i].prex; 
				}
				prex = IO.commonPrefix(tra, 0, num - 1);
				tra = new String[num];
				for (int i = 0; i < num; i ++) {
					tra[i] = datays[i].prex; 
				}
				prey = IO.commonPrefix(tra, 0, num - 1);
				tra = new String[num];
				for (int i = 0; i < num; i ++) {
					tra[i] = dataxs[i].prex_enc; 
				}
				String prex_enc = IO.commonPrefix(tra, 0, num - 1);
				tra = new String[num];
				for (int i = 0; i < num; i ++) {
					tra[i] = datays[i].prex_enc; 
				}
				String prey_enc = IO.commonPrefix(tra, 0, num - 1);
				return new Query (prex, prey, prex_enc, prey_enc, dataxs[0].time, dataxs[num - 1].time, 0, num - 1);
			}
		}
	}
	
	public static Query getRandomTime(Random random, int num, int range) {
		boolean entire = false;
		if ((dataxs[num - 1].time - dataxs[0].time) <= range) 
			entire = true;
		while (true) {
			int start = random.nextInt(num);
			int end = start;
			while( end < num) {
				if ((dataxs[end].time - dataxs[start].time) >= range || entire == true) {
					if (entire) {
						end = num - 1; start = 0;
					}
					String[] tra = new String[end - start + 1];
					for (int i = start; i <= end; i ++) {
						tra[i - start] = dataxs[i].prex; 
					}
					String prex = IO.commonPrefix(tra, 0, end - start);
					tra = new String[end - start + 1];
					for (int i = start; i <= end; i ++) {
						tra[i - start] = datays[i].prex; 
					}
					String prey = IO.commonPrefix(tra, 0, end - start);
					tra = new String[end - start + 1];
					for (int i = start; i <= end; i ++) {
						tra[i - start] = dataxs[i].prex_enc; 
					}
					String prex_enc = IO.commonPrefix(tra, 0, end - start);
					tra = new String[end - start + 1];
					for (int i = start; i <= end; i ++) {
						tra[i - start] = datays[i].prex_enc; 
					}
					String prey_enc = IO.commonPrefix(tra, 0, end - start);
					return new Query (prex, prey, prex_enc, prey_enc, dataxs[start].time, dataxs[end].time, start, end);
				}
				end ++;
			}
		}
	}
	
//	public static String[] extendPre(String prex, String prey, double ratio) {
//		StringBuffer sbx = new StringBuffer(prex);
//		StringBuffer sby = new StringBuffer(prey);
//		while(sbx.length() > 2 && sby.length() > 2) {
//			if (((32-sbx.length()+1) * (32-sby.length() + 1) / (256.0*256.0)) > ratio) {
//				break;
//			}
//			sbx.deleteCharAt(sbx.length() - 1);
//			sby.deleteCharAt(sby.length() - 1);
//		}
//		return new String[]{sbx.toString(), sby.toString()};
//	}
//	
//	public static void generateQuery (ArrayList<Query>[] datas, int level) {
//		if (level >= 7) return;
//		for (int i = 0; i < datas[level].size(); i ++) {
//			int l = datas[level].get(i).l;
//			int r = datas[level].get(i).r;
//			String[] prex = new String[r - l + 1];
//			String[] prey = new String[r - l + 1];
//			for (int j = l; j <= r; j ++) {
//				prex[j - l] = dataxs[j].prex;
//				prey[j - l] = datays[j].prex;
//			}
//			String cprex = DataIO.commonPrefix(prex, 0, r - l);
//			String cprey = DataIO.commonPrefix(prey, 0, r - l);
//			if (((32-cprex.length()) * (32-cprey.length()) / (256.0*256.0)) <= ratios[level]) {
////				System.out.println(cprex.length() + ", " + cprey.length());
//				String[] pre_ext = extendPre(cprex, cprey, ratios[level]); 
//				datas[level + 1].add(new Query(pre_ext[0], pre_ext[1], null, null, l, r, -1, -1));
//			}
//		}
//		generateQuery(datas, level + 1);
//	} 
	
//	public static void 
	
	public static void timeWithFixedQuery(String fileName, double query) {
		
	}
	
	/**
	 * 
	 */
	public QueryGenerator() {
		// TODO Auto-generated constructor stub
	}


	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
//		queryWithFixedTime("./dataset/10000.txt", Math.sqrt(0.00000625));
//		queryWithFixedTime("./dataset/1000.txt", Math.sqrt(0.00125));
//		queryWithFixedTime("1000.txt", Math.sqrt(0.0025));
//		queryWithFixedTime("1000.txt", Math.sqrt(0.005));
//		queryWithFixedTime("1000.txt", Math.sqrt(0.01));
//		queryWithFixedTime("1000.txt", Math.sqrt(0.02));
//		queryWithFixedTime("./dataset/10000.txt", Math.sqrt(0.0004));
		queryVariedTime("100000");
		queryVariedTime("10000");
		queryVariedTime("1000");
		queryVariedPre("100000");
		queryVariedPre("10000");
		queryVariedPre("1000");
		getRatioVariedPre("100000");
		getRatioVariedPre("10000");
		getRatioVariedPre("1000");
		getRatioVariedTime("100000");
		getRatioVariedTime("10000");
		getRatioVariedTime("1000");
	}
}

class Query {
	public String prex, prey;
	public String prex_enc, prey_enc;
	public int l, r;
	public int ol, or;
	public Query(String prex, String prey, String prex_enc, String prey_enc, int l, int r, int ol, int or) {
		super();
		this.prex = prex;
		this.prey = prey;
		this.prex_enc = prex_enc;
		this.prey_enc = prey_enc;
		this.l = l;
		this.r = r;
		this.ol = ol;
		this.or = or;
	}
	
}

class Data {
	public String prex;
	public String prex_enc;
	public int time;
	
	public Data(String prex, String prex_enc, int time) {
		super();
		this.prex = prex;
		this.prex_enc = prex_enc;
		this.time = time;
	}
}
