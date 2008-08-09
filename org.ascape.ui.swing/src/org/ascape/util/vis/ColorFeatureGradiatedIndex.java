/*
 * Copyright 1998-2007 The Brookings Institution, NuTech Solutions,Inc., Metascape LLC, and contributors. 
 * All rights reserved.
 * This program and the accompanying materials are made available solely under the BSD license "ascape-license.txt".
 * Any referenced or included libraries carry licenses of their respective copyright holders. 
 */

package org.ascape.util.vis;

import java.awt.Color;

import org.ascape.util.data.UnitIntervalDataPoint;


/**
 * A class for displaying a gradiated color whose intensity is determined by the
 * supplied UnitIntervalDataPoint. This implementation keeps an array of one thousand
 * colors that are created dynamically and then referred to as needed, improving
 * performance significantly over creating new colors for each value.
 * <p><b>Important</b>: To obtain maximum performance, the value is not bracketed,
 * that is, this class uses the 'raw' value obtained from the UnitIntervalDataPoint.
 * If that value is not between 0.0-1.0 inclusive, a RuntimeException
 * exception <i>will</i> be thrown. If you are not sure that your value will always
 * be clean, use ColorFeatureGradiated. If there is demand, I'll create a version of
 * this class that does use the bracketed value.
 *
 * @author Miles Parker
 * @version 1.9.1
 * @history 1.9.1 10/16/2000
 * @history 1.2.5 10/1/1999 first in
 */
public class ColorFeatureGradiatedIndex extends ColorFeatureGradiated {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    private Color[] indexedColors = new Color[1001];

    public ColorFeatureGradiatedIndex(String name) {
        super(name);
    }

    /**
     * Constructs a ColorFeatureGradiated with the supplied name, maximum color, and unit interval.
     * @param name the name of this color feature
     * @param maximumColor the color to be returned at unit maximum
     * @param dataPoint a unit interval data point (0..1) providing the relative intensity of a color for a given object
     */
    public ColorFeatureGradiatedIndex(String name, Color maximumColor, UnitIntervalDataPoint dataPoint) {
        super(name, maximumColor, dataPoint);
    }

    /**
     * Constructs a ColorFeatureGradiated with the supplied name, maximum color, and unit interval.
     * @param name the name of this color feature
     * @param minimumColor the color to be returned at unit minimum
     * @param maximumColor the color to be returned at unit maximum
     * @param dataPoint a unit interval data point (0..1) providing the relative intensity of a color for a given object
     */
    public ColorFeatureGradiatedIndex(String name, Color maximumColor, Color minimumColor, UnitIntervalDataPoint dataPoint) {
        super(name, maximumColor, minimumColor, dataPoint);
    }

    /**
     * Returns maximum color at intensity defined by the data point.
     * @param object the object to get a color from.
     */
    public Color getColor(Object object) {
        float val = (float) dataPoint.getValue(object);
        try {
            Color c = indexedColors[(int) (val * 1000)];
            if (c == null) {
                float agentColor = val;
                float r = redMinimum + (agentColor * (redMaximum - redMinimum));
                float g = greenMinimum + (agentColor * (greenMaximum - greenMinimum));
                float b = blueMinimum + (agentColor * (blueMaximum - blueMinimum));
                c = new Color(r, g, b);
                indexedColors[(int) (val * 1000)] = c;
            }
            return c;
        } catch (ArrayIndexOutOfBoundsException e) {
            throw new RuntimeException("An error occurred while drawing a color feature; Gradiated Index Value not in [0.0, 1.0]: " + val);
        }
    }
}
