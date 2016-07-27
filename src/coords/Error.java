package coords;

/**
 * todo
 */
public class Error {

	private final LatLonPair latLonPair;

	/**
	 * todo
	 */
	Error(double lat, double lon) {
		this.latLonPair = new LatLonPair(lat, lon);
	}


	/**
	 * todo
	 */
	public double getLat() {
		return latLonPair.getLat();
	}

	/**
	 * todo
	 */
	public double getLon() {
		return latLonPair.getLon();
	}


}
