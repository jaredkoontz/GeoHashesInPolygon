import java.util.HashSet;


public class Runner {

    public static void main(String[] a) {

        double[][] triangle = {
                {-109.248046875,
                        44.99588261816546},
                {-109.1162109375,
                        38.238180119798635},
                {-102.83203125,
                        38.41055825094609}};

        HashSet<String> hashList = PolygonGeoHash.geohashPoly(triangle, 3);
        System.out.println(hashList);
    }


}
