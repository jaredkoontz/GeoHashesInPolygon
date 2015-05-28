import java.util.HashMap;
import java.util.HashSet;


public class PolygonGeoHash {
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
    private static boolean pip(double[] point, double[][] vs) {

        double x = point[0], y = point[1];

        boolean inside = false;
        for (int i = 0, j = vs.length - 1; i < vs.length; j = i++) {
            double xi = vs[i][0], yi = vs[i][1];
            double xj = vs[j][0], yj = vs[j][1];

            boolean intersect = ((yi > y) != (yj > y))
                    && (x < (xj - xi) * (y - yi) / (yj - yi) + xi);
            if (intersect) inside = !inside;
        }

        return inside;
    }



    public static HashSet<String> geohashPoly(double[][] polygon, int precision) {

        HashSet<String> hashList = hashesInPoly(polygon, precision);


//
//        hashList = hashList.filter(function (elem, pos, arr) {
//            return arr.indexOf(elem) === pos;
//        });

        return hashList;
    }

    private static HashSet<String> hashesInPoly(double[][] polygon, int precision) {
        double[] bounding = polyToBB(polygon);
        HashSet<String> allHashes = new HashSet<>();
        String rowHash = encode(bounding[2], bounding[1], precision);
        double[] rowBox = decode_bbox(rowHash);

        do {


            String columnHash = rowHash;
            double[] columnBox = rowBox;
            while (isWest(columnBox[1], bounding[3])) {
                int inside = inside(decode(columnHash), polygon);
                if(inside%2==1) {
                    allHashes.add(columnHash);
                }
                columnHash = neighbor(columnHash, new int[]{0, 1});
                columnBox = decode_bbox(columnHash);
            }
            rowHash = neighbor(rowHash,new int[]{-1, 0});
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
        return encode(neighbor_lat, neighbor_lon, hashstring.length());
    }


    /**
     * Encode latitude and longitude into a GeoHash string.
     *
     * @param latitude  Latitude coordinate, in degrees.
     * @param longitude Longitude coordinate, in degrees.
     * @param precision Number of characters in the returned GeoHash String.
     *                  More characters is more precise.
     * @return resulting GeoHash String.
     */
    public static String encode(double latitude, double longitude,
                                int precision) {
        /* Set up 2-element arrays for longitude and latitude that we can flip
         * between while encoding */
        double[] high = new double[2];
        double[] low = new double[2];
        double[] value = new double[2];

        high[0] = LONGITUDE_RANGE;
        high[1] = LATITUDE_RANGE;
        low[0] = -LONGITUDE_RANGE;
        low[1] = -LATITUDE_RANGE;
        value[0] = longitude;
        value[1] = latitude;

        String hash = "";

        for (int p = 0; p < precision; ++p) {

            double middle;
            int charBits = 0;
            for (int b = 0; b < BITS_PER_CHAR; ++b) {
                int bit = (p * BITS_PER_CHAR) + b;

                charBits <<= 1;

                middle = (high[bit % 2] + low[bit % 2]) / 2;
                if (value[bit % 2] > middle) {
                    charBits |= 1;
                    low[bit % 2] = middle;
                } else {
                    high[bit % 2] = middle;
                }
            }

            hash += charMap[charBits];
        }

        return hash;
    }

    private static int inside(Coordinate point, double[][] polygon) {
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
        return new Coordinate(lat, lon,laterr,lonerr);
    }


    /**
     * determine if lon1 is west of lon2
     * returns boolean
     **/
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


    private static double[] polyToBB(double[][] polygon) {
        double minLat = Double.MAX_VALUE, minLon = minLat, maxLat = -minLat, maxLon = -minLat;
        for (double[] p : polygon) {
            minLat = Math.min(minLat, p[1]);
            minLon = Math.min(minLon, p[0]);
            maxLat = Math.max(maxLat, p[1]);
            maxLon = Math.max(maxLon, p[0]);
        }
        return new double[]{minLat, minLon, maxLat, maxLon};
    }
}
