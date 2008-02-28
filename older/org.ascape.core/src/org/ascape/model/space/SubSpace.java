/*
 * Copyright 1998-2007 The Brookings Institution, NuTech Solutions,Inc., Metascape LLC, and contributors. 
 * All rights reserved.
 * This program and the accompanying materials are made available solely under of the BSD license "ascape-license.txt".
 * Any referenced or included libraries carry licenses of their respective copyright holders. 
 */

package org.ascape.model.space;

/**
 * The Interface SubSpace.
 */
public interface SubSpace {

    /**
     * Gets the super space.
     * 
     * @return the super space
     */
    Space getSuperSpace();

    /**
     * Sets the super space.
     * 
     * @param superSpace
     *            the new super space
     */
    void setSuperSpace(Space superSpace);
}
