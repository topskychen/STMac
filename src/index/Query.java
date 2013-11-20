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
	int start, end;
	
	public Query(String range, int start, int end) {
		this.range = range;
		this.start = start;
		this.end = end;
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

	public int getStart() {
		return start;
	}

	public void setStart(int start) {
		this.start = start;
	}

	public int getEnd() {
		return end;
	}

	public void setEnd(int end) {
		this.end = end;
	}	

	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append(range + ", [" + start + ", " + end  + "]");
		return sb.toString();
	}


	public int getQueryType() {
		return queryType;
	}


	public void setQueryType(int queryType) {
		this.queryType = queryType;
	}
	
	
}
