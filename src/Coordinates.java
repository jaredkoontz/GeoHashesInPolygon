/**
 * Encapsulates a point in space with latitude, longitude coordinates.
 */
public class Coordinates {
    private float lat;
    private float lon;

    /**
     * Create Coordinates at the specified latitude and longitude.
     *
     * @param lat Latitude for this coordinate pair, in degrees.
     * @param lon Longitude for this coordinate pair, in degrees.
     */
    public Coordinates(float lat, float lon) {
        this.lat = lat;
        this.lon = lon;
    }

    /**
     * Get the latitude of this coordinate pair.
     *
     * @return latitude, in degrees.
     */
    public float getLatitude() {
        return lat;
    }

    /**
     * Get the longitude of this coordinate pair.
     *
     * @return longitude, in degrees
     */
    public float getLongitude() {
        return lon;
    }


}
