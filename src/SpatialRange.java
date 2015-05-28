/*
Copyright (c) 2013, Colorado State University
All rights reserved.

Redistribution and use in source and binary forms, with or without modification,
are permitted provided that the following conditions are met:

1. Redistributions of source code must retain the above copyright notice, this
   list of conditions and the following disclaimer.
2. Redistributions in binary form must reproduce the above copyright notice,
   this list of conditions and the following disclaimer in the documentation
   and/or other materials provided with the distribution.

This software is provided by the copyright holders and contributors "as is" and
any express or implied warranties, including, but not limited to, the implied
warranties of merchantability and fitness for a particular purpose are
disclaimed. In no event shall the copyright holder or contributors be liable for
any direct, indirect, incidental, special, exemplary, or consequential damages
(including, but not limited to, procurement of substitute goods or services;
loss of use, data, or profits; or business interruption) however caused and on
any theory of liability, whether in contract, strict liability, or tort
(including negligence or otherwise) arising in any way out of the use of this
software, even if advised of the possibility of such damage.
*/

public class SpatialRange {
    private float upperLat;
    private float lowerLat;
    private float upperLon;
    private float lowerLon;

    private boolean hasElevation;
    private float upperElevation;
    private float lowerElevation;

    public SpatialRange(float lowerLat, float upperLat,
                        float lowerLon, float upperLon) {
        this.lowerLat = lowerLat;
        this.upperLat = upperLat;
        this.lowerLon = lowerLon;
        this.upperLon = upperLon;

        hasElevation = false;
    }

    public SpatialRange(float lowerLat, float upperLat,
                        float lowerLon, float upperLon,
                        float upperElevation, float lowerElevation) {
        this.lowerLat = lowerLat;
        this.upperLat = upperLat;
        this.lowerLon = lowerLon;
        this.upperLon = upperLon;

        hasElevation = true;
        this.upperElevation = upperElevation;
        this.lowerElevation = lowerElevation;
    }

    public SpatialRange(SpatialRange copyFrom) {
        this.lowerLat = copyFrom.lowerLat;
        this.upperLat = copyFrom.upperLat;
        this.lowerLon = copyFrom.lowerLon;
        this.upperLon = copyFrom.upperLon;

        this.hasElevation = copyFrom.hasElevation;
        this.upperElevation = copyFrom.upperElevation;
        this.lowerElevation = copyFrom.lowerElevation;
    }


    /*
     * Retrieves the smallest latitude value of this spatial range going east.
     */
    public float getLowerBoundForLatitude() {
        return lowerLat;
    }

    /*
     * Retrieves the largest latitude value of this spatial range going east.
     */
    public float getUpperBoundForLatitude() {
        return upperLat;
    }

    /*
     * Retrieves the smallest longitude value of this spatial range going south.
     */
    public float getLowerBoundForLongitude() {
        return lowerLon;
    }

    /*
     * Retrieves the largest longitude value of this spatial range going south.
     */
    public float getUpperBoundForLongitude() {
        return upperLon;
    }

    public Coordinates getCenterPoint() {
        float latDifference = upperLat - lowerLat;
        float latDistance = latDifference / 2;

        float lonDifference = upperLon - lowerLon;
        float lonDistance = lonDifference / 2;

        return new Coordinates(lowerLat + latDistance,
                lowerLon + lonDistance);
    }

    /**
     * Using the upper and lower boundaries for this spatial range, generate
     * two lat, lon points that represent the upper-left and lower-right
     * coordinates of the range.  Note that this method does not account for the
     * curvature of the earth (aka the Earth is flat).
     *
     * @return a Pair of Coordinates, with the upper-left and lower-right
     * points of this spatial range.
     */
    public Pair<Coordinates, Coordinates> get2DCoordinates() {
        return new Pair<>(
                new Coordinates(
                        this.getLowerBoundForLatitude(),
                        this.getLowerBoundForLongitude()),
                new Coordinates(
                        this.getUpperBoundForLatitude(),
                        this.getUpperBoundForLongitude()));
    }

    public boolean hasElevationBounds() {
        return hasElevation;
    }

    public float getUpperBoundForElevation() {
        return upperElevation;
    }

    public float getLowerBoundForElevation() {
        return lowerElevation;
    }

    @Override
    public String toString() {
        Pair<Coordinates, Coordinates> p = get2DCoordinates();
        return "[" + p.a + ", " + p.b + "]";
    }

}
