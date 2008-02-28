/*
 * Copyright 1998-2007 The Brookings Institution, NuTech Solutions,Inc., Metascape LLC, and contributors. 
 * All rights reserved.
 * This program and the accompanying materials are made available solely under of the BSD license "ascape-license.txt".
 * Any referenced or included libraries carry licenses of their respective copyright holders. 
 */

package org.ascape.model.space;


/**
 * A space containing a sub-population of agents that exist within some
 * continuous space.
 * 
 * @author Miles Parker
 * @version 3.0
 * @history 3.0 9/10/02 significant refactoring of space model hierarchy
 * @history 2.0 8/31/01 first in
 * @since 2.0
 */
public abstract class SubContinuous extends SubCollection implements SubSpace, Continuous {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    /**
     * The size.
     */
    private int size;

    /**
     * Constructs a sub-continuous space.
     */
    public SubContinuous() {
        super();
    }

    /**
     * Constructs a sub-collection.
     * 
     * @param extent
     *            a coordinate describing the size of this space
     */
    public SubContinuous(CoordinateDiscrete extent) {
        this();
        setExtent(extent);
    }

    /**
     * A no-op; overrides the base collection's behavior so that agents do not
     * have their coorinates changed.
     */
    public void coordinateSweep() {
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
        if (superSpace instanceof Continuous) {
            super.setSuperSpace(superSpace);
        } else {
            throw new RuntimeException("Super space type doesn't match.");
        }
    }

    /* (non-Javadoc)
     * @see org.ascape.model.space.Continuous#distancePerIteration(double)
     */
    public double distancePerIteration(double velocity) {
        return ((Continuous) getSuperSpace()).distancePerIteration(velocity);
    }

    /* (non-Javadoc)
     * @see org.ascape.model.space.CollectionSpace#findRandomCoordinate()
     */
    public Coordinate findRandomCoordinate() {
        return getSuperSpace().findRandomCoordinate();
    }

    /* (non-Javadoc)
     * @see org.ascape.model.space.Continuous#normalize(org.ascape.model.space.Coordinate)
     */
    public void normalize(Coordinate coor) {
        ((Continuous) getSuperSpace()).normalize(coor);
    }

    /**
     * Sets the number of agents in the space.
     * 
     * @param size
     *            the size
     */
    public void setSize(int size) {
        this.size = size;
    }

    /**
     * Return the number of agents in the space.
     * 
     * @return the size
     */
    public int getSize() {
        return size;
    }

    /**
     * Returns the extent of this subscape, always the same as the super space.
     * 
     * @return the extent
     */
    public Coordinate getExtent() {
        return getSuperSpace().getExtent();
    }
}
