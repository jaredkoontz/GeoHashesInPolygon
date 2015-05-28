public class CoordinateWithError {
    protected final double latitude;
    private final double longitude;
    private final Error error;

    public CoordinateWithError(double latitude, double longitude, double laterr, double lonerr) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.error = new Error(laterr, lonerr);
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public Error getError() {
        return error;
    }


    public String toString() {
        return latitude + " | " + longitude + " | " + error;
    }


}
