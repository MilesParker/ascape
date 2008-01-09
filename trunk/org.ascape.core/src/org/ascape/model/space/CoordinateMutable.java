/*
 * Copyright 1998-2007 The Brookings Institution, NuTech Solutions,Inc., Metascape LLC, and contributors. 
 * All rights reserved.
 * This program and the accompanying materials are made available solely under of the BSD license "ascape-license.txt".
 * Any referenced or included libraries carry licenses of their respective copyright holders. 
 */

package org.ascape.model.space;

/**
 * A one-dimensional, fixed-size, collection of agents providing services
 * described for space.
 * 
 * @author Miles Parker
 * @version 3.0
 * @history 3.0 9/10/02 significant refactoring of space model hierarchy
 * @history 2.0 11/10/01 added new agent random method
 * @history 2.0 11/5/01 added new agent method
 * @history 1.5 11/30/99 first in
 * @since 1.5
 */
public interface CoordinateMutable extends Mutable {

    /**
     * Is a coordinate location sweep needed for this space?.
     * 
     * @return true, if is coordinate sweep needed
     */
    boolean isCoordinateSweepNeeded();

    /**
     * Walks through each cell, setting the cell's coordinates. Only applies to
     * discrrete mutable scapes, the behavior is overriden for scapes in which
     * the deletion or insertion of agents has no effect on an agent's
     * coordinate.
     */
    void coordinateSweep();
}
