package polygon;

import polygon.Coordinates;

/**
 * Created by jared on 5/27/15.
 */
public class PointInPolygon {
    /**
     * ray-casting algorithm based on
     * http://www.ecse.rpi.edu/Homepages/wrf/Research/Short_Notes/pnpoly.html
     */
    public static boolean pip(double[] point, Coordinates[] vs) {

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
