package geohash;


import coords.Coordinates;
import polygon.PointInPolygon;

/**
 todo
 */
public class GeoHashUtils {

    /**
     * todo
     */
    public static Coordinates decodeWithError(String hash_string) {
        double[] bbox = GeoHash.decode_bbox(hash_string);
        double lat = (bbox[0] + bbox[2]) / 2;
        double lon = (bbox[1] + bbox[3]) / 2;
        double laterr = bbox[2] - lat;
        double lonerr = bbox[3] - lon;
        return new Coordinates(lat, lon, laterr, lonerr);
    }


    /**
     * determine if lon1 is west of lon2
     * returns boolean
     */
    public static boolean isWest(double lon1, double lon2) {
        return (lon1 < lon2 && lon2 - lon1 < 180) || (lon1 > lon2 && lon2 - lon1 + 360 < 180);
    }


    /**
     * todo
     */
    public static double[] polyToBB(Coordinates[] polygon) {
        double minLat = Double.MAX_VALUE, minLon = minLat, maxLat = -minLat, maxLon = -minLat;
        for (Coordinates p : polygon) {
            minLat = Math.min(minLat, p.getLatitude());

            minLon = Math.min(minLon, p.getLongitude());
            maxLat = Math.max(maxLat, p.getLatitude());
            maxLon = Math.max(maxLon, p.getLongitude());
        }
        return new double[]{minLat, minLon, maxLat, maxLon};
    }


    /**
     * todo
     */
    public static String neighbor(String hash, int[] direction) {
        Coordinates lonlat = GeoHashUtils.decodeWithError(hash);
        double neighbor_lat = lonlat.getLatitude()
                + direction[0] * lonlat.getError().getLat() * 2;

        double neighbor_lon = lonlat.getLongitude()
                + direction[1] * lonlat.getError().getLon() * 2;

        return GeoHash.encode(neighbor_lat, neighbor_lon, hash.length());
    }

    /**
     * todo
     */
    public static int inside(Coordinates point, Coordinates[] polygon) {
        int inside = 0;

        inside += PointInPolygon.pip(new double[]{point.getLongitude(), point.getLatitude()}, polygon) ? 1 : 0;

        return inside % 2;
    }

}
