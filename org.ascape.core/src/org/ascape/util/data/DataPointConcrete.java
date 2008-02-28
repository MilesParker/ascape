/*
 * Copyright 1998-2007 The Brookings Institution, NuTech Solutions,Inc., Metascape LLC, and contributors. 
 * All rights reserved.
 * This program and the accompanying materials are made available solely under the BSD license "ascape-license.txt".
 * Any referenced or included libraries carry licenses of their respective copyright holders. 
 */

package org.ascape.util.data;

import java.io.Serializable;
import java.util.Collection;
import java.util.Iterator;

/**
 * An class providing a data point for a given object.
 * A data point is just some interpretation of an object's state as a double value.
 *
 * @author Miles Parker
 * @version 2.0
 * @history 2.0 6/6/2001 added equals features
 * @history 1.0.1 3/9/1999 renamed from ValueSource
 * @since 1.0
 */
public abstract class DataPointConcrete implements DataPoint, Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    /**
     * The name of the data point.
     */
    private String name;

    /**
     * Constructs a concrete instantiation of a color feature.
     */
    public DataPointConcrete() {
    }

    /**
     * Constructs a concrete instantiation of a color feature with the supplied name.
     * @param name the user relevant name of the feature
     */
    public DataPointConcrete(String name) {
        this.name = name;
    }

    /**
     * Returns a name for the object as defined by set name.
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name of this data point.
     * @param name a user relevant name for this feature
     */
    public final void setName(String name) {
        this.name = name;
    }

    /**
     * Returns the value of a given data point from a given object.
     * @param object the object to extract the value from.
     */
    public abstract double getValue(Object object);

    /**
     * Calcualtes the total across the entire collection.
     * @param collection
     * @return
     */
    public double sum(Collection collection) {
        double sum = 0.0;
        for (Iterator iterator = collection.iterator(); iterator.hasNext();) {
            Object o = iterator.next();
            sum += getValue(o);
        }
        return sum;
    }

    public final static float equalsEpsilonFloat = 1.0e-5f;
    public final static double equalsEpsilon = 1.0e-10;

    /**
     * Return true if the objects as interprested by this datapoint are equal within some tolerance defined by epsilon (10^-9)
     */
    public boolean equals(Object o1, Object o2) {
        return equals(getValue(o1), getValue(o2));
    }

    /**
     * Return true if the objects as interprested by the supplied datapoint are equal within some tolerance defined by epsilon (10^-9)
     */
    public final static boolean equals(DataPoint dataPoint, Object o1, Object o2) {
        return equals(dataPoint.getValue(o1), dataPoint.getValue(o2));
    }

    /**
     * Return true if the values supplied are equal within some tolerance defined by equalsEpsilon
     */
    public final static boolean equals(double v1, double v2) {
        //special case, designed to catch where v1 and v2 are both 0.0, but will also improve general performance.
        if (v1 == v2) {
            return true;
        }
        //Short term fix, a little longer, but needed to take care of 0.00000001 == 0.0 case
        return ((Math.abs((v1 - v2) / (v1 + v2)) < equalsEpsilon) || (Math.abs(v1 - v2) < equalsEpsilon));
    }

    /**
     * Return true if the values supplied are equal within some tolerance defined by equalsEpsilonFloat
     */
    public final static boolean equals(float v1, float v2) {
        //special case, designed to catch where v1 and v2 are both 0.0, but will also improve general performance.
        if (v1 == v2) {
            return true;
        }
        //Short term fix, a little longer, but needed to take care of 0.00000001 == 0.0 case
        return ((Math.abs((v1 - v2) / (v1 + v2)) < equalsEpsilonFloat) || (Math.abs(v1 - v2) < equalsEpsilonFloat));
    }

    /**
     * Gets the equalsEpsilon for the DataValue class.
     *
     * @return   the equalsEpsilon
     */
    public static double getEqualsEpsilon() {
        return equalsEpsilon;
    }

    /**
     * Gets the equalsEpsilonFloat for the DataValue class.
     *
     * @return   the equalsEpsilonFloat
     */
    public static double getEqualsEpsilonFloat() {
        return equalsEpsilonFloat;
    }
}
