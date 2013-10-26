/**
 * 
 */
package index;

/**
 * @author chenqian
 *
 */
public class Trajectory {

	String[] locations = null;
	Long[]	timeStamps = null; 
	
	public String getLocation() {
		return locations[0];
	}
	
	public Trajectory(String[] locations, Long[] timeStamps) {
		super();
		this.locations = locations;
		this.timeStamps = timeStamps;
		if (timeStamps.length != locations.length + 2) {
			throw new IllegalStateException("The length of timeStamps should be 2 + length of locations.");
		}
	}

	public void setLocation(String x) {
		locations = new String[1];
		locations[0] = x;
	}
	
	public String[] getLocations() {
		return locations;
	}

	public void setLocations(String[] locations) {
		this.locations = locations;
	}

	public Long[] getTimeStamps() {
		return timeStamps;
	}

	public void setTimeStamps(Long[] timeStamps) {
		this.timeStamps = timeStamps;
	}

	/**
	 * 
	 */
	public Trajectory() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * 
	 */
	public Trajectory(String x) {
		// TODO Auto-generated constructor stub
		setLocation(x);
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Trajectory trajectory = new Trajectory(new String[]{"0000", "0001", "0010", "0011"}, new Long[] {0l, 1l, 2l, 3l, 4l, 5l});
		System.out.println(trajectory.toString());
	}

	public int length() {
		if (locations == null) return 0;
		return locations.length;
	}
	
	public String toString() {
		StringBuffer sb = new StringBuffer("");
		sb.append("length = " + length() + " [");
		for (int i = 0; i < length(); i ++) {
			if (i != 0) {
				sb.append(",");
			}
			sb.append("(" + "\"" + locations[i] + "\"");
			if (timeStamps != null) {
				sb.append(", " + timeStamps[i]);
			}
			sb.append(")");
		}
		sb.append("]");
		return sb.toString();
	}

}
