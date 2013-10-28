/**
 * 
 */
package crypto;

import java.math.BigInteger;

/**
 * @author chenqian
 *
 */
public class Epoch {

	int preEpoch, curEpoch = -1, nextEpoch;
	
	public int getCurEpoch() {
		return curEpoch;
	}

	public void setCurEpoch(int curEpoch) {
		this.curEpoch = curEpoch;
	}

	public int getPreEpoch() {
		return preEpoch;
	}

	public void setPreEpoch(int preEpoch) {
		this.preEpoch = preEpoch;
	}

	public int getNextEpoch() {
		return nextEpoch;
	}

	public void setNextEpoch(int nextEpoch) {
		this.nextEpoch = nextEpoch;
	}

	public Epoch(int preEpoch, int curEpoch, int nextEpoch) {
		if (preEpoch > curEpoch || nextEpoch < curEpoch) {
			throw new IllegalStateException("The epoch state is not correct.");
		}
		this.curEpoch = curEpoch;
		this.preEpoch = preEpoch;
		this.nextEpoch = nextEpoch;
	}

	public BigInteger getDigest() {
		if (curEpoch < 0) {
			throw new IllegalStateException("The current Epoch is " + curEpoch);
		} else if (curEpoch == 0) return BigInteger.ONE;
		String ts = "" + preEpoch + "|" + curEpoch + "|" + nextEpoch;
		Hasher.hashString(ts);
		return new BigInteger(Hasher.hashString(ts), 16);
	}
	
	
	/**
	 * 
	 */
	public Epoch() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Epoch epoch = new Epoch(-1, 0, 1);
		System.out.println(epoch.getDigest());
		epoch = new Epoch(-1, 1, 3);
		System.out.println(epoch.getDigest());
	}

}
