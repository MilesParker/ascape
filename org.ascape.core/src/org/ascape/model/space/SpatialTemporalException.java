/*
 * Copyright 1998-2007 The Brookings Institution, NuTech Solutions,Inc., Metascape LLC, and contributors. 
 * All rights reserved.
 * This program and the accompanying materials are made available solely under of the BSD license "ascape-license.txt".
 * Any referenced or included libraries carry licenses of their respective copyright holders. 
 */

package org.ascape.model.space;

/**
 * An exception thrown if attempt is made to create inconsistent or impossible
 * state in local space-time.
 * 
 * @author Miles Parker
 * @version 1.0
 */
public class SpatialTemporalException extends Exception {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    /**
     * Instantiates a new spatial temporal exception.
     */
    public SpatialTemporalException() {
        super();
    }

    /**
     * Instantiates a new spatial temporal exception.
     * 
     * @param s
     *            the s
     */
    public SpatialTemporalException(String s) {
        super(s);
    }
}

