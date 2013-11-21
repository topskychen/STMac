/**
 * 
 */
package index;

/**
 * @author chenqian
 *
 */
public class Query {

	
	private int queryType;
	String range;
	int lBound, rBound;
	
	public Query(String range, int lBound, int rBound) {
		this.range = range;
		this.lBound = lBound;
		this.rBound = rBound;
	}


	/**
	 * 
	 */
	public Query() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

	public String getRange() {
		return range;
	}

	public void setRange(String range) {
		this.range = range;
	}


	public int getlBound() {
		return lBound;
	}


	public void setlBound(int lBound) {
		this.lBound = lBound;
	}


	public int getrBound() {
		return rBound;
	}


	public void setrBound(int rBound) {
		this.rBound = rBound;
	}


	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append(range + ", [" + lBound + ", " + rBound  + "]");
		return sb.toString();
	}


	public int getQueryType() {
		return queryType;
	}


	public void setQueryType(int queryType) {
		this.queryType = queryType;
	}
	
	
}
