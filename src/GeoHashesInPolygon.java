import java.util.HashMap;
import java.util.HashSet;


public class GeoHashesInPolygon {

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




    public static HashSet<String> geohashesInPolygon(Coordinates[] polygon, int precision) {
        return hashesInPoly(polygon, precision);
    }

    public static HashSet<String> hashesInPoly(Coordinates[] polygon){
        int longestEnclosingHashLength = EnclosingGeoHash.longestEnclosingHash(polygon).length();
        int precision = longestEnclosingHashLength + 2;
        return geohashesInPolygon(polygon, precision);
    }


    private static HashSet<String> hashesInPoly(Coordinates[] polygon, int precision) {
        double[] bounding = GeoHashUtils.polyToBB(polygon);
        HashSet<String> allHashes = new HashSet<>();
        String rowHash = GeoHash.encode(bounding[2], bounding[1], precision);
        double[] rowBox = GeoHash.decode_bbox(rowHash);
        do {
            String columnHash = rowHash;
            double[] columnBox = rowBox;
            while (GeoHashUtils.isWest(columnBox[1], bounding[3])) {
                int inside = inside(GeoHashUtils.decode(columnHash), polygon);
                if (inside % 2 == 1) {
                    allHashes.add(columnHash);
                }
                columnHash = neighbor(columnHash, new int[]{0, 1});
                columnBox = GeoHash.decode_bbox(columnHash);
            }
            rowHash = neighbor(rowHash, new int[]{-1, 0});
            rowBox = GeoHash.decode_bbox(rowHash);

        } while (rowBox[2] > bounding[0]);

        return allHashes;
    }

    private static String neighbor(String hash, int[] direction) {
        CoordinateWithError lonlat = GeoHashUtils.decode(hash);
        double neighbor_lat = lonlat.latitude
                + direction[0] * lonlat.getError().getLat() * 2;

        double neighbor_lon = lonlat.getLongitude()
                + direction[1] * lonlat.getError().getLon() * 2;

        return GeoHash.encode(neighbor_lat, neighbor_lon, hash.length());
    }


    private static int inside(CoordinateWithError point, Coordinates[] polygon) {
        int inside = 0;

        inside += PointInPolygon.pip(new double[]{point.getLongitude(), point.getLatitude()}, polygon) ? 1 : 0;

        return inside % 2;
    }

}
