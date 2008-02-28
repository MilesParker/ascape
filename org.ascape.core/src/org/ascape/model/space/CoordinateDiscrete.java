/*
 * Copyright 1998-2007 The Brookings Institution, NuTech Solutions,Inc., Metascape LLC, and contributors. 
 * All rights reserved.
 * This program and the accompanying materials are made available solely under of the BSD license "ascape-license.txt".
 * Any referenced or included libraries carry licenses of their respective copyright holders. 
 */

package org.ascape.model.space;

import java.io.Serializable;
import java.util.Arrays;

/**
 * The base class for a location within a regular discrete space.
 * 
 * @author Miles Parker
 * @version 1.2
 * @history 1.2 6/4/99 Removed geometry as a member variable and changed
 *          constructors to reflect
 * @history 1.0 first in 1.0
 * @since 1.0
 */
public abstract class CoordinateDiscrete extends Coordinate implements Cloneable, Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    /**
     * Each element of this array represents the position of this coordinate
     * within a dimension, the index of the element.
     */
    protected int values[];

    /**
     * Creates a new discrete coordinate.
     */
    public CoordinateDiscrete() {
        super();
    }

    /**
     * Creates a new coordinate with the provided dimensional positions.
     * 
     * @param values
     *            the values
     */
    public CoordinateDiscrete(int[] values) {
        //this.geometry = geometry;
        this.values = values;
    }

    /**
     * Creates a new coordinate with the provided position.
     * 
     * @param value
     *            the value
     */
    public CoordinateDiscrete(int value) {
        //this.geometry = geometry;
        values = new int[1];
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
    final public int[] getValues() {
        return values;
    }

    /**
     * Sets the position values for this coordinate.
     * 
     * @param values
     *            the values
     */
    final public void setValues(int[] values) {
        this.values = values;
    }

    /**
     * Gets the value at the specified (1-based) dimension.
     * 
     * @param dimension
     *            the dimension
     * @return the value at dimension
     */
    final public int getValueAtDimension(int dimension) {
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
    final public void setValueAtDimension(int dimension, int value) {
        this.values[dimension - 1] = value;
    }

    /**
     * Returns the product of all the dimension's values. (A measure of volume
     * of the region enclosed by {0, ..., n} and this coordinate.)
     * 
     * @return the product
     */
    final public int getProduct() {
//        int size = 0;
//        for (int i=0; i < values.length; i++) {
//            size *= values[i];
//        }
        int size = values[0];
        for (int i = 1; i < values.length; i++) {
            size *= values[i];
        }
        return size;
    }

    /**
     * Determines whether this coordinate is equal to another. Does it have the
     * same values?
     * 
     * @param o
     *            the o
     * @return true, if equals
     */
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CoordinateDiscrete)) return false;

        final CoordinateDiscrete coordinateDiscrete = (CoordinateDiscrete) o;

        if (!Arrays.equals(values, coordinateDiscrete.values)) return false;

        return true;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    public int hashCode() {
        int hash = 0;
        for (int i = 0; i < values.length; i++) {
            hash ^= values[i];
            hash = hash << 2;
        }
        return hash;
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
