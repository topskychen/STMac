/**
 * 
 */
package crypto;

import index.BData;
import index.Data;
import index.GData;
import index.Query;
import index.SearchIndex;
import index.Trajectory;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;

import IO.DataIO;
import timer.Timer;
import crypto.Gfunction;

/**
 * @author chenqian
 *
 */
public class VO {

	Timer timer 				= null;
	private double prepareTime	= -1; 
	private double verifyTime 	= -1;
	private int 	voSize 		= -1;
	ArrayList<VOCell> voCells 	= new ArrayList<VOCell>();
	Query query 				= null;
//	Trajectory 	trajectory		= null;
	public static boolean precise = true;
	
	public VO() {
		timer = new Timer();
	}
	
	/**
	 * Prepare the VO
	 */
	public void prepare(PMAC pmac, Trajectory tra, Query query) {
		timer.reset();
		int d = query.getRange().length();
		int[] range = tra.getTimeRange(query.getlBound(), query.getrBound());
		VOCell voCell = new VOCell(
				pmac.aggregatePMACs(tra, range[0], range[1]),
				pmac.generateTraGPiSu(tra.getLocations(), tra.getRs(), range[0], range[1], d),
				tra.getTimeStamp(range[0] - 1),
				tra.getTimeStamp(range[0]),
				tra.getTimeStamp(range[1]),
				tra.getTimeStamp(range[1] + 1)); 
		voCells.add(voCell);
		this.query = query;
		timer.stop();
		prepareTime = timer.timeElapseinMs();
	}
	
	public VOCell prepareBData(PMAC pmac, BData data, Query query) {
		VOCell voCell = new VOCell(data.getSigma(), 
				pmac.increGPiSu(data.getPrex(), data.getG_pi_su(), query.getRange().length()), 
				data.getT1(), 
				data.getT2(), 
				data.getT3(), 
				data.getT4());
		return voCell;
	}
	
	public VOCell prepareGData(PMAC pmac, GData data, Query query) {
		boolean isLeft = false, isRight = false;
		if (data.getT2() < query.getlBound()) {
			isLeft = true;
		}
		if (data.getT3() > query.getrBound() ) {
			isRight = true;
		}
		VOCell voCell = new VOCell(data.getSigma(), 
				pmac.increGPiSu(data.getPrex(), data.getG_pi_su(), query.getRange().length()), 
				data.getGf1(), 
				isLeft ? data.getGf2().prepareValueLessThan(query.getlBound()) : data.getGf2().getDigest(), 
				isRight ? data.getGf3().prepareValueGreaterThan(query.getrBound()) : data.getGf3().getDigest(), 
				data.getGf4(),
				data.getT1(), 
				data.getT2(), 
				data.getT3(), 
				data.getT4(),
				isLeft ? GData.getHash(data.getT1()) : null,
				isLeft ? GData.getHash(data.getT2()) : null,
				isRight ? GData.getHash(data.getT3()) : null,
				isRight ? GData.getHash(data.getT4()) : null
				);
		voCell.setLeft(isLeft);
		voCell.setRight(isRight);
		return voCell;
	}
	
	/**
	 * Prepare VO from Index
	 * @param pmac
	 * @param index
	 * @param query
	 */
	public void prepare(PMAC pmac, SearchIndex index, Query query) {
		timer.reset();
		ArrayList<Data> datas = index.rangeQuery(query);
		for (Data data : datas) {
			if (data instanceof GData) voCells.add(prepareGData(pmac, (GData)data, query));
			else if (data instanceof BData) voCells.add(prepareBData(pmac, (BData)data, query));
		}
		timer.stop();
		prepareTime = timer.timeElapseinMs();
	}
	
	/**
	 * Verify the VO.
	 * @return
	 */
	public boolean verify(PMAC pmac, Query query) {
		timer.reset();
		boolean isVerify = true;
		for (VOCell voCell : voCells) {
			if (!voCell.verify(pmac, query)) {
				isVerify = false;
//				System.out.println("fail");
				break;
			} 
//			else {
//				System.out.println("pass");
//			}
//			if (voCell.t2 == voCell.t3) {
//				System.out.println(voCell.toString());
////				System.out.println(pmac.toString());
//			}
		}
		timer.stop();
		verifyTime = timer.timeElapseinMs();
		voSize = this.toBytes().length;
		return isVerify;
	}
	
	
	public byte[] toBytes() {
		ByteArrayOutputStream bs = new ByteArrayOutputStream();
		DataOutputStream ds = new DataOutputStream(bs);
		for (VOCell voCell : voCells) {
			voCell.write(ds);
		}
		return bs.toByteArray();
	}

	
	public String toString() {
		StringBuffer sb = new StringBuffer("");
		sb.append("PrepareTime: " + prepareTime + "ms\n");
		sb.append("VerifyTime: " + verifyTime + "ms\n");
		sb.append("VOSize: " + voSize + "bytes, " + voSize / 1024.0 + " KB\n");
		if(!precise){
			for (int i = 0; i < voCells.size(); i ++) {
				sb.append((i + 1)  + " [ " + voCells.get(i).toString() + " ] : " + Data.TYPE_NAMES[voCells.get(i).voType] + "\n");
			}
		}
		return sb.toString();
	}
	
	public double getVerifyTime() {
		if (verifyTime == -1) {
			throw new IllegalStateException("No verify function is called.");
		}
		return verifyTime;
	}
	
	/**
	 * Get preparation time.
	 * @return
	 */
	public double getPrepareTime() {
		if (prepareTime == -1) {
			throw new IllegalStateException("No prepare function is called.");
		}
		return prepareTime;
	}
	
	/**
	 * 
	 * @return
	 */
	public int getVOSize() {
		if (voSize == -1) {
			throw new IllegalStateException("No prepare function is called.");
		}
		return voSize;
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
	}
	
	class VOCell {
		int voType 					= -1;				
		BigInteger sigma			= null;
		BigInteger 	g_pi_su			= null; 
		int[] timeStamps			= null;
		byte[][] gfs 				= null;
		byte[][] timeDigests 		= null;
		boolean left				= false;
		boolean right	 			= false;
		
		
		public VOCell(BigInteger sigma, BigInteger g_pi_su, int t1, int t2,
				int t3, int t4) {
			super();
			this.sigma = sigma;
			this.g_pi_su = g_pi_su;
			this.timeStamps = new int[4];
			this.timeStamps[0] = t1;
			this.timeStamps[1] = t2;
			this.timeStamps[2] = t3;
			this.timeStamps[3] = t4;
			this.voType = Data.B_TYPE;
		}
		
		public VOCell(BigInteger sigma, BigInteger g_pi_su, 
				byte[] gf1, byte[] gf2, byte[] gf3, byte[] gf4,
				int t1, int t2, int t3, int t4,
				byte[] tg1, byte[] tg2, byte[] tg3, byte[] tg4) {
			this.sigma = sigma;
			this.g_pi_su = g_pi_su;
			this.gfs = new byte[4][];
			this.gfs[0] = gf1;
			this.gfs[1] = gf2;
			this.gfs[2] = gf3;
			this.gfs[3] = gf4;
			this.timeStamps = new int[4];
			this.timeStamps[0] = t1; // Note here, the timestamps are used only for debuging printing
			this.timeStamps[1] = t2;
			this.timeStamps[2] = t3;
			this.timeStamps[3] = t4;
			this.timeDigests = new byte[4][];
			this.timeDigests[0] = tg1;
			this.timeDigests[1] = tg2;
			this.timeDigests[2] = tg3;
			this.timeDigests[3] = tg4;
			this.voType = Data.G_TYPE;
		}
		
		public void setGf2(byte[] data) {
			this.left = true;
			this.gfs[1] = data;
		}
		
		public void setGf3(byte[] data) {
			this.right = true;
			this.gfs[2] = data;
		}
		
		public boolean isRight() {
			return right;
		}

		public void setRight(boolean right) {
			this.right = right;
		}

		
		public boolean isLeft() {
			return left;
		}

		public void setLeft(boolean left) {
			this.left = left;
		}


		
		/**
		 * Verify the VO.
		 * @return
		 */
		public boolean verify(PMAC pmac, Query query) {
			boolean isVerify = false;
			BigInteger pi_prex = pmac.generatePix(query.getRange());
			if (voType == Data.B_TYPE) {
				BigInteger verifierComponent = pmac.generatePMACbyPrex(g_pi_su, pi_prex, 
						timeStamps[0], timeStamps[1], timeStamps[2], timeStamps[3]);
				isVerify = pmac.verify(sigma, verifierComponent);
			} else if (voType == Data.G_TYPE) {
				byte[] gf2 = null, gf3 = null;
				if (isLeft()) {
					gf2 = Gfunction.getDigest(gfs[1]);
					if (!Gfunction.verifyValueLessThan(gfs[1], query.getlBound())) return false;
				}
				else gf2 = gfs[1];
				if (isRight()) {
					gf3 = Gfunction.getDigest(gfs[2]);
					if (!Gfunction.verifyValueGreaterThan(gfs[2], query.getrBound())) return false;
				}
				else gf3 = gfs[2];
				BigInteger verifierComponent = pmac.generatePMACbyPrex(g_pi_su, pi_prex, 
						GData.getHash(gfs[0], isLeft() ? timeDigests[0] : GData.getHash(timeStamps[0])), 
						GData.getHash(gf2, isLeft() ? timeDigests[1] : GData.getHash(timeStamps[1])), 
						GData.getHash(gf3, isRight() ? timeDigests[2] : GData.getHash(timeStamps[2])), 
						GData.getHash(gfs[3], isRight() ? timeDigests[3] : GData.getHash(timeStamps[3])));
				isVerify = pmac.verify(sigma, verifierComponent);
			}
			return isVerify;
		}
		
		/**
		 * get VO in bytes
		 * @return
		 * @throws IOException 
		 */
		public byte[] toBytes() {
			ByteArrayOutputStream bs = new ByteArrayOutputStream();
			DataOutputStream ds = new DataOutputStream(bs);
			write(ds);
			return bs.toByteArray();
		}
		
		public void write(DataOutputStream ds) {
			try {
				ds.write(sigma.toByteArray());
				ds.write(g_pi_su.toByteArray());
				DataIO.writeIntArrays(ds, timeStamps);
				if (gfs != null) {
					for (int i = 0; i < gfs.length; i ++) {
						DataIO.writeBytes(ds, gfs[i]);
					}
				}
				if (timeDigests != null) {
					for (int i = 0; i < timeDigests.length; i ++) {
						DataIO.writeBytes(ds, timeDigests[i]);
					}
				}
				ds.flush();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		public String toString() {
			StringBuffer sb = new StringBuffer();
//			sb.append(sigma + "\n");
//			sb.append(g_pi_su + "\n");
			sb.append(timeStamps[0] + "|" + timeStamps[1] + ", " + timeStamps[2] + "|" + timeStamps[3]);
			return sb.toString();
		}
	}

}
