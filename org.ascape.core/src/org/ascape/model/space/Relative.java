/*
 * Copyright 1998-2007 The Brookings Institution, NuTech Solutions,Inc., Metascape LLC, and contributors. 
 * All rights reserved.
 * This program and the accompanying materials are made available solely under of the BSD license "ascape-license.txt".
 * Any referenced or included libraries carry licenses of their respective copyright holders. 
 */
package org.ascape.model.space;

/**
 * User: Miles Parker Date: Feb 28, 2006 Time: 12:12:21 PM To change this
 * template use File | Settings | File Templates.
 */
public interface Relative {
    
    /**
     * Find relative.
     * 
     * @param location
     *            the location
     * @param coordinate
     *            the coordinate
     * @return the location
     */
    Location findRelative(Location location, Coordinate coordinate);
}
