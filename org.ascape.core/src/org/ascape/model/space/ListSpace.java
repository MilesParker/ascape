/*
 * Copyright 1998-2007 The Brookings Institution, NuTech Solutions,Inc., Metascape LLC, and contributors. 
 * All rights reserved.
 * This program and the accompanying materials are made available solely under of the BSD license "ascape-license.txt".
 * Any referenced or included libraries carry licenses of their respective copyright holders. 
 */

package org.ascape.model.space;


/**
 * A one-dimensional, fixed-size, collection of agents providing services
 * described for space. This os just a ListBase that is marked as Mutable; that
 * is that you can add and remove agents from at runtime.
 * 
 * @author Miles Parker
 * @version 3.0
 * @history 3.0 9/10/02 significant refactoring of space model hierarchy
 * @history 2.0 10/29/01 changes to support new continuous space functionality
 * @history 1.9.2 3/5/01 first in, to absorb common functionality of Scape and
 *          ScapeArray1D
 * @since 1.0
 */
public class ListSpace extends ListBase implements Mutable, CoordinateMutable {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    /**
     * Constructs a (one-dimensional) mutable list.
     */
    public ListSpace() {
        super();
        setExtent(new Coordinate1DDiscrete(0));
    }

    /**
     * Constructs a (one-dimensional) mutable list.
     * 
     * @param extent
     *            a coordinate describing the size of this space
     */
    public ListSpace(CoordinateDiscrete extent) {
        this();
        setExtent(extent);
    }
}
