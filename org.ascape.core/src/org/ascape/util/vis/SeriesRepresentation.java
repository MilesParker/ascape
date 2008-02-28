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
 * Information needed by views to display a data series.
 *
 * @author Miles Parker
 * @version 1.2
 * @history 1.2 7/22/99 first in
 * @since 1.2
 */
public class SeriesRepresentation implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    /**
     * The color for the series.
     */
    private Color color;

    /**
     * Is the series continuos?
     */
    private boolean continuous;

    /**
     * Constructs a new Series View with a random color, and assuming continuous data.
     */
    public SeriesRepresentation() {
        color = new Color((float) Math.random(), (float) Math.random(), (float) Math.random());
        continuous = true;
    }

    /**
     * Returns a color for the object as defined irrespective of object by setColor.
     */
    public Color getColor() {
        return color;
    }

    /**
     * Sets the color of the series.
     * @param color the color this feature should always return
     */
    public void setColor(Color color) {
        this.color = color;
    }

    /**
     * Should the series be protrayed as continuous? Used for time series.
     */
    public boolean isContinuous() {
        return continuous;
    }

    /**
     * Sets whether the series is protrayed as continuous.
     * @param continuous
     */
    public void setContinuous(boolean continuous) {
        this.continuous = continuous;
    }
}
