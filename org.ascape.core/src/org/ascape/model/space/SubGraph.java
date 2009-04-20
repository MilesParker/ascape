/*
 * Copyright 1998-2007 The Brookings Institution, NuTech Solutions,Inc., Metascape LLC, and contributors.
 * All rights reserved.
 * This program and the accompanying materials are made available solely under of the BSD license "ascape-license.txt".
 * Any referenced or included libraries carry licenses of their respective copyright holders.
 */
package org.ascape.model.space;

import java.util.ArrayList;

/**
 * User: jmiller Date: Nov 14, 2005 Time: 1:05:27 PM To change this template use
 * Options | File Templates.
 */
/**
 * This is an unsual version of the SubSpace - it's a Graph, where the members also exist in a Continuous Space.
 * Written for use in the NAS model. At some point, this should probably be made more general.
 */
public class SubGraph extends Graph implements SubSpace {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    /**
     * The super space.
     */
    private Space superSpace;

    /**
     * Constructs an arbitrary directed graph.
     */
    public SubGraph() {
        super();
    }

    /**
     * Adds the supplied object (assumed to be an agent) to this graph.
     * 
     * @param o
     *            the agent to add
     * @param isParent
     *            the is parent
     * @return true, if add
     */
    public boolean add(Object o, boolean isParent) {
        superSpace.add(o, false);
        getAdjacencyMap().put(o, new ArrayList());
        if (o instanceof ISubGraphAgent) {
            ((ISubGraphAgent) o).setCoordinateGraph(new CoordinateGraph((Node) o));
        } else {
            ((Node) o).setCoordinate(((Node) o).getCoordinate());
        }
        return  super.add(o, isParent);

    }

    /* (non-Javadoc)
     * @see org.ascape.model.space.SubSpace#getSuperSpace()
     */
    public Space getSuperSpace() {
        return superSpace;
    }

    /* (non-Javadoc)
     * @see org.ascape.model.space.SubSpace#setSuperSpace(org.ascape.model.space.Space)
     */
    public void setSuperSpace(Space superSpace) {
        this.superSpace = superSpace;
    }
}
