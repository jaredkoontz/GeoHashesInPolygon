import java.util.HashMap;
import java.util.HashSet;


public class GeoHashesInPolygon {
    public final static byte BITS_PER_CHAR = 5;
    public final static int LATITUDE_RANGE = 90;
    public final static int LONGITUDE_RANGE = 180;
    /**
     * This character array maps integer values (array indices) to their
     * GeoHash base32 alphabet equivalents.
     */
    public final static char[] charMap = {
            '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'b', 'c',
            'd', 'e', 'f', 'g', 'h', 'j', 'k', 'm', 'n', 'p', 'q', 'r',
            's', 't', 'u', 'v', 'w', 'x', 'y', 'z'
    };
    /**
     * Allows lookups from a GeoHash character to its integer index value.
     */
    public final static HashMap<Character, Integer> charLookupTable =
            new HashMap<>();

    /**
     * Initialize HashMap for character to integer lookups.
     */
    static {
        for (int i = 0; i < charMap.length; ++i) {
            charLookupTable.put(charMap[i], i);
        }
    }

    public static int DEFAULT_PRECISION = 12;

    // ray-casting algorithm based on
// http://www.ecse.rpi.edu/Homepages/wrf/Research/Short_Notes/pnpoly.html
    private static boolean pip(double[] point, Coordinates[] vs) {

        double x = point[0], y = point[1];

        boolean inside = false;
        for (int i = 0, j = vs.length - 1; i < vs.length; j = i++) {
            double xi = vs[i].getLongitude(), yi = vs[i].getLatitude();
            double xj = vs[j].getLongitude(), yj = vs[j].getLatitude();

            boolean intersect = ((yi > y) != (yj > y))
                    && (x < (xj - xi) * (y - yi) / (yj - yi) + xi);
            if (intersect) inside = !inside;
        }

        return inside;
    }


    public static HashSet<String> geohashesInPolygon(Coordinates[] polygon, int precision) {

        return hashesInPoly(polygon, precision);
    }

    private static HashSet<String> hashesInPoly(Coordinates[] polygon, int precision) {
        double[] bounding = polyToBB(polygon);
        HashSet<String> allHashes = new HashSet<>();
        String rowHash = GeoHash.encode(bounding[2], bounding[1], precision);
        double[] rowBox = decode_bbox(rowHash);
        do {
            String columnHash = rowHash;
            double[] columnBox = rowBox;
            while (isWest(columnBox[1], bounding[3])) {
                int inside = inside(decode(columnHash), polygon);
                if (inside % 2 == 1) {
                    allHashes.add(columnHash);
                }
                columnHash = neighbor(columnHash, new int[]{0, 1});
                columnBox = decode_bbox(columnHash);
            }
            rowHash = neighbor(rowHash, new int[]{-1, 0});
            rowBox = decode_bbox(rowHash);

        } while (rowBox[2] > bounding[0]);

        return allHashes;
    }

    private static String neighbor(String hashstring, int[] direction) {
        Coordinate lonlat = decode(hashstring);
        double neighbor_lat = lonlat.latitude
                + direction[0] * lonlat.getLaterr() * 2;

        double neighbor_lon = lonlat.getLongitude()
                + direction[1] * lonlat.getLonerr() * 2;
        return GeoHash.encode(neighbor_lat, neighbor_lon, hashstring.length());
    }


    private static int inside(Coordinate point, Coordinates[] polygon) {
        int inside = 0;

        inside += pip(new double[]{point.getLongitude(), point.getLatitude()}, polygon) ? 1 : 0;

        return inside % 2;
    }

    private static Coordinate decode(String hash_string) {
        double[] bbox = decode_bbox(hash_string);
        double lat = (bbox[0] + bbox[2]) / 2;
        double lon = (bbox[1] + bbox[3]) / 2;
        double laterr = bbox[2] - lat;
        double lonerr = bbox[3] - lon;
        return new Coordinate(lat, lon, laterr, lonerr);
    }


    /**
     * determine if lon1 is west of lon2
     * returns boolean
     */
    private static boolean isWest(double lon1, double lon2) {
        return (lon1 < lon2 && lon2 - lon1 < 180) || (lon1 > lon2 && lon2 - lon1 + 360 < 180);
    }


    private static double[] decode_bbox(String hash_string) {
        hash_string = hash_string.toLowerCase();
        boolean islon = true;
        double maxlat = 90, minlat = -90;
        double maxlon = 180;
        double minlon = -180;

        int hash_value;
        for (int i = 0, l = hash_string.length(); i < l; i++) {
            char code = hash_string.charAt(i);
            hash_value = charLookupTable.get(code);

            for (int bits = 4; bits >= 0; bits--) {
                int bit = (hash_value >> bits) & 1;
                if (islon) {
                    double mid = (maxlon + minlon) / 2;
                    if (bit == 1) {
                        minlon = mid;
                    } else {
                        maxlon = mid;
                    }
                } else {
                    double mid = (maxlat + minlat) / 2;
                    if (bit == 1) {
                        minlat = mid;
                    } else {
                        maxlat = mid;
                    }
                }
                islon = !islon;
            }
        }
        return new double[]{minlat, minlon, maxlat, maxlon};
    }


    private static double[] polyToBB(Coordinates[] polygon) {
        double minLat = Double.MAX_VALUE, minLon = minLat, maxLat = -minLat, maxLon = -minLat;
        for (Coordinates p : polygon) {
            minLat = Math.min(minLat, p.getLatitude());

            minLon = Math.min(minLon, p.getLongitude());
            maxLat = Math.max(maxLat, p.getLatitude());
            maxLon = Math.max(maxLon, p.getLongitude());
        }
        return new double[]{minLat, minLon, maxLat, maxLon};
    }
}
