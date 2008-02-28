/*
 * Copyright 1998-2007 The Brookings Institution, NuTech Solutions,Inc., Metascape LLC, and contributors. 
 * All rights reserved.
 * This program and the accompanying materials are made available solely under of the BSD license "ascape-license.txt".
 * Any referenced or included libraries carry licenses of their respective copyright holders. 
 */

package org.ascape.model.space;


/**
 * The Interface Location.
 */
public interface Location {

    /**
     * Initialize.
     */
    public void initialize();

    /**
     * Gets the coordinate.
     * 
     * @return the coordinate
     */
    public Coordinate getCoordinate();

    /**
     * Sets the coordinate.
     * 
     * @param coordinate
     *            the new coordinate
     */
    public void setCoordinate(Coordinate coordinate);

    /**
     * Checks if is delete.
     * 
     * @return true, if is delete
     */
    public boolean isDelete();

    /**
     * Mark for deletion.
     */
    public void markForDeletion();

    /**
     * Clear delete marker.
     */
    public void clearDeleteMarker();

    /**
     * Clone.
     * 
     * @return the object
     */
    public Object clone();
}
