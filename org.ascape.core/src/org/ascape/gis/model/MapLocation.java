/*
 * Copyright 1998-2007 The Brookings Institution, NuTech Solutions,Inc., Metascape LLC, and contributors. 
 * All rights reserved.
 * This program and the accompanying materials are made available solely under of the BSD license "ascape-license.txt".
 * Any referenced or included libraries carry licenses of their respective copyright holders. 
 */
package org.ascape.gis.model;

import com.bbn.openmap.layer.location.Location;
import com.bbn.openmap.omGraphics.OMGraphic;

/**
 * Contains the continuous space Location information for the MapAgent object.
 *
 * @author    Miles Parker, Josh Miller, and others
 * @created   September-November, 2001
 */

public class MapLocation extends Location {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;


    /**
     * Constructs an instance of MapLocation.
     *
     * @param lat             the new MapLocation's latitude
     * @param lon             the new MapLocation's longitude
     * @param name            the new MapLocation's name
     * @param locationMarker  the new MapLocation's locationMarker
     */
    public MapLocation(float lat, float lon, String name, OMGraphic locationMarker) {
        super(lat, lon, name, locationMarker);
    }


    /**
     * Sets the graphicLocations attribute of the MapLocation object
     *
     * @param lat  the latitude
     * @param lon  the longitude
     */
    public void setGraphicLocations(float lat, float lon) {
        super.lat = lat;
        super.lon = lon;
    }


    /**
     * Sets the graphicLocations attribute of the MapLocation object
     *
     * @param f
     * @param f2
     * @param i   the latitude as x
     * @param j   the longitude as y
     */
    public void setGraphicLocations(float f, float f2, int i, int j) {
        throw new RuntimeException("Unintended call to mapLocation.setLocation.");
    }


    /**
     * Sets the graphicLocations attribute of the MapLocation object
     *
     * @param i   The new graphicLocations value
     * @param i2  The new graphicLocations value
     */
    public void setGraphicLocations(int i, int i2) {
        throw new RuntimeException("Unintended call to mapLocation.setLocation.");
    }
}