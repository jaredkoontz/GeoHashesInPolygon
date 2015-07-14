import coords.Coordinates;
import geohash.LongestEnclosingGeoHash;
import org.junit.Test;

import static junit.framework.Assert.assertTrue;

public class LongestEnclosingGeoHashTest {

    @Test
    public void test0Character() {
        Coordinates[] us = {
                new Coordinates(28.22697003891834f, -123.662109375f),
                new Coordinates(50.42951794712287f, -123.662109375f),
                new Coordinates(50.42951794712287f, -90.04394531249999f),
                new Coordinates(28.22697003891834f, -90.04394531249999f)
        };

        int longestEnclosingHashLength = LongestEnclosingGeoHash.longestEnclosingHash(us).length();
        assertTrue(longestEnclosingHashLength == 0);
    }

    @Test
    public void test1Character() {
        Coordinates[] wyomingCo = {
                new Coordinates(44.99588261816546f, -109.248046875f),
                new Coordinates(38.238180119798635f, -109.1162109375f),
                new Coordinates(38.41055825094609f, -102.83203125f)
        };

        String longestEnclosingHash = LongestEnclosingGeoHash.longestEnclosingHash(wyomingCo);
        assertTrue(longestEnclosingHash.length() == 1);
        assertTrue(longestEnclosingHash.equals("9"));
    }

    @Test
    public void test2Character() {
        Coordinates[] noCo = {
                new Coordinates(39.56758783088903f, -106.1444091796875f),
                new Coordinates(40.942564441333296f, -106.1444091796875f),
                new Coordinates(40.942564441333296f, -103.88671875f),
                new Coordinates(39.56758783088903f, -103.88671875f)
        };

        String longestEnclosingHash = LongestEnclosingGeoHash.longestEnclosingHash(noCo);
        assertTrue(longestEnclosingHash.length() == 2);
        assertTrue(longestEnclosingHash.equals("9x"));
    }


    @Test
    public void test3Character() {
        Coordinates[] weird = {
                new Coordinates(40.6639728763869f, -105.0567626953125f),
                new Coordinates(40.49918094806632f, -105.38360595703125f),
                new Coordinates(40.0717663466261f, -104.4854736328125f),
                new Coordinates(40.29419163838167f, -104.47448730468749f),
                new Coordinates(40.30466538259176f, -104.765625f),
                new Coordinates(40.47202439692057f, -104.6392822265625f),
                new Coordinates(40.46575594018434f, -104.94415283203125f)
        };

        String longestEnclosingHash = LongestEnclosingGeoHash.longestEnclosingHash(weird);
        assertTrue(longestEnclosingHash.length() == 3);
        assertTrue(longestEnclosingHash.equals("9xj"));
    }

    @Test
    public void test4Character() {
    }


}
