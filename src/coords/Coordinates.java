package coords;

/**
 * Encapsulates a point in space with latitude, longitude coordinates.
 * Can have an error value.
 *
 * @author koontz
 */
public class Coordinates {
	private final Error error;
	private final LatLonPair latLonPair;

	/**
	 * Create polygon.Coordinates at the specified latitude and longitude.
	 *
	 * @param lat Latitude for this coordinate pair, in degrees.
	 * @param lon Longitude for this coordinate pair, in degrees.
	 */
	public Coordinates(float lat, float lon) {
		this.latLonPair = new LatLonPair(lat,lon);
		this.error = null;
	}

	/**
	 * todo
	 */
	public Coordinates(double lat, double lon, double laterr, double lonerr) {
		this.latLonPair = new LatLonPair(lat,lon);
		this.error = new Error(laterr, lonerr);
	}

	/**
	 * Get the latitude of this coordinate pair.
	 *
	 * @return latitude, in degrees.
	 */
	public double getLatitude() {
		return latLonPair.getLat();
	}

	/**
	 * Get the longitude of this coordinate pair.
	 *
	 * @return longitude, in degrees
	 */
	public double getLongitude() {
		return latLonPair.getLon();
	}

	/**
	 * Get the Error values.
	 *
	 * @return Error object
	 */
	public Error getError() {
		return error;
	}

	/**
	 * simple toString 
	 */
	public String toString() {
		return latLonPair.toString() + " | " + error;
	}

}
