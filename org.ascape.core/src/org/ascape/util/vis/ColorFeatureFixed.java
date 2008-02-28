/*
 * Copyright 1998-2007 The Brookings Institution, NuTech Solutions,Inc., Metascape LLC, and contributors. 
 * All rights reserved.
 * This program and the accompanying materials are made available solely under the BSD license "ascape-license.txt".
 * Any referenced or included libraries carry licenses of their respective copyright holders. 
 */

package org.ascape.util.vis;

import java.awt.Color;

/**
 * An interface for a class providing an color that doesn't change in
 * relation to the supplied object. All awt colors are supplied.
 *
 * @author Miles Parker
 * @version 1.2
 * @history 1.2 7/8/99 added more constructor options (including name) to simplify subclassing
 * @history 1.2 7/8/99 Changed order of consturctor so it would match other similar classes.
 * @history 1.0.1 3/13/99 renamed from FixedColorSource
 * @since 1.0
 */
public class ColorFeatureFixed extends ColorFeatureConcrete {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    /**
     * A color feature that always returns black.
     */
    public final static ColorFeatureFixed black = new ColorFeatureFixed("Black", Color.black);

    /**
     * A color feature that always returns blue.
     */
    public final static ColorFeatureFixed blue = new ColorFeatureFixed("Blue", Color.blue);

    /**
     * A color feature that always returns cyan.
     */
    public final static ColorFeatureFixed cyan = new ColorFeatureFixed("Cyan", Color.cyan);

    /**
     * A color feature that always returns darkGray.
     */
    public final static ColorFeatureFixed darkGray = new ColorFeatureFixed("Dark Gray", Color.darkGray);

    /**
     * A color feature that always returns gray.
     */
    public final static ColorFeatureFixed gray = new ColorFeatureFixed("Gray", Color.gray);

    /**
     * A color feature that always returns green.
     */
    public final static ColorFeatureFixed green = new ColorFeatureFixed("Green", Color.green);

    /**
     * A color feature that always returns lightGray.
     */
    public final static ColorFeatureFixed lightGray = new ColorFeatureFixed("Light Gray", Color.lightGray);

    /**
     * A color feature that always returns magenta.
     */
    public final static ColorFeatureFixed magenta = new ColorFeatureFixed("Magenta", Color.magenta);

    /**
     * A color feature that always returns orange.
     */
    public final static ColorFeatureFixed orange = new ColorFeatureFixed("Orange", Color.orange);

    /**
     * A color feature that always returns pink.
     */
    public final static ColorFeatureFixed pink = new ColorFeatureFixed("Pink", Color.pink);

    /**
     * A color feature that always returns red.
     */
    public final static ColorFeatureFixed red = new ColorFeatureFixed("Red", Color.red);

    /**
     * A color feature that always returns white.
     */
    public final static ColorFeatureFixed white = new ColorFeatureFixed("White", Color.white);

    /**
     * A color feature that always returns yellow.
     */
    public final static ColorFeatureFixed yellow = new ColorFeatureFixed("Yellow", Color.yellow);

    /**
     * The color of the feature.
     */
    private Color color;

    /**
     * Constructs a ColorFeatureFixed.
     */
    public ColorFeatureFixed() {
    }

    /**
     * Constructs a ColorFeatureFixed.
     * @param name the color of the feature
     * @param color the user relevant name of the feature
     */
    public ColorFeatureFixed(String name, Color color) {
        super(name);
        this.color = color;
    }

    /**
     * Returns a color for the object as defined irrespective of object by setColor.
     * @param object Ignored; normally, the object the object to get a color from.
     */
    public final Color getColor(Object object) {
        return color;
    }

    /**
     * Sets the color of this feature.
     * @param color the color this feature should always return
     */
    public final void setColor(Color color) {
        this.color = color;
    }

    /**
     * Clones this feature.
     */
    public Object clone() {
        try {
            ColorFeatureFixed clone = (ColorFeatureFixed) super.clone();
            return clone;
        } catch (CloneNotSupportedException e) {
            // this shouldn't happen, since we are Cloneable
            throw new InternalError();
        }
    }
}
