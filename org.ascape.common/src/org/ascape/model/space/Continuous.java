/*
 * Copyright 1998-2007 The Brookings Institution, NuTech Solutions,Inc., Metascape LLC, and contributors. 
 * All rights reserved.
 * This program and the accompanying materials are made available solely under of the BSD license "ascape-license.txt".
 * Any referenced or included libraries carry licenses of their respective copyright holders. 
 */

package org.ascape.model.space;


/**
 * A market interface for a space representing continuous space. Note that
 * continuous is also an instance of COllectionSpace so it will not work to
 * check for continuous scapes by doing something like !instanceof Discrete;
 * instead, check explicitly for instanceof Continuous.
 * 
 * @author Miles Parker
 * @version 3.0
 * @history 3.0 9/10/02 significant refactoring of space model hierarchy
 * @history 2.0 8/31/01 first in
 * @since 2.0
 */
public interface Continuous extends Mutable {

    /**
     * Distance per iteration.
     * 
     * @param velocity
     *            the velocity
     * @return the double
     */
    public double distancePerIteration(double velocity);

    /**
     * Find random coordinate.
     * 
     * @return the coordinate
     */
    public Coordinate findRandomCoordinate();

    /**
     * Moves an agent toward the specified agent.
     * 
     * @param origin
     *            the agent moving
     * @param target
     *            the agent's target
     * @param distance
     *            the distance to move
     */
    public void moveToward(Location origin, Coordinate target, double distance);

    /**
     * Converts the coordiante into the boundaries of the space. If the
     * cooridnate is out of bounds, adds or substracts the bounds as appropriate
     * to bring the coordinate into a common sapce mod boundary.
     * 
     * @param coor
     *            the Coordinate to normalize
     */
    public void normalize(Coordinate coor);
}
