/*
 * Copyright 1998-2007 The Brookings Institution, NuTech Solutions,Inc., Metascape LLC, and contributors.
 * All rights reserved.
 * This program and the accompanying materials are made available solely under of the BSD license "ascape-license.txt".
 * Any referenced or included libraries carry licenses of their respective copyright holders.
 */

package org.ascape.model.space;

/**
 * A location within a two-dimensional continuous space.
 * 
 * @author Miles Parker
 * @version 2.0
 * @history 2.0 9/10/01 First in
 * @since 2.0
 */
public class Coordinate2DContinuous extends Coordinate1DContinuous {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    /**
     * Creates a 2-dimensional coordinate with the specified position values.
     * 
     * @param values
     *            the values
     */
    public Coordinate2DContinuous(double[] values) {
        super(values);
    }

    /**
     * Creates a 2-dimensional coordinate with the specified position values.
     * 
     * @param x
     *            the x
     * @param y
     *            the y
     */
    public Coordinate2DContinuous(double x, double y) {
        super();
        values = new double[2];
        values[0] = x;
        values[1] = y;
    }

    /**
     * Creates a 2-dimensional coordinate with the specified position values.
     * 
     * @param intValues
     *            the int values
     */
    public Coordinate2DContinuous(int[] intValues) {
        super();
        values = new double[2];
        values[0] = intValues[0];
        values[1] = intValues[1];
    }

    /**
     * Returns the y value of this coordinate.
     * 
     * @return the y value
     */
    public double getYValue() {
        return values[1];
    }

    /**
     * Sets the y value for this coordinate.
     * 
     * @param y
     *            the y
     */
    public void setYValue(double y) {
        values[1] = y;
    }

    /**
     * Returns the calculated distance between this coordinate and the supplied
     * coordinate. Note: Expects non-periodic (i.e. not non-wrap around) space,
     * since there is no space context. Coordinates are not aware of a
     * particular space context, so the agent and space getDistance methods
     * should be used whenever comparing distance in the context of scapes.
     * 
     * @param target
     *            the location to calculate distance to
     * @return the distance between the two coordinates
     */
    public double getDistance(Coordinate target) {
        double minXDistance = Math.abs(this.values[0] - ((Coordinate2DContinuous) target).getXValue());
        double minYDistance = Math.abs(this.values[1] - ((Coordinate2DContinuous) target).getYValue());
        return Math.sqrt(minXDistance * minXDistance + minYDistance * minYDistance);
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
        return new Coordinate2DContinuous(getXValue() + ((Coordinate2DContinuous) coordinate).getXValue(), getYValue() + ((Coordinate2DContinuous) coordinate).getYValue());
    }

    /**
     * Returns a string representing this coordinate.
     * 
     * @return the string
     */
    public String toString() {
        return "[" + getXValue() + ", " + getYValue() + "]";
    }
}
