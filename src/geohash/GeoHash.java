package geohash;

import coords.Coordinates;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * This class provides an implementation of the GeoHash (http://www.geohash.org)
 * algorithm.

 * See http://en.wikipedia.org/wiki/Geohash for implementation details.

 * created by malensek and koontz
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

    /**
     * Initialize HashMap for character to integer lookups.
     */
    static {
        for (int i = 0; i < charMap.length; ++i) {
            charLookupTable.put(charMap[i], i);
        }
    }

    public static int DEFAULT_PRECISION = 12;

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
     * Another helper method to call the default decode method when our input is a double, not a float
     */
    public static String encode(double lat, double lon) {
        return encode((float) lat, (float) lon, DEFAULT_PRECISION);
    }

    /**
     * Convert a GeoHash String to a long integer.
     *
     * @param hash GeoHash String to convert.
     * @return The GeoHash as a long integer.
     */
    public static long hashToLong(String hash) {
        long longForm = 0;

        /* Long can fit 12 GeoHash characters worth of precision. */
        if (hash.length() > 12) {
            hash = hash.substring(0, 12);
        }

        for (char c : hash.toCharArray()) {
            longForm <<= BITS_PER_CHAR;
            longForm |= charLookupTable.get(c);
        }

        return longForm;
    }


    /**
     * Decode GeoHash bits from a binary GeoHash.
     *
     * @param bits     ArrayList of Booleans containing the GeoHash bits
     * @param latitude If set to <code>true</code> the latitude bits are decoded.  If set to
     *                 <code>false</code> the longitude bits are decoded.
     * @return low, high range that the GeoHashed location falls between.
     */
    private static float[] decodeBits(ArrayList<Boolean> bits,
                                      boolean latitude) {
        float low, high, middle;
        int offset;

        if (latitude) {
            offset = 1;
            low = -90.0f;
            high = 90.0f;
        } else {
            offset = 0;
            low = -180.0f;
            high = 180.0f;
        }

        for (int i = offset; i < bits.size(); i += 2) {
            middle = (high + low) / 2;

            if (bits.get(i)) {
                low = middle;
            } else {
                high = middle;
            }
        }

        if (latitude) {
            return new float[]{high, low};
        } else {
            return new float[]{low, high};
        }
    }

    /**
     * Converts a GeoHash string to its binary representation.
     *
     * @param hash GeoHash string to convert to binary
     * @return The GeoHash in binary form, as an ArrayList of Booleans.
     */
    private static ArrayList<Boolean> getBits(String hash) {
        hash = hash.toLowerCase();

        /* Create an array of bits, 5 bits per character: */
        ArrayList<Boolean> bits =
                new ArrayList<Boolean>(hash.length() * BITS_PER_CHAR);

        /* Loop through the hash string, setting appropriate bits. */
        for (int i = 0; i < hash.length(); ++i) {
            int charValue = charLookupTable.get(hash.charAt(i));

            /* Set bit from charValue, then shift over to the next bit. */
            for (int j = 0; j < BITS_PER_CHAR; ++j, charValue <<= 1) {
                bits.add((charValue & 0x10) == 0x10);
            }
        }
        return bits;
    }


    public static double[] decode_bbox(String hash_string) {
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

}
