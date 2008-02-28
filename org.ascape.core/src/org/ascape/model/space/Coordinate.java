/*
 * Copyright 1998-2007 The Brookings Institution, NuTech Solutions,Inc., Metascape LLC, and contributors. 
 * All rights reserved.
 * This program and the accompanying materials are made available solely under of the BSD license "ascape-license.txt".
 * Any referenced or included libraries carry licenses of their respective copyright holders. 
 */

package org.ascape.model.space;

import java.io.Serializable;

/**
 * The base class for a location within a space defined by a particular
 * geometry.
 * 
 * @author Miles Parker
 * @version 1.2
 * @history 1.2 6/4/99 Removed geometry as a member variable and changed
 *          constructors to reflect
 * @history 1.0 first in 1.0
 * @since 1.0
 */
public abstract class Coordinate implements Cloneable, Serializable {

    /**
     * The geometry this coordinate exists within.
     * 
     * @param coordinate
     *            the coordinate
     * @return the distance
     */
    //protected Geometry geometry;

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    /**
     * Returns the distance between this coordinate and the supplied coordinate.
     * @param coordinate the coordiante to measure distance to
     */
    public abstract double getDistance(Coordinate coordinate);

    /**
     * Adds the location of the supplied coordinate to this coordinate,
     * returning the resulting coordinate.
     * 
     * @param coordinate
     *            the coordinate to sum with this one
     * @return the coordinate
     */
    public abstract Coordinate add(Coordinate coordinate);
}
