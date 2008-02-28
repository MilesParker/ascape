/*
 * Copyright 1998-2007 The Brookings Institution, NuTech Solutions,Inc., Metascape LLC, and contributors. 
 * All rights reserved.
 * This program and the accompanying materials are made available solely under of the BSD license "ascape-license.txt".
 * Any referenced or included libraries carry licenses of their respective copyright holders. 
 */

package org.ascape.model.space;

import java.util.Iterator;
import java.util.List;

/**
 * A 1D Small World version of the default Graph.
 */
public class GraphSmallWorld extends Graph {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    /**
     * Constructs a one-dimensional small-world graph.
     */
    public GraphSmallWorld() {
        super();
    }

    /**
     * Constructs a one-dimensional small-world graph.
     * 
     * @param extent
     *            a coordinate describing the size of this space
     */
    public GraphSmallWorld(CoordinateDiscrete extent) {
        this();
        setExtent(extent);
    }

    /**
     * The random edge ratio.
     */
    private double randomEdgeRatio = 0.10;

    /* (non-Javadoc)
     * @see org.ascape.model.space.Graph#initialize()
     */
    public void initialize() {
        super.initialize();
        Iterator it = collection.iterator();
        Node first = (Node) it.next();
        Node prev = first;
        while (it.hasNext()) {
            Node cell = (Node) it.next();
            addNeighbor(cell, prev, false);
            // if this is the last cell in the iterator, connect it to the first one
            if (!it.hasNext()) {
                addNeighbor(cell, first, false);
            }
            prev = cell;
        }

        // build random links based on randomEdgeRatio
        if (randomEdgeRatio > 0.0) {
            for (Iterator graphIt = collection.iterator(); graphIt.hasNext();) {
                Node cell = (Node) graphIt.next();
                if (getRandom().nextDouble() <= randomEdgeRatio) {
                    boolean tryAgain;
                    Node random;
                    do {
                        tryAgain = false;
                        random = (Node) findRandom();
                        // PROBLEM: this could go on for ever.
                        List neighbors = getNeighborsFor(cell);
                        if (cell == random) {
                            tryAgain = true;
                        } else if (neighbors.contains(random)) {
                            tryAgain = true;
                        }
                    } while (tryAgain);
                    addNeighbor(cell, random);
                }
            }
        }
    }

    /**
     * Gets the random edge ratio.
     * 
     * @return the random edge ratio
     */
    public double getRandomEdgeRatio() {
        return randomEdgeRatio;
    }

    /**
     * Sets the random edge ratio.
     * 
     * @param randomEdgeRatio
     *            the new random edge ratio
     */
    public void setRandomEdgeRatio(double randomEdgeRatio) {
        this.randomEdgeRatio = randomEdgeRatio;
    }
}
