/*
 * Copyright 1998-2007 The Brookings Institution, NuTech Solutions,Inc., Metascape LLC, and contributors. 
 * All rights reserved.
 * This program and the accompanying materials are made available solely under of the BSD license "ascape-license.txt".
 * Any referenced or included libraries carry licenses of their respective copyright holders. 
 */

package org.ascape.model.space;


/**
 * The Class DefaultLocation.
 */
public class DefaultLocation implements Location {

    /**
     * The delete.
     */
    private boolean delete;

    /**
     * The coordinate.
     */
    private Coordinate coordinate;

    /* (non-Javadoc)
     * @see org.ascape.model.space.Location#clearDeleteMarker()
     */
    public void clearDeleteMarker() {
        delete = false;
    }

    /* (non-Javadoc)
     * @see org.ascape.model.space.Location#getCoordinate()
     */
    public Coordinate getCoordinate() {
        return coordinate;
    }

    /* (non-Javadoc)
     * @see org.ascape.model.space.Location#initialize()
     */
    public void initialize() {
    }

    /* (non-Javadoc)
     * @see org.ascape.model.space.Location#isDelete()
     */
    public boolean isDelete() {
        return delete;
    }

    /* (non-Javadoc)
     * @see org.ascape.model.space.Location#markForDeletion()
     */
    public void markForDeletion() {
        delete = true;
    }

    /* (non-Javadoc)
     * @see org.ascape.model.space.Location#setCoordinate(org.ascape.model.space.Coordinate)
     */
    public void setCoordinate(Coordinate coordinate) {
        this.coordinate = coordinate;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#clone()
     */
    public Object clone() {
        try {
            return super.clone();
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException("Not supported");
        }
    }
}
