/*
 * Copyright 1998-2007 The Brookings Institution, NuTech Solutions,Inc., Metascape LLC, and contributors. 
 * All rights reserved.
 * This program and the accompanying materials are made available solely under of the BSD license "ascape-license.txt".
 * Any referenced or included libraries carry licenses of their respective copyright holders. 
 */

package org.ascape.model.space;


import java.util.List;


/**
 * A two-dimensional space providing effecient implementations for Moore
 * neighbors. In a Moore neighborhood, cells are considered neighbors if they
 * meet the target at any point:
 * 
 * <pre>
 * OOO
 * OXO
 * OOO
 * </pre>
 * 
 * @author Miles Parker
 * @version 2.0
 * @history 2.0 10/29/01 changes to support new continuous space functionality,
 *          (int) getDistance is now (double) calcualteDistance
 * @history 1.9.3 3-4/2001 Many QA fixes and functional improvements
 * @history 1.2.5 10/6/99 changed space constructors to include name and not
 *          include geometry where appropriate
 * @since 1.0
 */
public class Array2DMoore extends Array2D {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    /**
     * Constructs a two-dimensional moore-neighborhood array of provided
     * geometry and extent, populated with clones of provided agent.
     */
    public Array2DMoore() {
        super();
        setGeometry(new Geometry(2, true, Geometry.MOORE));
    }

    /**
     * Constructs a two-dimensional moore-neighborhood space array of provided
     * geometry and extent, populated with clones of provided agent.
     * 
     * @param extent
     *            a coordinate describing the size of this space
     */
    public Array2DMoore(CoordinateDiscrete extent) {
        this();
        setExtent(extent);
    }

    /**
     * Returns the shortest distance between one cell and the other, assuming a
     * walk between the two cells. In a Moore neighborhood, the distance is
     * equal to the maximum dimension distance.
     * 
     * @param origin
     *            the starting cell
     * @param target
     *            the ending cell
     * @return the double
     */
    public final double calculateDistance(Coordinate origin, Coordinate target) {
        return calculateDistanceMoore(origin, target);
    }

    /* (non-Javadoc)
     * @see org.ascape.model.space.Array#findWithinImpl(org.ascape.model.space.Coordinate, boolean, double)
     */
    public List findWithinImpl(Coordinate origin, boolean includeSelf, double distance) {
        return findWithinMoore(origin, includeSelf, distance);
    }

}
