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
 * supplied UnitIntervalDataPoint. The data point is bracketed, so values outside
 * of the range 0.0 - 1.0 will be treeated as minimum and maximum respectivly.
 *
 * @author Miles Parker
 * @version 1.2
 * @history 1.2 7/8/99 added more constructor options (including name) to simplify subclassing
 * @history 1.0.1 3/13/99 renamed from GradiatedColorSource
 * @since 1.0
 */
public class ColorFeatureGradiated extends ColorFeatureConcrete {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    /**
     * The minimum value of the red component of this color.
     */
    protected float redMinimum;

    /**
     * The minimum value of the green component of this color.
     */
    protected float greenMinimum;

    /**
     * The minimum value of the blue component of this color.
     */
    protected float blueMinimum;

    /**
     * The maximum value of the red component of this color.
     */
    protected float redMaximum;

    /**
     * The maximum value of the green component of this color.
     */
    protected float greenMaximum;

    /**
     * The maximum value of the blue component of this color.
     */
    protected float blueMaximum;

    /**
     * The base value of the red component of this color.
     */
    protected float redBase;

    /**
     * The base value of the green component of this color.
     */
    protected float greenBase;

    /**
     * The base value of the blue component of this color.
     */
    protected float blueBase;

    /**
     * The unit interval data point used to calculate the gradiated color.
     */
    protected UnitIntervalDataPoint dataPoint;

    /**
     * Constructs a ColorFeatureGradiated.
     */
    public ColorFeatureGradiated() {
        setMinimumColor(Color.white);
        setMaximumColor(Color.yellow);
    }

    /**
     * Constructs the gradiated color feature with the supplied name.
     * @param name the name of this draw feature
     */
    public ColorFeatureGradiated(String name) {
        super(name);
        setMinimumColor(Color.white);
        setMaximumColor(Color.yellow);
    }

    /**
     * Constructs a ColorFeatureGradiated with the supplied unit interval and maximum color.
     * @param maximumColor the color to be returned at unit maximum
     * @param dataPoint a unit interval data point (0..1) providing the relative intensity of a color for a given object
     */
    public ColorFeatureGradiated(Color maximumColor, UnitIntervalDataPoint dataPoint) {
        setDataPoint(dataPoint);
        setMinimumColor(Color.white);
        setMaximumColor(maximumColor);
    }

    /**
     * Constructs a ColorFeatureGradiated with the supplied name, maximum color, and unit interval.
     * @param name the name of this color feature
     * @param maximumColor the color to be returned at unit maximum
     * @param dataPoint a unit interval data point (0..1) providing the relative intensity of a color for a given object
     */
    public ColorFeatureGradiated(String name, Color maximumColor, UnitIntervalDataPoint dataPoint) {
        super(name);
        setDataPoint(dataPoint);
        setMinimumColor(Color.white);
        setMaximumColor(maximumColor);
    }

    /**
     * Constructs a ColorFeatureGradiated with the supplied name, maximum color, and unit interval.
     * @param name the name of this color feature
     * @param minimumColor the color to be returned at unit minimum
     * @param maximumColor the color to be returned at unit maximum
     * @param dataPoint a unit interval data point (0..1) providing the relative intensity of a color for a given object
     */
    public ColorFeatureGradiated(String name, Color maximumColor, Color minimumColor, UnitIntervalDataPoint dataPoint) {
        super(name);
        setDataPoint(dataPoint);
        setMinimumColor(minimumColor);
        setMaximumColor(maximumColor);
    }

    /**
     * Returns the data point used to calculate color.
     * (As an alternative to setting this value, you may override with a method returning a data point.)
     */
    public UnitIntervalDataPoint getDataPoint() {
        return dataPoint;
    }

    /**
     * Sets a unit data point that will return some number between 0.0 and 1.0
     */
    public void setDataPoint(UnitIntervalDataPoint dataPoint) {
        this.dataPoint = dataPoint;
    }

    /**
     * Returns the name of the gradiated color feature.
     * Override to provide a different name.
     */
    public String getName() {
        if (name == null) {
            return name;
        } else {
            return dataPoint.getName();
        }
    }

    /**
     * Returns maximum color at intensity defined by the data point.
     * @param object the object to get a color from.
     */
    public Color getColor(Object object) {
        float agentColor = (float) dataPoint.getBracketedValue(object);
        float r = redMinimum + (agentColor * (redMaximum - redMinimum));
        float g = greenMinimum + (agentColor * (greenMaximum - greenMinimum));
        float b = blueMinimum + (agentColor * (blueMaximum - blueMinimum));
        return new Color(r, g, b);
    }

    /**
     * Returns maximum color at intensity defined by the data point.
     * @param object the object to get a color from.
     */
    public Color getColor(Object object, float alpha) {
        float agentColor = (float) dataPoint.getBracketedValue(object);
        float r = redMinimum + (agentColor * (redMaximum - redMinimum));
        float g = greenMinimum + (agentColor * (greenMaximum - greenMinimum));
        float b = blueMinimum + (agentColor * (blueMaximum - blueMinimum));
        return new Color(r, g, b, alpha);
    }

    /**
     * Calculates the values to be used for quickly returning a color within graidient.
     */
    private void calculateColorValues() {
        if (redMinimum < redMaximum) {
        } else {
        }
        if (blueMinimum < blueMaximum) {
        } else {
        }
        if (greenMinimum < greenMaximum) {
        } else {
        }
    }

    /**
     * Sets the color at minimum intensity; that is, the color that
     * would be returned if the unit value were 0.0.
     */
    public void setMinimumColor(Color valueColor) {
        redMinimum = (float) valueColor.getRed() / 255.0F;
        greenMinimum = (float) valueColor.getGreen() / 255.0F;
        blueMinimum = (float) valueColor.getBlue() / 255.0F;
        calculateColorValues();
    }

    /**
     * Sets the color at maximum intensity; that is, the color that
     * would be returned if the unit value were 1.0.
     */
    public void setMaximumColor(Color valueColor) {
        redMaximum = (float) valueColor.getRed() / 255.0F;
        greenMaximum = (float) valueColor.getGreen() / 255.0F;
        blueMaximum = (float) valueColor.getBlue() / 255.0F;
        calculateColorValues();
    }

    /**
     * Clones this feature.
     */
    public Object clone() {
        try {
            ColorFeatureGradiated clone = (ColorFeatureGradiated) super.clone();
            //clone.dataPoint = (UnitIntervalDataPoint) this.dataPoint.clone();
            return clone;
        } catch (CloneNotSupportedException e) {
            // this shouldn't happen, since we are Cloneable
            throw new InternalError();
        }
    }
}
