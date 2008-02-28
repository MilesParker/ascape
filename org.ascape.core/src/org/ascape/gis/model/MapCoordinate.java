/*
 * Copyright 1998-2007 The Brookings Institution, NuTech Solutions,Inc., Metascape LLC, and contributors. 
 * All rights reserved.
 * This program and the accompanying materials are made available solely under of the BSD license "ascape-license.txt".
 * Any referenced or included libraries carry licenses of their respective copyright holders. 
 */


package org.ascape.gis.model;


/**
 * A location on a map
 *
 * @author    Miles Parker, Josh Miller, and others
 * @created   September-November, 2001
 */
public class MapCoordinate extends org.ascape.model.space.Coordinate2DContinuous {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;


    /**
     * Constructs an instance of MapCoordinate.
     */
    public MapCoordinate() {
        super(0.0, 0.0);
    }


    /**
     * Constructs an instance of MapCoordinate. Warning: this contructor expects
     * to have coordiantes supplied in latitude, longitude order. This is the
     * opposite of the typical x, y ordering, that is, the vertical (y) value is
     * specified first, in keeping with cartological practice.
     *
     * @param lat  parameter
     * @param lon  parameter
     */
    public MapCoordinate(double lat, double lon) {
        super(lon, lat);
    }


    /**
     * Constructs an instance of MapCoordinate.
     *
     * @param coordinate  parameter
     */
    public MapCoordinate(MapCoordinate coordinate) {
        super(coordinate.getLongitude(), coordinate.getLatitude());
    }


    /**
     * Returns true if two MapCoordinates are equal
     *
     * @param other  The other MapCoordinate
     * @return      True if the two are equal
     */
    public boolean equals(MapCoordinate other) {
        if ((this.getLatitude() == other.getLatitude()) && (this.getLongitude() == other.getLongitude())) {
            return true;
        }
        return false;
    }


    /**
     * Return a String of the Latitude and Longitude of the MapCoordinate
     *
     * @return a String
     */
    public String toString() {
        StringBuffer s = new StringBuffer("Lat: ");
        s.append((new Double(getLatitude())).toString());
        s.append(", Lon: ");
        s.append((new Double(getLongitude())).toString());
        return s.toString();
    }


    /**
     * Gets the latitude for the MapCoordinate object.
     *
     * @return   the latitude
     */
    public double getLatitude() {
        return getYValue();
    }


    /**
     * Gets the longitude for the MapCoordinate object.
     *
     * @return   the longitude
     */
    public double getLongitude() {
        return getXValue();
    }


    /**
     * Sets latLong for the MapCoordinate object.
     *
     * @param lat  the latLong
     * @param lon  the latLong
     */
    public void setLatLong(double lat, double lon) {
        setYValue(lat);
        setXValue(lon);
    }


    /**
     * Sets latitude for the MapCoordinate object.
     *
     * @param lat  the latitude
     */
    public void setLatitude(double lat) {
        setYValue(lat);
    }


    /**
     * Sets longitude for the MapCoordinate object.
     *
     * @param lon  the longitude
     */
    public void setLongitude(double lon) {
        setXValue(lon);
    }
}
