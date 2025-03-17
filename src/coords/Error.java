package coords;

public class Error {

    private final LatLonPair latLonPair;

    Error(double lat, double lon) {
        this.latLonPair = new LatLonPair(lat, lon);
    }

    public double getLat() {
        return latLonPair.getLat();
    }

    public double getLon() {
        return latLonPair.getLon();
    }


}
