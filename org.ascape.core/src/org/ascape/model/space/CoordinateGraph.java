/*
 * Copyright 1998-2007 The Brookings Institution, NuTech Solutions,Inc., Metascape LLC, and contributors. 
 * All rights reserved.
 * This program and the accompanying materials are made available solely under of the BSD license "ascape-license.txt".
 * Any referenced or included libraries carry licenses of their respective copyright holders. 
 */

package org.ascape.model.space;


/**
 * The Class CoordinateGraph.
 */
public class CoordinateGraph extends Coordinate {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    /**
     * The location.
     */
    private Location location;

    /**
     * Instantiates a new coordinate graph.
     * 
     * @param location
     *            the location
     */
    public CoordinateGraph(Location location) {
        this.location = location;
    }

    /* (non-Javadoc)
     * @see org.ascape.model.space.Coordinate#add(org.ascape.model.space.Coordinate)
     */
    public Coordinate add(Coordinate coordinate) {
        return null;
    }

    /* (non-Javadoc)
     * @see org.ascape.model.space.Coordinate#getDistance(org.ascape.model.space.Coordinate)
     */
    public double getDistance(Coordinate coordinate) {
        return 0;
    }

    /**
     * Gets the location.
     * 
     * @return the location
     */
    public Location getLocation() {
        return location;
    }

    /**
     * Sets the location.
     * 
     * @param location
     *            the new location
     */
    public void setLocation(Location location) {
        this.location = location;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    public boolean equals(Object o) {
        return this == o;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    public int hashCode() {
        return location.hashCode();
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    public String toString() {
        return "";
    }
}
