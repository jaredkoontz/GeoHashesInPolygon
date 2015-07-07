package coords;

/**
 * todo
 */
public class Error {

    private final double lat;
    private final double lon;

    /**
     * todo
     */
    public Error(double lat, double lon) {
        this.lat = lat;
        this.lon = lon;
    }

    /**
     * todo
     */
    public String toString() {
        return lat + " | " + lon;
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
    public double getLon() {
        return lon;
    }


}
