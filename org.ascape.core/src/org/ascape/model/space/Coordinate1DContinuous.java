/*
 * Copyright 1998-2007 The Brookings Institution, NuTech Solutions,Inc., Metascape LLC, and contributors. 
 * All rights reserved.
 * This program and the accompanying materials are made available solely under of the BSD license "ascape-license.txt".
 * Any referenced or included libraries carry licenses of their respective copyright holders. 
 */

package org.ascape.model.space;

/**
 * A location within a one-dimensional discrete space.
 * 
 * @author Miles Parker
 * @version 2.0
 * @history 2.0 9/10/01 First in
 * @since 2.0
 */
public class Coordinate1DContinuous extends CoordinateContinuous {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    /**
     * Creates a 1-dimensional coordinate.
     */
    public Coordinate1DContinuous() {
        super();
    }

    /**
     * Creates a 1-dimensional coordinate with the specified postion values.
     * 
     * @param values
     *            the values
     */
    public Coordinate1DContinuous(double[] values) {
        super(values);
    }

    /**
     * Creates a 1-dimensional coordinate with the specified position.
     * 
     * @param value
     *            the value
     */
    public Coordinate1DContinuous(double value) {
        super();
        values = new double[1];
        values[0] = value;
    }

    /**
     * Returns the distance between two coordinates. Note: Expects non-periodic
     * (i.e. not non-wrap around) space, since there is no space context.
     * Coordinates are not aware of a particular space context, so the agent and
     * space getDistance methods should be used whenever comparing distance in
     * the context of scapes.
     * 
     * @param coordinate
     *            target to calcualte distance to
     * @return the distance between the two coordinates
     */
    public double getDistance(Coordinate coordinate) {
        return Math.min(Math.abs(this.values[0] - ((Coordinate2DContinuous) coordinate).getXValue()),
            Math.abs(((Coordinate2DContinuous) coordinate).getXValue()) - this.values[0]);
    }

    /**
     * Returns the number of dimensions for this lattice.
     * 
     * @return the value
     */
    public double getValue() {
        return values[0];
    }

    /**
     * Sets the value for this coordinate.
     * 
     * @param x
     *            the x
     */
    public void setValue(double x) {
        values[0] = x;
    }

    /**
     * Returns the x value of this coordinate.
     * 
     * @return the x value
     */
    public double getXValue() {
        return values[0];
    }

    /**
     * Sets the y value for this coordinate.
     * 
     * @param x
     *            the x
     */
    public void setXValue(double x) {
        values[0] = x;
    }

    /**
     * Adds the location of the supplied coordinate to this coordinate,
     * returning the resulting coordinate.
     * 
     * @param coordinate
     *            the coordinate to sum with this one
     * @return the coordinate
     */
    public Coordinate add(Coordinate coordinate) {
        return new Coordinate1DContinuous(getXValue() + ((Coordinate1DContinuous) coordinate).getXValue());
    }

    /**
     * Returns a string representing this coordinate.
     * 
     * @return the string
     */
    public String toString() {
        return "[" + getXValue() + "]";
    }
}
