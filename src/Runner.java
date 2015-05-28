import java.util.Set;


public class Runner {

    public static void main(String[] a) {
        Coordinates[] wyomingCo = {
                new Coordinates(44.99588261816546f, -109.248046875f),
                new Coordinates(38.238180119798635f, -109.1162109375f),
                new Coordinates(38.41055825094609f, -102.83203125f)
        };

        int longestEnclosingHashLength = EnclosingGeoHash.longestEnclosingHash(wyomingCo).length();
        int precision = longestEnclosingHashLength + 2;
        Set<String> hashes = GeoHashesInPolygon.geohashesInPolygon(wyomingCo, precision);
        System.out.println(hashes);
        System.out.println(hashes.size());
    }


}
