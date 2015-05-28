public class EnclosingGeoHash {

    /**
     * todo
     *
     * @param polygonPoints
     * @return
     */
    public static String longestEnclosingHash(Coordinates... polygonPoints) {
        String[] hashes = new String[polygonPoints.length];
        int i = 0;
        for (Coordinates ds : polygonPoints) {
            hashes[i] = GeoHash.encode(ds);
            i++;
        }
        return longestCommonPrefix(hashes);


    }

    /**
     * todo
     *
     * @param strings
     * @return
     */
    private static String longestCommonPrefix(String[] strings) {
        if (strings.length == 0) {
            return "";   // Or maybe return null?
        }

        for (int prefixLen = 0; prefixLen < strings[0].length(); prefixLen++) {
            char c = strings[0].charAt(prefixLen);
            for (int i = 1; i < strings.length; i++) {
                if (prefixLen >= strings[i].length() || strings[i].charAt(prefixLen) != c) {
                    // Mismatch found
                    return strings[i].substring(0, prefixLen);
                }
            }
        }
        return strings[0];
    }

    /**
     * @param granularityInMeters granularity
     * @param latitude            latitude
     * @param longitude           longitude
     * @return the largest hash length where the hash bbox has a width less than granularityInMeters.
     */
    public static int suitableHashLength(double granularityInMeters, double latitude, double longitude) {
        if (granularityInMeters < 5) {
            return 10;
        }
        String hash = GeoHash.encode(latitude, longitude);
        double width = 0;
        int length = hash.length();
        // the height is the same at for any latitude given a length, but the width converges towards the poles
        while (width < granularityInMeters && hash.length() >= 2) {
            length = hash.length();
            double[] bbox = GeoHashUtils.decode_bbox(hash);
            width = GeoGeometry.distance(bbox[0], bbox[2], bbox[0], bbox[3]);
            hash = hash.substring(0, hash.length() - 1);
        }
        return Math.min(length + 1, GeoHashUtils.DEFAULT_PRECISION);
    }

}
