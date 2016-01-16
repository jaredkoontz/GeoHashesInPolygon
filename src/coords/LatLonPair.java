package coords;

/**
 * todo
 *
 * @author koontz
 */
public class LatLonPair {

	private final double lat;
	private final double lon;

	/**
	 * todo
	 */
	public LatLonPair(double lat, double lon) {

		this.lat = lat;
		this.lon = lon;
	}

	/**
	 * todo
	 */
	public double getLon() {
		return lon;
	}

	/**
	 * todo
	 */
	public double getLat() {
		return lat;
	}

	/**
	 * todo
	 */
	public String toString() {
		return lat + " | " + lon;
	}

}
