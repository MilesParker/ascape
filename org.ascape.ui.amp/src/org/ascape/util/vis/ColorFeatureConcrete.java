/*
 * Copyright 1998-2007 The Brookings Institution, NuTech Solutions,Inc., Metascape LLC, and contributors.
 * All rights reserved.
 * This program and the accompanying materials are made available solely under the BSD license "ascape-license.txt".
 * Any referenced or included libraries carry licenses of their respective copyright holders.
 */

package org.ascape.util.vis;

import java.util.HashMap;
import java.util.Map;

import org.ascape.model.AscapeObject;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;

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
public abstract class ColorFeatureConcrete implements Cloneable, ColorFeature {

    {
        AscapeObject.PLATFORM_DEFAULT_COLOR = BLACK;
    }

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    /**
     * The name of the feature.
     */
    protected String name;

    protected static Map<RGB, Color> colorCache = new HashMap<RGB, Color>();

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

    //todo cache colors
    public final static Color create(RGB rgb) {
        Color color = colorCache.get(rgb);
        if (color == null) {
            color = new Color(org.eclipse.swt.widgets.Display.getCurrent(), rgb.red, rgb.green, rgb.blue);
            colorCache.put(rgb, color);
        }
        return color;
    }

    //todo cache colors
    public final static Color create(int r, int g, int b) {
        return create(new RGB(r, g, b));
    }

    public final static Color create(float r, float g, float b) {
        return create((int) (r * 255), (int) (g * 255), (int) (b * 255));
    }

    //handle alpha
    public final static Color create(int r, int g, int b, int a) {
        return new Color(org.eclipse.swt.widgets.Display.getCurrent(), r, g, b);
    }

    public static Color adjustBrightness(Color color, float factor) {
        // TODO Cache generated values
        float[] agentHSB = color.getRGB().getHSB();
        float newBright = Math.min(1.0f, agentHSB[2] * factor);
        Color brighter = createHSB(agentHSB[0], agentHSB[1], newBright);
        return brighter;
    }

    // handle alpha
    public final static Color createHSB(float hue, float sat, float brightness) {
        return create(new RGB(hue, sat, brightness));
    }

    //todo cache colors
    public final static Color createRandom() {
        return create((float) Math.random(), (float) Math.random(), (float) Math.random());
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
