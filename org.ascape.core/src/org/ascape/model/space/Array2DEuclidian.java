/*
 * Copyright 1998-2007 The Brookings Institution, NuTech Solutions,Inc., Metascape LLC, and contributors. 
 * All rights reserved.
 * This program and the accompanying materials are made available solely under of the BSD license "ascape-license.txt".
 * Any referenced or included libraries carry licenses of their respective copyright holders. 
 */

package org.ascape.model.space;

import java.util.List;


/**
 * A two-dimensional space providing effecient implementations for von Neumann
 * neighbors. In a von Neumann neighborhood, cells are considered neighbors if
 * they share an edge with the target:
 * 
 * <pre>
 * O
 * OXO
 * O
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
public class Array2DEuclidian extends Array2D {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    /**
     * Constructs a two-dimensional Euclidian array.
     */
    public Array2DEuclidian() {
        super();
        setGeometry(new Geometry(2, false, Geometry.EUCLIDIAN));
    }

    /**
     * Constructs a two-dimensional space array of provided geometry and extent,
     * populated with clones of provided agent.
     * 
     * @param extent
     *            a coordinate describing the size of this space
     */
    public Array2DEuclidian(CoordinateDiscrete extent) {
        this();
        setExtent(extent);
    }

    /* (non-Javadoc)
     * @see org.ascape.model.space.Discrete#calculateNeighbors(org.ascape.model.space.Node)
     */
    public List calculateNeighbors(Node cell) {
        //For euclidian, it makes more sense to treat any adjoinging cells as neighbors..
        //If we used the set distance of 1, we'd get the vonNeumann neighborhood..
        return findWithinMoore(cell.getCoordinate(), false, 1);
    }

    /* (non-Javadoc)
     * @see org.ascape.model.space.Array#findWithinImpl(org.ascape.model.space.Coordinate, boolean, double)
     */
    public List findWithinImpl(Coordinate origin, boolean includeSelf, double distance) {
        return findWithinEuclidian(origin, includeSelf, distance);
    }

    /**
     * Returns the shortest distance between one cell and the other, assuming a
     * walk between the two cells. In a Euclidian space, this distance is equal
     * to the dimension root of the sum of each dimension distance squared.
     * 
     * @param origin
     *            the starting cell
     * @param target
     *            the ending cell
     * @return the double
     */
    public double calculateDistance(Coordinate origin, Coordinate target) {
        return (int) Math.round(Math.sqrt(Math.pow(getXSpan(origin, target), 2.0) + Math.pow(getYSpan(origin, target), 2.0)));
    }
}

