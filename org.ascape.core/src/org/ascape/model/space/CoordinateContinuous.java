/*
 * Copyright 1998-2007 The Brookings Institution, NuTech Solutions,Inc., Metascape LLC, and contributors. 
 * All rights reserved.
 * This program and the accompanying materials are made available solely under of the BSD license "ascape-license.txt".
 * Any referenced or included libraries carry licenses of their respective copyright holders. 
 */

package org.ascape.model.space;

import java.io.Serializable;

import org.ascape.util.data.DataPointConcrete;


/**
 * The base class for a location within a continuous space.
 * 
 * @author Miles Parker
 * @version 2.0
 * @history 2.0 9/10/01 First in
 * @since 2.0
 */
public abstract class CoordinateContinuous extends Coordinate implements Cloneable, Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    /**
     * Each element of this array represents the position of this coordinate
     * within a dimension, the index of the element.
     */
    protected double values[];

    /**
     * Creates a new discrete coordinate.
     */
    public CoordinateContinuous() {
        super();
    }

    /**
     * Creates a new coordinate with the provided dimensional positions.
     * 
     * @param values
     *            the values
     */
    public CoordinateContinuous(double[] values) {
        //this.geometry = geometry;
        this.values = values;
    }

    /**
     * Creates a new coordinate with the provided position.
     * 
     * @param value
     *            the value
     */
    public CoordinateContinuous(double value) {
        //this.geometry = geometry;
        values = new double[1];
        values[0] = value;
    }

    /**
     * Returns the number of dimensions this coordinate has.
     * 
     * @return the dimension count
     */
    public int getDimensionCount() {
        return values.length;
    }

    /**
     * Returns an array of position values for each dimension.
     * 
     * @return the values
     */
    final public double[] getValues() {
        return values;
    }

    /**
     * Sets the position values for this coordinate.
     * 
     * @param values
     *            the values
     */
    final public void setValues(double[] values) {
        this.values = values;
    }

    /**
     * Gets the value at the specified (1-based) dimension.
     * 
     * @param dimension
     *            the dimension
     * @return the value at dimension
     */
    final public double getValueAtDimension(int dimension) {
        return values[dimension - 1];
    }

    /**
     * Sets the value at the specified (1-based) dimension.
     * 
     * @param dimension
     *            the dimension
     * @param value
     *            the value
     */
    final public void setValueAtDimension(int dimension, double value) {
        this.values[dimension - 1] = value;
    }

    /**
     * Returns the product of all the dimension's values. (A measure of volume
     * of the region enclosed by {0, ..., n} and this coordinate.)
     * 
     * @return the product
     */
    final public double getProduct() {
        int size = 0;
        for (int i = 0; i < values.length; i++) {
            size *= values[i];
        }
        return size;
    }

    /**
     * Determines whether this coordinate is equal to another. Does it have the
     * same values?
     * 
     * @param obj
     *            the obj
     * @return true, if equals
     */
    public boolean equals(Object obj) {
        if ((obj instanceof CoordinateContinuous) && (values.length == (((CoordinateContinuous) obj).values.length))) {
            for (int i = 0; i < values.length; i++) {
                if (!DataPointConcrete.equals(values[i], ((CoordinateContinuous) obj).values[i])) {
                    return false;
                }
            }
            return true;
        }
        //test for same class and dimensionality failed
        return false;
    }

    /**
     * Returns a string representing this coordinate.
     * 
     * @return the string
     */
    public String toString() {
        //Language: Change to "an" for eight, eleven... dimensional LatticeDiscretes?
        String desc = "Coordinate with values: [";
        for (int i = 0; i < values.length; i++) {
            desc += values[i];
            if (i < values.length - 1) {
                desc += ", ";
            }
        }
        desc += values.length + "-dimensional geometry.";
        return desc;
    }
}
