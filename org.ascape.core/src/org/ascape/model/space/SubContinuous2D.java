/*
 * Copyright 1998-2007 The Brookings Institution, NuTech Solutions,Inc., Metascape LLC, and contributors. 
 * All rights reserved.
 * This program and the accompanying materials are made available solely under of the BSD license "ascape-license.txt".
 * Any referenced or included libraries carry licenses of their respective copyright holders. 
 */

package org.ascape.model.space;

import java.util.Iterator;

/**
 * A space containing a population of agents that exist within some continuous
 * space.
 * 
 * @author Miles Parker
 * @version 3.0
 * @history 3.0 9/10/02 significant refactoring of space model hierarchy
 * @history 2.0 8/31/01 first in
 * @since 2.0
 */
public class SubContinuous2D extends SubContinuous {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    /**
     * Constructs a sub-continuous space.
     */
    public SubContinuous2D() {
        super();
    }

    /**
     * Constructs a sub-continuous space.
     * 
     * @param extent
     *            a coordinate describing the size of this space
     */
    public SubContinuous2D(CoordinateDiscrete extent) {
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
            ((Location) iter.next()).setCoordinate(new Coordinate2DContinuous(0.0f, 0.0f));
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
        if ((superSpace instanceof Continuous2D) || (superSpace instanceof SubContinuous2D)) {
            super.setSuperSpace(superSpace);
        } else {
            throw new RuntimeException("Super space type doesn't match.");
        }
    }
}
