/*
 * Copyright 1998-2007 The Brookings Institution, NuTech Solutions,Inc., Metascape LLC, and contributors. 
 * All rights reserved.
 * This program and the accompanying materials are made available solely under of the BSD license "ascape-license.txt".
 * Any referenced or included libraries carry licenses of their respective copyright holders. 
 */

package org.ascape.model.space;

import java.util.List;

/**
 * The Interface Node.
 */
public interface Node extends Location {

    /**
     * Checks if is available.
     * 
     * @return true, if is available
     */
    public boolean isAvailable();

    /**
     * Find random neighbor.
     * 
     * @return the node
     */
    public Node findRandomNeighbor();

    /**
     * Find random neighbor.
     * 
     * @return the node
     */
    public Node findRandomAvailableNeighbor();

    /**
     * Find neighbors.
     * 
     * @return the list
     */
    public List findNeighbors();

    /**
     * Gets the occupant.
     * 
     * @return the occupant
     * @throws UnsupportedOperationException
     *             if occupants are not supported.
     */
    public Node getOccupant();

    /**
     * Sets the occupant.
     * 
     * @param occupant
     *            the occupant
     * @throws UnsupportedOperationException
     *             if occupants are not supported.
     */
    public void setOccupant(Node occupant);
}
