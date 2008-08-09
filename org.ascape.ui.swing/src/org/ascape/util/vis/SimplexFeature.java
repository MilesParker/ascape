/*
 * Copyright 1998-2007 The Brookings Institution, NuTech Solutions,Inc., Metascape LLC, and contributors. 
 * All rights reserved.
 * This program and the accompanying materials are made available solely under the BSD license "ascape-license.txt".
 * Any referenced or included libraries carry licenses of their respective copyright holders. 
 */

package org.ascape.util.vis;

import org.ascape.util.HasName;

/**
 * An interface providing an interpretation of a peice of data for a simplex view.
 * @author Miles Parker
 * @version 1.5
 * @history 1.5 11/29/99 first in
 * @since 1.5
 */
public abstract class SimplexFeature implements HasName {

    /**
     * The user mnemonic name for this feature.
     */
    private String name;

    /**
     * Constructs the feature.
     */
    public SimplexFeature() {
    }

    /**
     * Constructs the feature with the provided name.
     * @param name the name of this draw feature
     */
    public SimplexFeature(String name) {
        this.name = name;
    }

    public abstract float getAxis1Value(Object object);

    public abstract String getAxis1Name();

    public abstract float getAxis2Value(Object object);

    public abstract String getAxis2Name();

    public abstract float getAxis3Value(Object object);

    public abstract String getAxis3Name();

    /**
     * Returns the user relevant name of this feature.
     * "Unnamed" or the name of the nested feature by default.
     * Override or set name to provide a name.
     */
    public String getName() {
        if (name != null) {
            return name;
        } else {
            return "Unnamed";
        }
    }

    /**
     * Sets the user relevant name for this feature.
     * (Typically, it is quicker and more simple to override the
     * <code>getName</code> method, since you will be creating a subclass anyway.)
     * @param name the user
     */
    public void setName(String name) {
        this.name = name;
    }
}
