/**
 * 
 */
package crypto;

import index.BInnerData;
import index.Data;
import index.LeafData;
import index.Query;
import index.SearchIndex;
import index.Trajectory;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;

import timer.Timer;

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
	
	public VOCell prepareFromLeafData(PMAC pmac, LeafData data, Query query) {
		VOCell voCell = new VOCell(data.getSigma(), 
				pmac.generateGPiSu(data.getLocation(), data.getR(), query.getRange().length()), 
				data.getT1(), 
				data.getT2(), 
				data.getT2(), 
				data.getT3());
		return voCell;
	}
	
	public VOCell prepareBInnerData(PMAC pmac, BInnerData data, Query query) {
		VOCell voCell = new VOCell(data.getSigma(), 
				pmac.increGPiSu(data.getPrex(), data.getG_pi_su(), query.getRange().length()), 
				data.getT1(), 
				data.getT2(), 
				data.getT3(), 
				data.getT4());
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
			if (data instanceof LeafData) {
				voCells.add(prepareFromLeafData(pmac, (LeafData)data, query));
			} else {
				voCells.add(prepareBInnerData(pmac, (BInnerData)data, query));
			}
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
				break;
			} //else {
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
		sb.append("VOSize: " + voSize + "bytes, " + voSize / 1024 + " KB\n");
		if(!precise){
			for (int i = 0; i < voCells.size(); i ++) {
				sb.append(voCells.get(i).toString());
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
		BigInteger sigma			= null;
		BigInteger 	g_pi_su			= null; 
		int t1, t2, t3, t4;
		
		public VOCell(BigInteger sigma, BigInteger g_pi_su, int t1, int t2,
				int t3, int t4) {
			super();
			this.sigma = sigma;
			this.g_pi_su = g_pi_su;
			this.t1 = t1;
			this.t2 = t2;
			this.t3 = t3;
			this.t4 = t4;
		}
		
		/**
		 * Verify the VO.
		 * @return
		 */
		public boolean verify(PMAC pmac, Query query) {
			boolean isVerify = false;
			BigInteger pi_prex = pmac.generatePix(query.getRange());
			BigInteger verifierComponent = pmac.generatePMACbyPrex(g_pi_su, pi_prex, t1, t2, t3, t4);
			isVerify = pmac.verify(sigma, verifierComponent);
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
				ds.writeInt(t1);
				ds.writeInt(t2);
				ds.writeInt(t3);
				ds.writeInt(t4);
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
			sb.append(t1 + ", " + t2 + ", " + t3 + ", " + t4 + "\n");
			return sb.toString();
		}
	}

}
