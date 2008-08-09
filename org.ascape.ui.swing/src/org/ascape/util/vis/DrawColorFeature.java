/*
 * Copyright 1998-2007 The Brookings Institution, NuTech Solutions,Inc., Metascape LLC, and contributors. 
 * All rights reserved.
 * This program and the accompanying materials are made available solely under the BSD license "ascape-license.txt".
 * Any referenced or included libraries carry licenses of their respective copyright holders. 
 */

package org.ascape.util.vis;

import java.awt.Color;
import java.awt.Graphics;

/**
 * A class using a color feature to determine the color to use to draw a feature.
 * @author Miles Parker
 * @version 1.2
 * @history 1.2 7/8/99 added more constructor options (including name) to simplify subclassing
 * @history 1.0.1 3/13/99 renamed from DrawColorSource
 * @since 1.0
 */
public class DrawColorFeature extends DrawFeature implements ColorFeature {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    /**
     * The color feature to use for drawing this feature.
     * By default, black.
     */
    protected ColorFeature colorFeature = ColorFeatureFixed.black;

    /**
     * Constructs a DrawColorFeature.
     */
    public DrawColorFeature() {
        super();
    }

    /**
     * Constructs the feature with the supplied name.
     * @param name the name of this draw feature
     */
    public DrawColorFeature(String name) {
        super(name);
    }

    /**
     * Constructs the feature with a name and color feature.
     * @param name the name of this draw feature
     */
    public DrawColorFeature(String name, ColorFeature colorFeature) {
        super(name);
        this.colorFeature = colorFeature;
    }

    /**
     * Constructs the feature with a name and nested feature.
     * @param name the name of this draw feature
     * @param nestedFeature the feature to nest within this feature
     */
    public DrawColorFeature(String name, DrawFeature nestedFeature) {
        super(name, nestedFeature);
    }

    /**
     * Constructs a DrawColorFeature with a nested feature.
     * @param nestedFeature the feature to nest
     */
    public DrawColorFeature(DrawFeature nestedFeature) {
        super(nestedFeature);
    }

    /**
     * Draws a graphic interpreation of the object into the supplied graphics port,
     * (typically) within the supplied dimensions. Views which use this class are
     * responsible for translating the graphics so that the object is drawn at the
     * approriate location. Please let us know if you think you need a directly
     * addressed alternative.
     * @param g the Graphics context to draw into
     * @param object the object to interpret for drawing
     * @param width the width of the space that should be drawn into
     * @param height the height of the space that should be drawn into
     */
    public void draw(Graphics g, Object object, int width, int height) {
        g.setColor(getColor(object));
        nestedFeature.draw(g, object, width, height);
    }

    /**
     * Returns the color feature's color interpretation of the object.
     * @param object the object to interpret color for
     */
    public final Color getColor(Object object) {
        return colorFeature.getColor(object);
    }

    /**
     * Returns the color feature this object is using to interpret the object's color.
     */
    public ColorFeature getColorFeature() {
        return colorFeature;
    }

    /**
     * Sets the color feature this object uses to interpret the object's color.
     * @param colorFeature the feature to use for coloring
     */
    public void setColorFeature(ColorFeature colorFeature) {
        this.colorFeature = colorFeature;
    }

    /**
     * Returns the user relevant name of this feature.
     * "Unnamed", the name of the nested feature, or the name of the color feature, by default.
     * Override or set name to provide a name.
     */
    public String getName() {
        if (name != null) {
            return name;
        } else {
            if (nestedFeature != null) {
                String candidateName = nestedFeature.getName();
                if (candidateName != "Unnamed") {
                    return candidateName;
                } else {
                    return colorFeature.getName();
                }
            } else if (colorFeature != null) {
                return colorFeature.getName();
            } else {
                return "Unnamed";
            }
        }
    }
}

