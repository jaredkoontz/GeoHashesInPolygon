/**
 * Created by jared on 5/27/15.
 */
public class Coordinate {
    protected final double latitude;
    private final double longitude;
    private final double laterr;
    private final double lonerr;

    public Coordinate(double latitude, double longitude, double laterr, double lonerr) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.laterr = laterr;
        this.lonerr = lonerr;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public double getLaterr() {
        return laterr;
    }

    public double getLonerr() {
        return lonerr;
    }


    public String toString() {
        return latitude + " | " + longitude + " | " + laterr + " | " + lonerr;
    }

}
