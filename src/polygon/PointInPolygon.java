package polygon;

import coords.Coordinates;


public class PointInPolygon {
    /**
     * ray-casting algorithm based on
     * <a href="http://www.ecse.rpi.edu/Homepages/wrf/Research/Short_Notes/pnpoly.html">...</a>
     */
    public static boolean pointInPolygon(double[] point, Coordinates[] vs) {

        double x = point[0], y = point[1];

        boolean inside = false;
        for (int i = 0, j = vs.length - 1; i < vs.length; j = i++) {
            double xi = vs[i].getLongitude(), yi = vs[i].getLatitude();
            double xj = vs[j].getLongitude(), yj = vs[j].getLatitude();

            boolean intersect = ((yi > y) != (yj > y))
                    && (x < (xj - xi) * (y - yi) / (yj - yi) + xi);
            if (intersect) inside = !inside;
        }

        return inside;
    }

}
