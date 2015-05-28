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
    private static final double EARTH_RADIUS_METERS = 6371000.0;

//    private static final double EARTH_CIRCUMFERENCE_METERS = EARTH_RADIUS_METERS * Math.PI * 2.0;
//    private static final double DEGREE_LATITUDE_METERS = EARTH_RADIUS_METERS * Math.PI / 180.0;


    /**
     * @param lineString line
     * @return bounding box that contains the lineString as a double array of
     * [minLat,maxLat,minLon,maxLon}
     */
    public static double[] boundingBox(double[][] lineString) {
        double minLat = Integer.MAX_VALUE;
        double minLon = Integer.MAX_VALUE;
        double maxLat = Integer.MIN_VALUE;
        double maxLon = Integer.MIN_VALUE;

        for (double[] aLineString : lineString) {
            minLat = min(minLat, aLineString[1]);
            minLon = min(minLon, aLineString[0]);
            maxLat = max(maxLat, aLineString[1]);
            maxLon = max(maxLon, aLineString[0]);
        }

        return new double[]{minLat, maxLat, minLon, maxLon};
    }


    /**
     * @param bbox      double array of [minLat,maxLat,minLon,maxLon}
     * @param latitude  latitude
     * @param longitude longitude
     * @return true if the latitude and longitude are contained in the bbox
     */
    public static boolean bboxContains(double[] bbox, double latitude, double longitude) {
        validate(latitude, longitude, false);
        return bbox[0] <= latitude && latitude <= bbox[1] && bbox[2] <= longitude && longitude <= bbox[3];
    }


    /**
     * Determine whether a point is contained in a polygon. Note, technically
     * the points that make up the polygon are not contained by it.
     *
     * @param latitude      latitude
     * @param longitude     longitude
     * @param polygonPoints polygonPoints points that make up the polygon as arrays of
     *                      [longitude,latitude]
     * @return true if the polygon contains the coordinate
     */
    public static boolean polygonContains(double latitude, double longitude, double[]... polygonPoints) {
        validate(latitude, longitude, false);

        if (polygonPoints.length <= 2) {
            throw new IllegalArgumentException("a polygon must have at least three points");
        }

        double[] bbox = boundingBox(polygonPoints);
        if (!bboxContains(bbox, latitude, longitude)) {
            // outside the containing bbox
            return false;
        }

        int hits = 0;

        double lastLatitude = polygonPoints[polygonPoints.length - 1][1];
        double lastLongitude = polygonPoints[polygonPoints.length - 1][0];
        double currentLatitude, currentLongitude;

        // Walk the edges of the polygon
        for (int i = 0; i < polygonPoints.length; lastLatitude = currentLatitude, lastLongitude = currentLongitude, i++) {
            currentLatitude = polygonPoints[i][1];
            currentLongitude = polygonPoints[i][0];

            if (currentLongitude == lastLongitude) {
                continue;
            }

            double leftLatitude;
            if (currentLatitude < lastLatitude) {
                if (latitude >= lastLatitude) {
                    continue;
                }
                leftLatitude = currentLatitude;
            } else {
                if (latitude >= currentLatitude) {
                    continue;
                }
                leftLatitude = lastLatitude;
            }

            double test1, test2;
            if (currentLongitude < lastLongitude) {
                if (longitude < currentLongitude || longitude >= lastLongitude) {
                    continue;
                }
                if (latitude < leftLatitude) {
                    hits++;
                    continue;
                }
                test1 = latitude - currentLatitude;
                test2 = longitude - currentLongitude;
            } else {
                if (longitude < lastLongitude || longitude >= currentLongitude) {
                    continue;
                }
                if (latitude < leftLatitude) {
                    hits++;
                    continue;
                }
                test1 = latitude - lastLatitude;
                test2 = longitude - lastLongitude;
            }

            if (test1 < test2 / (lastLongitude - currentLongitude) * (lastLatitude - currentLatitude)) {
                hits++;
            }
        }

        return (hits & 1) != 0;
    }


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
     * Converts a circle to a polygon.
     * This method does not behave very well very close to the poles because the math gets a little funny there.
     *
     * @param segments  number of segments the polygon should have. The higher this
     *                  number, the better of an approximation the polygon is for the
     *                  circle.
     * @param latitude  latitude
     * @param longitude longitude
     * @param radius    radius of the circle
     * @return a linestring polygon
     */
    public static double[][] circle2polygon(int segments, double latitude, double longitude, double radius) {
        validate(latitude, longitude, false);

        if (segments < 5) {
            throw new IllegalArgumentException("you need a minimum of 5 segments");
        }
        double[][] points = new double[segments + 1][0];

        double relativeLatitude = radius / EARTH_RADIUS_METERS * 180 / PI;

        // things get funny near the north and south pole, so doing a modulo 90
        // to ensure that the relative amount of degrees doesn't get too crazy.
        double relativeLongitude = relativeLatitude / cos(Math.toRadians(latitude)) % 90;

        for (int i = 0; i < segments; i++) {
            // radians go from 0 to 2*PI; we want to divide the circle in nice
            // segments
            double theta = 2 * PI * i / segments;
            // trying to avoid theta being exact factors of pi because that results in some funny behavior around the
            // north-pole
//            theta = theta += 0.1;
            theta += 0.1;
            if (theta >= 2 * PI) {
                theta = theta - 2 * PI;
            }

            // on the unit circle, any point of the circle has the coordinate
            // cos(t),sin(t) where t is the radian. So, all we need to do that
            // is multiply that with the relative latitude and longitude
            // note, latitude takes the role of y, not x. By convention we
            // always note latitude, longitude instead of the other way around
            double latOnCircle = latitude + relativeLatitude * Math.sin(theta);
            double lonOnCircle = longitude + relativeLongitude * Math.cos(theta);
            if (lonOnCircle > 180) {
                lonOnCircle = -180 + (lonOnCircle - 180);
            } else if (lonOnCircle < -180) {
                lonOnCircle = 180 - (lonOnCircle + 180);
            }

            if (latOnCircle > 90) {
                latOnCircle = 90 - (latOnCircle - 90);
            } else if (latOnCircle < -90) {
                latOnCircle = -90 - (latOnCircle + 90);
            }

            points[i] = new double[]{lonOnCircle, latOnCircle};
        }
        // should end with same point as the origin
        points[points.length - 1] = new double[]{points[0][0], points[0][1]};
        return points;
    }


    /**
     * Validates coordinates. Note. because of some edge cases at the extremes that I've encountered in several data sources, I've built in
     * a small tolerance for small rounding errors that allows e.g. 180.00000000000023 to validate.
     *
     * @param latitude  latitude between -90.0 and 90.0
     * @param longitude longitude between -180.0 and 180.0
     * @throws IllegalArgumentException if the lat or lon is out of the allowed range.
     */
    public static void validate(double latitude, double longitude) {
        validate(latitude, longitude, false);
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

    /**
     * @param point point
     */
    public static void validate(double[] point) {
        validate(point[1], point[0], false);
    }
}