import coords.Coordinates;
import geohash.LongestEnclosingGeoHash;
import org.junit.Test;
import polygon.GeoHashesInPolygon;

import java.util.Set;

import static junit.framework.Assert.assertTrue;


public class PolygonGeoHashTest {

	@Test
	public void testTriangle() {
		Coordinates[] wyomingCo = {
				new Coordinates(44.99588261816546f, -109.248046875f),
				new Coordinates(38.238180119798635f, -109.1162109375f),
				new Coordinates(38.41055825094609f, -102.83203125f)
		};

		int longestEnclosingHashLength = LongestEnclosingGeoHash.longestEnclosingHash(wyomingCo).length();
		int precision = longestEnclosingHashLength + 2;
		Set<String> hashes = GeoHashesInPolygon.geohashesInPolygon(wyomingCo, precision);
		assertTrue(hashes.size() == 15);
	}


	@Test
	public void testLargeSquare() {
		Coordinates[] us = {
				new Coordinates(28.22697003891834f, -123.662109375f),
				new Coordinates(50.42951794712287f, -123.662109375f),
				new Coordinates(50.42951794712287f, -90.04394531249999f),
				new Coordinates(28.22697003891834f, -90.04394531249999f)
		};

		int longestEnclosingHashLength = LongestEnclosingGeoHash.longestEnclosingHash(us).length();
		int precision = longestEnclosingHashLength + 2;
		Set<String> hashes = GeoHashesInPolygon.geohashesInPolygon(us, precision);
		assertTrue(hashes.size() == 12);
	}


	@Test
	public void testSmallSquare() {
		Coordinates[] noCo = {
				new Coordinates(39.56758783088903f, -106.1444091796875f),
				new Coordinates(40.942564441333296f, -106.1444091796875f),
				new Coordinates(40.942564441333296f, -103.88671875f),
				new Coordinates(39.56758783088903f, -103.88671875f)
		};

		int longestEnclosingHashLength = LongestEnclosingGeoHash.longestEnclosingHash(noCo).length();
		int precision = longestEnclosingHashLength + 2;
		Set<String> hashes = GeoHashesInPolygon.geohashesInPolygon(noCo, precision);
		assertTrue(hashes.size() == 48);
	}

	@Test
	public void testWeirdPolygon() {
		Coordinates[] weird = {
				new Coordinates(40.6639728763869f, -105.0567626953125f),
				new Coordinates(40.49918094806632f, -105.38360595703125f),
				new Coordinates(40.0717663466261f, -104.4854736328125f),
				new Coordinates(40.29419163838167f, -104.47448730468749f),
				new Coordinates(40.30466538259176f, -104.765625f),
				new Coordinates(40.47202439692057f, -104.6392822265625f),
				new Coordinates(40.46575594018434f, -104.94415283203125f)
		};

		int longestEnclosingHashLength = LongestEnclosingGeoHash.longestEnclosingHash(weird).length();
		int precision = longestEnclosingHashLength + 2;
		Set<String> hashes = GeoHashesInPolygon.geohashesInPolygon(weird, precision);
		assertTrue(hashes.size() == 92);
	}


}
