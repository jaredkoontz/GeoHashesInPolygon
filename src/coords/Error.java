package coords;

public class Error {

    private final double lat;
    private final double lon;

    public Error(double lat, double lon) {
        this.lat = lat;
        this.lon = lon;
    }

    public String toString() {
        return lat + " | " + lon;
    }

    public double getLat() {
        return lat;
    }

    public double getLon() {
        return lon;
    }


}
