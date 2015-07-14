package coords;

/**
 * Encapsulates a point in space with latitude, longitude coordinates.
 * Can have an error value.
 *
 * @author koontz
 */
public class Coordinates {
    private final Error error;
    private double lat;
    private double lon;

    /**
     * Create polygon.Coordinates at the specified latitude and longitude.
     *
     * @param lat Latitude for this coordinate pair, in degrees.
     * @param lon Longitude for this coordinate pair, in degrees.
     */
    public Coordinates(float lat, float lon) {
        this.lat = lat;
        this.lon = lon;
        this.error = null;
    }

    /**
     *
     */
    public Coordinates(double latitude, double longitude, double laterr, double lonerr) {
        this.lat = latitude;
        this.lon = longitude;
        this.error = new Error(laterr, lonerr);
    }

    /**
     * Get the latitude of this coordinate pair.
     *
     * @return latitude, in degrees.
     */
    public double getLatitude() {
        return lat;
    }

    /**
     * Get the longitude of this coordinate pair.
     *
     * @return longitude, in degrees
     */
    public double getLongitude() {
        return lon;
    }

    /**
     * returns
     */
    public Error getError() {
        return error;
    }

    /**
     * simple toString
     */
    public String toString() {
        return lat + " | " + lon + " | " + error;
    }

}
