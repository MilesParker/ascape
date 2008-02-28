/*
 * Copyright 1998-2007 The Brookings Institution, NuTech Solutions,Inc., Metascape LLC, and contributors. 
 * All rights reserved.
 * This program and the accompanying materials are made available solely under the BSD license "ascape-license.txt".
 * Any referenced or included libraries carry licenses of their respective copyright holders. 
 */

package org.ascape.util.vis;

import java.awt.Color;
import java.io.Serializable;

/**
 * A Color feature returns a color based on the state of an object of known type provided to it.
 * This class provides a concrete version of color feature for convenience in implementing subclasses.
 * Used to provide an appropriate color for some feature or aspect of an object.
 *
 * @author Miles Parker
 * @version 1.2
 * @history 1.2 7/8/99 first in, providing concrete implementation of color feature to simplify creation of subclasses
 * @since 1.2
 */
public abstract class ColorFeatureConcrete implements Cloneable, Serializable, ColorFeature {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    /**
     * The name of the feature.
     */
    protected String name;

    /**
     * Constructs a concrete instantiation of a color feature.
     */
    public ColorFeatureConcrete() {
    }

    /**
     * Constructs a concrete instantiation of a color feature with the supplied name.
     * @param name the user relevant name of the feature
     */
    public ColorFeatureConcrete(String name) {
        this.name = name;
    }

    /**
     * Returns a name for the object as defined by set name.
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name of this feature.
     * @param name a user relevant name for this feature
     */
    public final void setName(String name) {
        this.name = name;
    }

    /**
     * Returns a color for the object as defined in implementions of this class.
     * @param object the object to get a color from.
     */
    public abstract Color getColor(Object object);
}
