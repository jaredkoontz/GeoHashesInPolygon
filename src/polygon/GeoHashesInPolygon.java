package polygon;

import coords.Coordinates;
import geohash.GeoHash;
import geohash.GeoHashUtils;
import geohash.LongestEnclosingGeoHash;

import java.util.HashSet;

/**
 * todo
 */
public class GeoHashesInPolygon {

	/**
	 * todo
	 */
	public static HashSet<String> geohashesInPolygon(Coordinates[] polygon, int precision) {
		return hashesInPoly(polygon, precision);
	}

	/**
	 * todo
	 */
	public static HashSet<String> hashesInPoly(Coordinates[] polygon) {
		int longestEnclosingHashLength = LongestEnclosingGeoHash.longestEnclosingHash(polygon).length();
		int precision = longestEnclosingHashLength + 2;
		return geohashesInPolygon(polygon, precision);
	}

	/**
	 * @param polygon
	 * @param precision
	 * @return todo change all of these integers into boolean logic
	 */
	private static HashSet<String> hashesInPoly(Coordinates[] polygon, int precision) {
		double[] bounding = GeoHashUtils.polyToBB(polygon);
		HashSet<String> allHashes = new HashSet<>();
		String rowHash = GeoHash.encode(bounding[2], bounding[1], precision);
		double[] rowBox = GeoHash.decode_bbox(rowHash);
		do {
			String columnHash = rowHash;
			double[] columnBox = rowBox;
			while (GeoHashUtils.isWest(columnBox[1], bounding[3])) {
				int inside = GeoHashUtils.inside(GeoHashUtils.decodeWithError(columnHash), polygon);
				if (inside % 2 == 1) {
					allHashes.add(columnHash);
				}
				columnHash = GeoHashUtils.neighbor(columnHash, new int[]{0, 1});
				columnBox = GeoHash.decode_bbox(columnHash);
			}
			rowHash = GeoHashUtils.neighbor(rowHash, new int[]{-1, 0});
			rowBox = GeoHash.decode_bbox(rowHash);

		} while (rowBox[2] > bounding[0]);

		return allHashes;
	}


}
