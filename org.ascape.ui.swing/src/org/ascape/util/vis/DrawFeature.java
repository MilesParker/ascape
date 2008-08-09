/*
 * Copyright 1998-2007 The Brookings Institution, NuTech Solutions,Inc., Metascape LLC, and contributors. 
 * All rights reserved.
 * This program and the accompanying materials are made available solely under the BSD license "ascape-license.txt".
 * Any referenced or included libraries carry licenses of their respective copyright holders. 
 */

package org.ascape.util.vis;

import java.awt.Graphics;
import java.io.Serializable;

import org.ascape.util.HasName;

/**
 * An class providing a drawing method for a given object.
 * Special Note: We have moved this clas temporarily to .model to fix a dependency issue.
 * This will be adressed in a better way soon.
 * A DrawFeature is just some interpretation of an object's state as a paintable figure.
 * Provides the abstract capability to nest features. At the moment, this is really only
 * used to provide a way to set a color and draw a feature with the same draw sources,
 * and is intended to support user customization. This will be fleshed out in the future
 * as user customization becomes more important, but isn't a very important feature at
 * the moment. For now, you would typically just set a color and draw a figure within the
 * same draw feature.
 * @author Miles Parker
 * @version 1.2
 * @history 1.2 7/8/99 added more constructor options (including name) to simplify subclassing
 * @history 1.0.1 3/13/99 renamed from DrawSource
 * @since 1.0
 */
public abstract class DrawFeature implements PlatformDrawFeature, Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    /**
     * A nested feature.
     */
    protected DrawFeature nestedFeature;

    /**
     * The user mnemonic name for this feature.
     */
    protected String name;

    /**
     * Constructs the feature.
     */
    public DrawFeature() {
    }

    /**
     * Constructs the feature with the provided name.
     * @param name a name for this draw feature
     */
    public DrawFeature(String name) {
        this.name = name;
    }

    /**
     * Constructs the feature with a name and nested feature.
     * @param name a name for this draw feature
     * @param nestedFeature the feature to nest within this feature
     */
    public DrawFeature(String name, DrawFeature nestedFeature) {
        this.nestedFeature = nestedFeature;
        this.name = name;
    }

    /**
     * Constructs the feature with a nested feature.
     * @param nestedFeature the feature to nest within this feature
     */
    public DrawFeature(DrawFeature nestedFeature) {
        this.nestedFeature = nestedFeature;
    }

    /**
     * Returns the feature nested inside this one.
     */
    public DrawFeature getNestedFeature() {
        return nestedFeature;
    }

    /**
     * Sets the feature nested inside of this one.
     * @param nestedFeature the feature to nest
     */
    public void setNestedFeature(DrawFeature nestedFeature) {
        this.nestedFeature = nestedFeature;
    }

    /**
     * Draws a graphic interpretation of the object into the supplied graphics port,
     * (typically) within the supplied dimensions. Views which use this class are
     * responsible for translating the graphics so that the object is drawn at the
     * approriate location. Please let us know if you think you need a directly
     * addressed alternative.
     * @param g the Graphics context to draw into
     * @param object the object to interpret for drawing
     * @param width the width of the space that should be drawn into
     * @param height the height of the space that should be drawn into
     */
    public abstract void draw(Graphics g, Object object, int width, int height);

    /**
     * Returns the user relevant name of this feature.
     * "Unnamed" or the name of the nested feature by default.
     * Override or set name to provide a name.
     */
    public String getName() {
        if (name != null) {
            return name;
        } else {
            if (nestedFeature != null) {
                return nestedFeature.getName();
            } else {
                return "Unnamed";
            }
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
