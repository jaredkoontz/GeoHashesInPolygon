package geohash;

import coords.Coordinates;

import java.util.HashMap;

/**
 * This class provides an implementation of the GeoHash (http://www.geohash.org)
 * algorithm.
 * <p/>
 * See http://en.wikipedia.org/wiki/Geohash for implementation details.
 * <p/>
 * created by malensek
 * edited by koontz
 */
public class GeoHash {

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
    public static int DEFAULT_PRECISION = 12;

    /**
     * Initialize HashMap for character to integer lookups.
     */
    static {
        for (int i = 0; i < charMap.length; ++i) {
            charLookupTable.put(charMap[i], i);
        }
    }

    /**
     * Encode a set of {@link coords.Coordinates} into a GeoHash string by calling the encode method with
     * the default precision
     *
     * @param coordinates polygon.Coordinates to get GeoHash for.
     */
    public static String encode(Coordinates coordinates) {
        return encode(coordinates, DEFAULT_PRECISION);
    }

    /**
     * Encode a set of {@link Coordinates} into a GeoHash string.
     *
     * @param coords    polygon.Coordinates to get GeoHash for.
     * @param precision Desired number of characters in the returned GeoHash String.  More
     *                  characters means more precision.
     * @return GeoHash string.
     */
    public static String encode(Coordinates coords, int precision) {
        return encode(coords.getLatitude(), coords.getLongitude(), precision);
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
    public static String encode(float latitude, float longitude,
                                int precision) {
        /* Set up 2-element arrays for longitude and latitude that we can flip
         * between while encoding */
        float[] high = new float[2];
        float[] low = new float[2];
        float[] value = new float[2];

        high[0] = LONGITUDE_RANGE;
        high[1] = LATITUDE_RANGE;
        low[0] = -LONGITUDE_RANGE;
        low[1] = -LATITUDE_RANGE;
        value[0] = longitude;
        value[1] = latitude;

        String hash = "";

        for (int p = 0; p < precision; ++p) {

            float middle;
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

    /**
     * helper method to call the default decode method when our input is a double, not a float
     */
    public static String encode(double lat, double lon, int length) {
        return encode((float) lat, (float) lon, length);
    }

    /**
     * Decode hashString into the bounding box that represents it
     * Data is returned as a four-element array: [minlat, minlon, maxlat, maxlon]
     */
    public static double[] decode_bbox(String hashString) {
        hashString = hashString.toLowerCase();
        boolean islon = true;
        double maxlat = 90, minlat = -90;
        double maxlon = 180;
        double minlon = -180;

        int hash_value;
        for (int i = 0, l = hashString.length(); i < l; i++) {
            char code = hashString.charAt(i);
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

}
