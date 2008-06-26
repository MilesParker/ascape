/*
 * Copyright 1998-2007 The Brookings Institution, NuTech Solutions,Inc., Metascape LLC, and contributors. 
 * All rights reserved.
 * This program and the accompanying materials are made available solely under of the BSD license "ascape-license.txt".
 * Any referenced or included libraries carry licenses of their respective copyright holders. 
 */

package org.ascape.model.space;

/**
 * The Interface SpaceContext.
 */
public interface SpaceContext {

    /**
     * Checks if is home.
     * 
     * @param a
     *            the a
     * @return true, if is home
     */
    public boolean isHome(Location a);

    /**
     * Gets the name.
     * 
     * @return the name
     */
    public String getName();

    /**
     * Gets the prototype.
     * 
     * @return the prototype
     */
    public Location getPrototype();
}
