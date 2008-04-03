/*
 * Copyright 1998-2007 The Brookings Institution, NuTech Solutions,Inc., Metascape LLC, and contributors. 
 * All rights reserved.
 * This program and the accompanying materials are made available solely under of the BSD license "ascape-license.txt".
 * Any referenced or included libraries carry licenses of their respective copyright holders. 
 */

package org.ascape.model.space;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;


/**
 * The Class DefaultNode.
 */
public class DefaultNode extends DefaultLocation implements Node {

    /**
     * The neightbors.
     */
    List neightbors = new ArrayList();

    /**
     * The random.
     */
    Random random;

    /* (non-Javadoc)
     * @see org.ascape.model.space.Node#findNeighbors()
     */
    public List findNeighbors() {
        return neightbors;
    }

    /* (non-Javadoc)
     * @see org.ascape.model.space.Node#findRandomNeighbor()
     */
    public Node findRandomNeighbor() {
        return (Node) neightbors.get(random.nextInt(neightbors.size()));
    }

    /* (non-Javadoc)
     * @see org.ascape.model.space.Node#findRandomNeighbor()
     */
    public Node findRandomAvailableNeighbor() {
        throw new UnsupportedOperationException("No support for occupants");
    }

    /* (non-Javadoc)
     * @see org.ascape.model.space.Node#getOccupant()
     */
    public Node getOccupant() {
        throw new UnsupportedOperationException("No support for occupants");
    }

    /* (non-Javadoc)
     * @see org.ascape.model.space.Node#isAvailable()
     */
    public boolean isAvailable() {
        return false;
    }

    /* (non-Javadoc)
     * @see org.ascape.model.space.Node#setOccupant(org.ascape.model.space.Node)
     */
    public void setOccupant(Node occupant) {
        throw new UnsupportedOperationException("No support for occupants");
    }

    /**
     * Gets the random.
     * 
     * @return the random
     */
    public Random getRandom() {
        return random;
    }

    /**
     * Sets the random.
     * 
     * @param random
     *            the new random
     */
    public void setRandom(Random random) {
        this.random = random;
    }
}
