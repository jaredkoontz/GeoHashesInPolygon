package coords;

public class LatLonPair {

    private final double lat;
    private final double lon;

    public LatLonPair(double lat, double lon) {

        this.lat = lat;
        this.lon = lon;
    }

    public double getLon() {
        return lon;
    }

    public double getLat() {
        return lat;
    }

    public String toString() {
        return lat + " | " + lon;
    }

}
