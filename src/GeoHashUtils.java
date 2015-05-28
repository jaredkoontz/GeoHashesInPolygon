/**
 * Copyright (c) 2012, Jilles van Gurp
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 *
 *
 * Modified by koontz
 *
 */

import java.util.HashMap;
import java.util.Map;


public class GeoHashUtils {

    final static Map<Character, Integer> BASE32_DECODE_MAP = new HashMap<>();

    static int DEFAULT_PRECISION = 12;

    static int[] BITS = {16, 8, 4, 2, 1};
    // note: no a,i,l, and o
    static char[] BASE32_CHARS = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'j', 'k', 'm', 'n', 'p', 'q',
            'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z'};

    static {
        for (int i = 0; i < BASE32_CHARS.length; i++) {
            BASE32_DECODE_MAP.put(BASE32_CHARS[i], i);
        }
    }


    /**
     * todo
     * @param hash_string
     * @return
     */
    public static CoordinateWithError decode(String hash_string) {
        double[] bbox = GeoHash.decode_bbox(hash_string);
        double lat = (bbox[0] + bbox[2]) / 2;
        double lon = (bbox[1] + bbox[3]) / 2;
        double laterr = bbox[2] - lat;
        double lonerr = bbox[3] - lon;
        return new CoordinateWithError(lat, lon, laterr, lonerr);
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
     * @param polygon
     * @return
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

}
