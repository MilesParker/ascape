/*
 * Copyright 1998-2007 The Brookings Institution, NuTech Solutions,Inc., Metascape LLC, and contributors. 
 * All rights reserved.
 * This program and the accompanying materials are made available solely under of the BSD license "ascape-license.txt".
 * Any referenced or included libraries carry licenses of their respective copyright holders. 
 */
package org.ascape.model.space;

import java.util.Iterator;

/**
 * User: jmiller Date: Feb 10, 2006 Time: 2:06:17 PM To change this template use
 * Options | File Templates.
 */
public class SubContinuous1D extends SubContinuous {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    /**
     * Constructs a sub-continuous space.
     */
    public SubContinuous1D() {
        super();
    }

    /**
     * Constructs a sub-continuous space.
     * 
     * @param extent
     *            a coordinate describing the size of this space
     */
    public SubContinuous1D(CoordinateDiscrete extent) {
        this();
        setExtent(extent);
    }

    /* (non-Javadoc)
     * @see org.ascape.model.space.SubCollection#populate()
     */
    public void populate() {
        super.populate();
        Iterator iter = iterator();
        while (iter.hasNext()) {
            ((Location) iter.next()).setCoordinate(new Coordinate1DContinuous(0.0f));
        }
    }

    /**
     * Sets the super space for this sub collection, that is, the collection
     * defined to contain a superset of this space's agents. Checks to ensure
     * that the super space is the appropriate type.
     * 
     * @param superSpace
     *            the collection to assign as super set
     */
    public void setSuperSpace(Space superSpace) {
        if ((superSpace instanceof Continuous1D) || (superSpace instanceof SubContinuous1D)) {
            super.setSuperSpace(superSpace);
        } else {
            throw new RuntimeException("Super space type doesn't match.");
        }
    }
}
