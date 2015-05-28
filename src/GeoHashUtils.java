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
     * @param geohash valid geohash
     * @return double array representing the bounding box for the geohash of [south latitude, north latitude, west
     * longitude, east longitude]
     */
    public static double[] decode_bbox(String geohash) {
        double[] latInterval = {-90.0, 90.0};
        double[] lonInterval = {-180.0, 180.0};

        boolean isEven = true;
        for (int i = 0; i < geohash.length(); i++) {

            int currentCharacter = BASE32_DECODE_MAP.get(geohash.charAt(i));

            for (int mask : BITS) {
                if (isEven) {
                    if ((currentCharacter & mask) != 0) {
                        lonInterval[0] = (lonInterval[0] + lonInterval[1]) / 2;
                    } else {
                        lonInterval[1] = (lonInterval[0] + lonInterval[1]) / 2;
                    }

                } else {

                    if ((currentCharacter & mask) != 0) {
                        latInterval[0] = (latInterval[0] + latInterval[1]) / 2;
                    } else {
                        latInterval[1] = (latInterval[0] + latInterval[1]) / 2;
                    }
                }
                isEven = !isEven;
            }
        }

        return new double[]{latInterval[0], latInterval[1], lonInterval[0], lonInterval[1]};
    }


    /**
     * @param geoHash geohash
     * @return the geo hash of the same length directly south of the bounding box.
     */
    public static String south(String geoHash) {
        double[] bbox = decode_bbox(geoHash);
        double latDiff = bbox[1] - bbox[0];
        double lat = bbox[0] - latDiff / 2;
        double lon = (bbox[2] + bbox[3]) / 2;
        return GeoHash.encode(lat, lon, geoHash.length());
    }

    /**
     * @param geoHash geohash
     * @return the geo hash of the same length directly north of the bounding box.
     */
    public static String north(String geoHash) {
        double[] bbox = decode_bbox(geoHash);
        double latDiff = bbox[1] - bbox[0];
        double lat = bbox[1] + latDiff / 2;
        double lon = (bbox[2] + bbox[3]) / 2;
        return GeoHash.encode(lat, lon, geoHash.length());
    }

    /**
     * @param geoHash geohash
     * @return the geo hash of the same length directly west of the bounding box.
     */
    public static String west(String geoHash) {
        double[] bbox = decode_bbox(geoHash);
        double lonDiff = bbox[3] - bbox[2];
        double lat = (bbox[0] + bbox[1]) / 2;
        double lon = bbox[2] - lonDiff / 2;
        if (lon < -180) {
            lon = 180 - (lon + 180);
        }
        if (lon > 180) {
            lon = 180;
        }

        return GeoHash.encode(lat, lon, geoHash.length());
    }

    /**
     * @param geoHash geohash
     * @return the geo hash of the same length directly east of the bounding box.
     */
    public static String east(String geoHash) {
        double[] bbox = decode_bbox(geoHash);
        double lonDiff = bbox[3] - bbox[2];
        double lat = (bbox[0] + bbox[1]) / 2;
        double lon = bbox[3] + lonDiff / 2;

        if (lon > 180) {
            lon = -180 + (lon - 180);
        }
        if (lon < -180) {
            lon = -180;
        }

        return GeoHash.encode(lat, lon, geoHash.length());
    }

    /**
     * @param geoHash   geo hash
     * @param latitude  latitude
     * @param longitude longitude
     * @return true if the coordinate is contained by the bounding box for this geo hash
     */
    public static boolean contains(String geoHash, double latitude, double longitude) {
        return GeoGeometry.bboxContains(decode_bbox(geoHash), latitude, longitude);
    }


    /**
     * @param l1 longitude
     * @param l2 longitude
     * @return true if l1 is west of l2
     */
    public static boolean isWest(double l1, double l2) {
        double ll1 = l1 + 180;
        double ll2 = l2 + 180;
        if (ll1 < ll2 && ll2 - ll1 < 180) {
            return true;
        } else if (ll1 > ll2 && ll2 + 360 - ll1 < 180) {
            return true;
        } else {
            return false;
        }
    }


}
