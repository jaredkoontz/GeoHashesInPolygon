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


import static java.lang.Math.*;


/**
 * The methods in this class provides methods that may be used to manipulate geometric shapes. The methods follow the
 * GeoJson http://geojson.org/ convention of expressing shapes as multi dimensional arrays of points.
 * <p/>
 * Following this convention means there is no need for an object oriented framework to represent the different shapes.
 * Consequently, all of the methods in this framework are static methods. This makes usage of these methods very
 * straightforward and also makes it easy to integrate with the many frameworks out there that provide their own object
 * oriented abstractions.
 * <p/>
 * So, a point is an array with the coordinate pair. A line (or line string) is a 2d array of points. A polygon is a 3d
 * array that consists of an outer polygon and zero or more inner polygons (holes). Each 2d array should be a closed
 * linear ring where the last point is the same as the first point.
 * <p/>
 * Finally, 4d arrays can be used to express multipolygons of one or more polygons (each with their own holes).
 * <p/>
 * It should be noted that GeoJson represents points as arrays of [longitude, latitude] rather than the conventional way
 * of latitude followed by longitude.
 * <p/>
 * It should also be noted that this class contains several methods that treat 2d arrays as polygons.
 */
public class GeoGeometry {

    /**
     * Earth's mean radius, in meters.
     */
    private static final double EARTH_RADIUS = 6371000.0;

//    private static final double EARTH_CIRCUMFERENCE_METERS = EARTH_RADIUS_METERS * Math.PI * 2.0;
//    private static final double DEGREE_LATITUDE_METERS = EARTH_RADIUS_METERS * Math.PI / 180.0;


    /**
     * Compute the Haversine distance between the two coordinates. Haversine is
     * one of several distance calculation algorithms that exist. It is not very
     * precise in the sense that it assumes the earth is a perfect sphere, which
     * it is not. This means precision drops over larger distances. According to
     * http://en.wikipedia.org/wiki/Haversine_formula there is a 0.5% error
     * margin given the 1% difference in curvature between the equator and the
     * poles.
     *
     * @param lat1  the latitude in decimal degrees
     * @param long1 the longitude in decimal degrees
     * @param lat2  the latitude in decimal degrees
     * @param long2 the longitude in decimal degrees
     * @return the distance in meters
     */
    public static double distance(final double lat1, final double long1, final double lat2, final double long2) {
        validate(lat1, long1, false);
        validate(lat2, long2, false);

        final double deltaLat = toRadians(lat2 - lat1);
        final double deltaLon = toRadians(long2 - long1);

        final double a = sin(deltaLat / 2) * sin(deltaLat / 2) + cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) * sin(deltaLon / 2) * sin(deltaLon / 2);

        final double c = 2 * asin(Math.sqrt(a));

        return EARTH_RADIUS * c;
    }

    /**
     * Validates coordinates. Note. because of some edge cases at the extremes that I've encountered in several data sources, I've built in
     * a small tolerance for small rounding errors that allows e.g. 180.00000000000023 to validate.
     *
     * @param latitude  latitude between -90.0 and 90.0
     * @param longitude longitude between -180.0 and 180.0
     * @param strict    if false, it will allow for small rounding errors. If true, it will not.
     * @throws IllegalArgumentException if the lat or lon is out of the allowed range.
     */
    public static void validate(double latitude, double longitude, boolean strict) {
        double roundedLat = latitude;
        double roundedLon = longitude;
        if (!strict) {
            // this gets rid of rounding errors e.g. 180.00000000000023 will validate
            roundedLat = Math.round(latitude * 1000000) / 1000000.0;
            roundedLon = Math.round(longitude * 1000000) / 1000000.0;
        }
        if (roundedLat < -90.0 || roundedLat > 90.0) {
            throw new IllegalArgumentException("Latitude " + latitude + " is outside legal range of -90,90");
        }
        if (roundedLon < -180.0 || roundedLon > 180.0) {
            throw new IllegalArgumentException("Longitude " + longitude + " is outside legal range of -180,180");
        }
    }
}