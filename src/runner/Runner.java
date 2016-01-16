package runner;


import coords.Coordinates;
import geohash.LongestEnclosingGeoHash;
import polygon.GeoHashesInPolygon;

import java.util.Set;

/**
 * todo
 */
public class Runner {

	/**
	 * todo
	 */
	public static void main(String[] a) {

		//sample polygon, representing a triangle over northern colorado and some of wyoming
		Coordinates[] polygon = {
				new Coordinates(44.99588261816546f, -109.248046875f),
				new Coordinates(38.238180119798635f, -109.1162109375f),
				new Coordinates(38.41055825094609f, -102.83203125f)
		};

		int longestEnclosingHashLength = LongestEnclosingGeoHash.longestEnclosingHash(polygon).length();
		int precision = longestEnclosingHashLength + 2;
		Set<String> hashes = GeoHashesInPolygon.geohashesInPolygon(polygon, precision);
		System.out.println(hashes);
		System.out.println(hashes.size());
	}

}
